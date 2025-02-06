/*
Copyright 2025 Adobe
All Rights Reserved.

NOTICE: Adobe permits you to use, modify, and distribute this file in
accordance with the terms of the Adobe license agreement accompanying
it.
*/
package com.adobe.mkto.assetcopy.service;


import com.adobe.mkto.assetcopy.client.LandingPageAPIClient;
import com.adobe.mkto.assetcopy.model.FolderId;
import com.adobe.mkto.assetcopy.model.LandingPageTemplate;
import com.adobe.mkto.assetcopy.model.LandingPageTemplateContent;
import com.adobe.mkto.assetcopy.model.request.CreateLPTemplateRequest;
import com.adobe.mkto.assetcopy.model.request.UpdateLPTemplateContentRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.nio.file.Path;
import java.util.List;

import static com.adobe.mkto.assetcopy.constant.AssetCopyConstants.APPROVED;

@Component
@RequiredArgsConstructor
public class CopyLandingPageTemplateService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CopyLandingPageTemplateService.class);

    private final LandingPageAPIClient sourceLandingPageAPIClient;
    private final LandingPageAPIClient destinationLandingPageAPIClient;
    private final CopyFileService copyFileService;

    public void copyTemplate(Integer sourceTemplateId, FolderId destinationFolder) {
        LOGGER.debug("Copying template {}", sourceTemplateId);
        List<LandingPageTemplate> sourceTemplateResponse = sourceLandingPageAPIClient.getTemplateById(sourceTemplateId, APPROVED);
        if (sourceTemplateResponse == null || sourceTemplateResponse.isEmpty()) {
            return;
        }
        LandingPageTemplate sourceTemplate = sourceTemplateResponse.getFirst();

        List<LandingPageTemplate> destinationTemplateResponse = destinationLandingPageAPIClient.getTemplateByName(sourceTemplate.getName(), null);

        LandingPageTemplate destinationTemplate;
        if (destinationTemplateResponse == null || destinationTemplateResponse.isEmpty()) {

            CreateLPTemplateRequest createRequest = CreateLPTemplateRequest.builder()
                    .templateType(sourceTemplate.getTemplateType())
                    .enableMunchkin(sourceTemplate.getEnableMunchkin())
                    .name(sourceTemplate.getName())
                    .description(sourceTemplate.getDescription())
                    .folder(destinationFolder)
                    .build();

            destinationTemplate = destinationLandingPageAPIClient.createTemplate(createRequest);
        }
        else {
            destinationTemplate = destinationTemplateResponse.getFirst();
        }

        if (!APPROVED.equalsIgnoreCase(destinationTemplate.getStatus())) {

            updateTemplateContent(sourceTemplate, destinationTemplate);
            destinationLandingPageAPIClient.approveTemplate(destinationTemplate.getId());

            LOGGER.debug("Successfully copied and approved LP template {} to destination folderId {}",
                    sourceTemplate.getName(), destinationFolder.getId());
        }
    }

    public void updateTemplateContent(LandingPageTemplate sourceTemplate, LandingPageTemplate destinationTemplate) {

        LandingPageTemplateContent sourceTemplateContent = sourceLandingPageAPIClient.getTemplateContentById(
                sourceTemplate.getId(), APPROVED);

        if (StringUtils.hasLength(sourceTemplateContent.getContent())) {

            Path contentFilePath = copyFileService.createTempFile(sourceTemplate.getName() + ".html", null,
                    sourceTemplateContent.getContent().getBytes());
            FileSystemResource contentFileResource = new FileSystemResource(contentFilePath);

            UpdateLPTemplateContentRequest updateLPTemplateContentRequest = UpdateLPTemplateContentRequest.builder()
                    .content(contentFileResource)
                    .build();
            destinationLandingPageAPIClient.updateTemplateContent(destinationTemplate.getId(), updateLPTemplateContentRequest);
        }
    }
}

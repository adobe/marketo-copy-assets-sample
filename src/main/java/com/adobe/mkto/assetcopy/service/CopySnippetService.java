/*
Copyright 2025 Adobe
All Rights Reserved.

NOTICE: Adobe permits you to use, modify, and distribute this file in
accordance with the terms of the Adobe license agreement accompanying
it.
*/
package com.adobe.mkto.assetcopy.service;

import com.adobe.mkto.assetcopy.client.SnippetAPIClient;
import com.adobe.mkto.assetcopy.model.FolderId;
import com.adobe.mkto.assetcopy.model.Snippet;
import com.adobe.mkto.assetcopy.model.SnippetContent;
import com.adobe.mkto.assetcopy.model.request.CreateSnippetRequest;
import com.adobe.mkto.assetcopy.model.request.UpdateSnippetContentRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import static com.adobe.mkto.assetcopy.constant.AssetCopyConstants.APPROVED;

@Component
@RequiredArgsConstructor
public class CopySnippetService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CopySnippetService.class);
    private final SnippetAPIClient sourceSnippetAPIClient;
    private final SnippetAPIClient destinationSnippetAPIClient;


    public void copySnippet(Integer sourceSnippetId, FolderId destinationFolder)  {
        LOGGER.debug("Copying Snippet {}", sourceSnippetId);
        // Grab the source Snippet by ID
        Snippet sourceSnippet = sourceSnippetAPIClient.getById(sourceSnippetId, APPROVED).getFirst();

        // Using the name from the source Snippet check if it exists in Destination
        Snippet destinationSnippet = destinationSnippetAPIClient.getByName(sourceSnippet.getName());

        if (destinationSnippet == null) {
            CreateSnippetRequest createRequest = CreateSnippetRequest.builder()
                    .name(sourceSnippet.getName())
                    .description(sourceSnippet.getDescription())
                    .folder(destinationFolder)
                    .build();

            destinationSnippet = destinationSnippetAPIClient.createSnippet(createRequest);
        }

        SnippetContent sourceSnippetContent = sourceSnippetAPIClient.getContentById(sourceSnippetId, APPROVED);
        if (sourceSnippetContent.getType() != null && StringUtils.hasLength(sourceSnippetContent.getContent())) {
            UpdateSnippetContentRequest updateSnippetContentRequest = UpdateSnippetContentRequest.builder()
                    .id(destinationSnippet.getId())
                    .type(sourceSnippetContent.getType())
                    .content(sourceSnippetContent.getContent())
                    .build();

            destinationSnippetAPIClient.updateSnippetContent(destinationSnippet.getId(), updateSnippetContentRequest);
        }

        destinationSnippetAPIClient.approveSnippet(destinationSnippet.getId());
    }

    public Snippet getDestinationSnippetBySourceId(Integer sourceSnippetId)  {
        Snippet sourceSnippet = sourceSnippetAPIClient.getById(sourceSnippetId, APPROVED).getFirst();
        return destinationSnippetAPIClient.getByName(sourceSnippet.getName());
    }
}

/*
Copyright 2025 Adobe
All Rights Reserved.

NOTICE: Adobe permits you to use, modify, and distribute this file in
accordance with the terms of the Adobe license agreement accompanying
it.
*/
package com.adobe.mkto.assetcopy.service;

import com.adobe.mkto.assetcopy.client.LandingPageAPIClient;
import com.adobe.mkto.assetcopy.client.ProgramAPIClient;
import com.adobe.mkto.assetcopy.constant.ContentType;
import com.adobe.mkto.assetcopy.exception.AssetCopyRuntimeException;
import com.adobe.mkto.assetcopy.model.File;
import com.adobe.mkto.assetcopy.model.FolderId;
import com.adobe.mkto.assetcopy.model.Form;
import com.adobe.mkto.assetcopy.model.LandingPage;
import com.adobe.mkto.assetcopy.model.LandingPageContent;
import com.adobe.mkto.assetcopy.model.LandingPageTemplate;
import com.adobe.mkto.assetcopy.model.LandingPageVariable;
import com.adobe.mkto.assetcopy.model.Snippet;
import com.adobe.mkto.assetcopy.model.request.CreateLandingPageRequest;
import com.adobe.mkto.assetcopy.model.request.LandingPageContentSectionRequest;
import com.adobe.mkto.assetcopy.model.request.UpdateLandingPageVariableRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.adobe.mkto.assetcopy.constant.AssetCopyConstants.APPROVED;
import static com.adobe.mkto.assetcopy.util.AssetCopyUtils.parseTimeString;

@Component
@RequiredArgsConstructor
public class CopyLandingPageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CopyLandingPageService.class);
    private static final String BORDER_COLOR = "borderColor";
    private static final String DEFAULT_BORDER_COLOR = "forestgreen";
    private static final String DEFAULT_COLOR_CODE = "#228B22";

    private final LandingPageAPIClient sourceLandingPageAPIClient;
    private final LandingPageAPIClient destinationLandingPageAPIClient;
    private final CopyLandingPageTemplateService copyLandingPageTemplateService;
    private final ProgramAPIClient sourceProgramAPIClient;
    private final CopySnippetService copySnippetService;
    private final CopyFormService copyFormService;
    private final CopyFileService copyFileService;

    public LandingPage copyLandingPage(LandingPage sourceLP, FolderId destinationLPFolder, FolderId destinationFormFolder,
                                       FolderId destinationImageFolder, boolean usePrefix, String urlFormat) {

        LOGGER.debug("Copying Landing Page with ID: {}", sourceLP.getId());

        LandingPageTemplate sourceTemplate = sourceLandingPageAPIClient
                .getTemplateById(sourceLP.getTemplate(), APPROVED).getFirst();

        List<LandingPageTemplate> destinationLPTemplateResponse =
                destinationLandingPageAPIClient.getTemplateByName(sourceTemplate.getName(), "approved");

        if (destinationLPTemplateResponse == null || destinationLPTemplateResponse.isEmpty()) {
            throw new AssetCopyRuntimeException(String.format("LP Template does not exist in destination or is not approved. " +
                    "Template needs to be created or approved. Source template name: %s id: %d", sourceTemplate.getName(), sourceTemplate.getId()));
        }

        LandingPageTemplate destinationTemplate = destinationLPTemplateResponse.getFirst();

        if (parseTimeString(sourceTemplate.getUpdatedAt()).after(parseTimeString(destinationTemplate.getUpdatedAt()))) {
            copyLandingPageTemplateService.updateTemplateContent(sourceTemplate, destinationTemplate);
            destinationLandingPageAPIClient.approveTemplate(destinationTemplate.getId());
        }

        String namePrefix = sourceLP.getProgramId() != null
                ? sourceProgramAPIClient.getById(sourceLP.getProgramId()).getName()
                : null;

        String pageName = buildDestinationPageName(sourceLP, namePrefix, usePrefix);
        List<LandingPage> destinationLPResponse = destinationLandingPageAPIClient.getByName(pageName, null);

        LandingPage destinationLP;

        if (!CollectionUtils.isEmpty(destinationLPResponse)) {
            destinationLP = destinationLPResponse.getFirst();
            if (parseTimeString(destinationLP.getUpdatedAt()).after(parseTimeString(sourceLP.getUpdatedAt()))) {
                LOGGER.info("Landing Page already exists and is updated with latest content");
                LOGGER.info("source: id - {}, name - {}. destination: id - {}, name - {}",
                        sourceLP.getId(), sourceLP.getName(), destinationLP.getId(), destinationLP.getName());
                return destinationLP;
            }
        }
        else {
            CreateLandingPageRequest createRequest = CreateLandingPageRequest.builder()
                    .name(pageName)
                    .title(sourceLP.getTitle())
                    .description(sourceLP.getDescription())
                    .facebookOgTags(sourceLP.getFacebookOgTags())
                    .customHeadHTML(sourceLP.getCustomHeadHtml())
                    .keywords(sourceLP.getKeywords())
                    .mobileEnabled(sourceLP.getMobileEnabled())
                    .prefillForm(sourceLP.getFormPrefill())
                    .robots(sourceLP.getRobots())
                    .template(destinationTemplate.getId())
                    .folder(destinationLPFolder)
                    .urlPageName(urlFormat)
                    .build();

            destinationLP = destinationLandingPageAPIClient.create(createRequest);
        }

        copyContents(sourceLP.getId(), destinationLP, destinationTemplate.getTemplateType(), destinationFormFolder, destinationImageFolder, namePrefix);

        copyLandingPageVariables(sourceLP, destinationLP);

        LOGGER.debug("Successfully copied over Landing Page: {}", sourceLP.getName());

        return destinationLP;
    }

    public void discardLandingPage(LandingPage sourceLP, boolean usePrefix) {
        String namePrefix = sourceLP.getProgramId() != null
                ? sourceProgramAPIClient.getById(sourceLP.getProgramId()).getName()
                : null;

        String pageName = buildDestinationPageName(sourceLP, namePrefix, usePrefix);
        List<LandingPage> destinationLPResponse = destinationLandingPageAPIClient.getByName(pageName, null);
        if (!CollectionUtils.isEmpty(destinationLPResponse)) {
            destinationLandingPageAPIClient.discardDraft(destinationLPResponse.getFirst().getId());
        }
    }

    private void copyContents(Integer sourceId, LandingPage destinationLP, String templateType,
                              FolderId destinationFormFolder, FolderId destinationImageFolder, String namePrefix) {
        LOGGER.debug("Copying content of Landing Page with ID: {}", sourceId);

        List<LandingPageContent> lpContents = sourceLandingPageAPIClient.getContentsById(sourceId);

        if (lpContents != null) {

            lpContents.forEach(lpContent -> {
                ContentType contentType = ContentType.fromValue(lpContent.getType());
                Object contentObject = lpContent.getContent();
                String contentId = contentObject instanceof Map<?,?> contentObjectProperties
                    ? contentObjectProperties.get("content").toString()
                    : null;

                Object destinationContentValue = contentId == null
                    ? lpContent.getContent()
                    : switch (contentType) {
                        case SNIPPET -> {
                            Snippet destinationSnippet;
                            destinationSnippet = copySnippetService.getDestinationSnippetBySourceId(Integer.parseInt(contentId));
                            if (destinationSnippet == null) {
                                throw new AssetCopyRuntimeException(String.format("Snippet does not exist in destination. " +
                                        "Need to create snippet from Source with id %s", contentId));
                            }
                            yield destinationSnippet.getId();
                        }
                        case FORM -> {
                            Form destinationForm = copyFormService.copyForm(Integer.parseInt(contentId),
                                    destinationFormFolder, namePrefix);
                            yield destinationForm.getId();
                        }
                        case IMAGE -> {
                            File destinationFile = copyFileService.copyFile(Integer.parseInt(contentId),
                                    destinationImageFolder, destinationLP.getWorkspace());
                            yield destinationFile.getId();
                        }
                    default -> lpContent.getContent();
                };

                LandingPageContentSectionRequest contentSectionRequest = LandingPageContentSectionRequest.builder()
                        .value(destinationContentValue)
                        .type(lpContent.getType())
                        .contentId(lpContent.getId().toString())
                        .build();

                updateLPContent(templateType, destinationContentValue, destinationLP, contentSectionRequest, lpContent);
            });
        }
    }

    private void updateLPContent(String templateType, Object destinationContentValue, LandingPage destinationLP,
                                 LandingPageContentSectionRequest contentSectionRequest, LandingPageContent lpContent) {
        if ("guided".equals(templateType)) {
            if (destinationContentValue != null) {
                destinationLandingPageAPIClient.updateLandingPageContent(destinationLP.getId(),
                        lpContent.getId().toString(), contentSectionRequest);
            }
        }
        else { //freeform
            processFormattingOptions(contentSectionRequest, lpContent.getFormattingOptions());
            destinationLandingPageAPIClient.addLandingPageContent(destinationLP.getId(), contentSectionRequest);
        }
    }

    private void processFormattingOptions(LandingPageContentSectionRequest request, Map<String, Object> formattingOptions) {

        if (formattingOptions != null) {

            formattingOptions.forEach((option, value) -> {

                if (value instanceof String string && string.contains("px")) {
                    value = string.substring(0, string.length() - 2);
                }
                // Checking for default color rectangle
                else if (BORDER_COLOR.equals(option) && DEFAULT_BORDER_COLOR.equals(value)) {
                    value = DEFAULT_COLOR_CODE;
                }

                formattingOptions.put(option, value);
            });

            BeanWrapper beanWrapper = new BeanWrapperImpl(request);
            formattingOptions.forEach(beanWrapper::setPropertyValue);
        }
    }

    public void createSitemap(FolderId folderId, List<LandingPage> landingPages) {
        LOGGER.debug("Creating sitemap for folder: {}", folderId);

        CreateLandingPageRequest createPageRequest = CreateLandingPageRequest.builder()
                .name("Sitemap")
                .folder(folderId)
                .template(1)
                .build();

        LandingPage sitemap = destinationLandingPageAPIClient.create(createPageRequest);

        List<LandingPage> sortedPages = landingPages.stream()
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(LandingPage::getTitle))
                .toList();

        List<List<LandingPage>> pagePartitions = partitionList(sortedPages, 300);
        int top = 0;

        for (List<LandingPage> subList : pagePartitions) {
            addSitemapContent(generateSitemapHtml(subList), sitemap.getId(), Integer.toString(top));
            top += 5000;
        }
    }

    public void updateExistingSitemap(Integer sitemapId, List<LandingPage> landingPages) {

        List<LandingPageContent> contents = destinationLandingPageAPIClient.getContentsById(sitemapId);
        if (!CollectionUtils.isEmpty(contents)) {
            contents.forEach(content -> destinationLandingPageAPIClient.deleteContent(sitemapId, content.getId().toString()));
        }

        List<LandingPage> sortedPages = landingPages.stream()
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(LandingPage::getTitle))
                .toList();

        List<List<LandingPage>> pagePartitions = partitionList(sortedPages, 300);
        int top = 0;

        for (List<LandingPage> subList : pagePartitions) {
            addSitemapContent(generateSitemapHtml(subList), sitemapId, Integer.toString(top));
            top += 5000;
        }
    }

    private List<List<LandingPage>> partitionList(List<LandingPage> pages, int partitionSize) {

        List<List<LandingPage>> result = new ArrayList<>();
        List<LandingPage> currentList = new ArrayList<>();

        for (LandingPage page : pages) {
            if (currentList.size() < partitionSize) {
                currentList.add(page);
            }
            else {
                result.add(currentList);
                currentList = new ArrayList<>();
            }
        }

        return result;
    }

    private void addSitemapContent(String html, Integer pageId, String top) {
        LandingPageContentSectionRequest addContentRequest = LandingPageContentSectionRequest.builder()
                .type("HTML")
                .value(html)
                .top(top)
                .build();

        destinationLandingPageAPIClient.addLandingPageContent(pageId, addContentRequest);
    }

    private String generateSitemapHtml(List<LandingPage> landingPages) {
        String sitemapHtml = landingPages.stream()
                .map(landingPage ->
                        "<li><a href=\"" +
                                (StringUtils.hasLength(landingPage.getUrl()) ? landingPage.getUrl() : landingPage.getComputedUrl()) +
                                "\">" +
                                (StringUtils.hasLength(landingPage.getTitle()) ? landingPage.getTitle() : landingPage.getName()) +
                                "</a></li>")
                .collect(Collectors.joining("\n"));

        return "<ul>" + sitemapHtml + "</ul>";
    }


    private void copyLandingPageVariables(LandingPage sourceLP, LandingPage destinationLP) {
        LOGGER.debug("Copying landing page variables from source LP {} to destination LP {}", sourceLP.getId(), destinationLP.getId());

        List<LandingPageVariable> lpVariables = sourceLandingPageAPIClient.getLandingPageVariables(sourceLP.getId(), APPROVED);

        if (lpVariables != null) {
            lpVariables.forEach(lpVariable ->{
                UpdateLandingPageVariableRequest updateLandingPageVariableRequest = UpdateLandingPageVariableRequest.builder()
                        .value(lpVariable.getValue())
                        .build();
                destinationLandingPageAPIClient.updateLandingPageVariable(destinationLP.getId(),
                        lpVariable.getId(), updateLandingPageVariableRequest);
            });
        }
    }

    private String buildDestinationPageName(LandingPage sourceLP, String namePrefix, boolean usePrefix) {

        return usePrefix && namePrefix != null
                ? namePrefix + "." + sourceLP.getName()
                : sourceLP.getName();
    }
}

/*
Copyright 2025 Adobe
All Rights Reserved.

NOTICE: Adobe permits you to use, modify, and distribute this file in
accordance with the terms of the Adobe license agreement accompanying
it.
*/
package com.adobe.mkto.assetcopy.config;

import com.adobe.mkto.assetcopy.client.APIClientHelper;
import com.adobe.mkto.assetcopy.client.FolderAPIClient;
import com.adobe.mkto.assetcopy.client.LandingPageAPIClient;
import com.adobe.mkto.assetcopy.client.ProgramAPIClient;
import com.adobe.mkto.assetcopy.client.UserManagementAPIClient;
import com.adobe.mkto.assetcopy.config.properties.AssetCopyProperties;
import com.adobe.mkto.assetcopy.constant.FolderType;
import com.adobe.mkto.assetcopy.exception.AssetCopyRuntimeException;
import com.adobe.mkto.assetcopy.model.Folder;
import com.adobe.mkto.assetcopy.model.FolderId;
import com.adobe.mkto.assetcopy.model.GlobalAsset;
import com.adobe.mkto.assetcopy.model.LandingPage;
import com.adobe.mkto.assetcopy.model.LandingPageCopyDestinationData;
import com.adobe.mkto.assetcopy.model.LocalAsset;
import com.adobe.mkto.assetcopy.model.Workspace;
import com.adobe.mkto.assetcopy.model.request.BrowseRequest;
import com.adobe.mkto.assetcopy.model.request.CreateFolderRequest;
import com.adobe.mkto.assetcopy.model.request.CreateProgramRequest;
import com.adobe.mkto.assetcopy.model.request.GetFolderByNameRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.util.CollectionUtils;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.adobe.mkto.assetcopy.constant.AssetCopyConstants.DESIGN_STUDIO_ROOT_FOLDER;
import static com.adobe.mkto.assetcopy.constant.AssetCopyConstants.MARKETING_ACTIVITIES_ROOT_FOLDER;
import static com.adobe.mkto.assetcopy.util.AssetCopyUtils.parseTimeString;

@Configuration
@RequiredArgsConstructor
@Profile("lp-copy")
public class LandingPageCopyConfig {

    private final UserManagementAPIClient destinationUserManagementAPIClient;
    private final FolderAPIClient destinationFolderAPIClient;
    private final ProgramAPIClient destinationProgramAPIClient;
    private final LandingPageAPIClient sourceLandingPageAPIClient;
    private final AssetCopyProperties assetCopyProperties;

    @Bean
    public Map<String, LandingPageCopyDestinationData> landingPageCopyDestinations() {
        return initializeLandingPageCopyDestinations();
    }

    @Bean
    public Map<String, List<LandingPage>> landingPagesByWorkspace() {

        BrowseRequest browseRequest = BrowseRequest.builder().status("approved").build();
        List<LandingPage> landingPages = APIClientHelper.browseAll(browseRequest, sourceLandingPageAPIClient::browse);
        Timestamp cutoffTime = parseTimeString(assetCopyProperties.getCutoffTime());

        return landingPages
                .stream()
                .filter(page -> eligibleForCopy(page, cutoffTime))
                .collect(Collectors.groupingBy(LandingPage::getWorkspace));
    }

    private boolean eligibleForCopy(LandingPage landingPage, Timestamp cutoffTime) {

        Timestamp createdAt = parseTimeString(landingPage.getCreatedAt());
        Timestamp updatedAt = parseTimeString(landingPage.getUpdatedAt());

        boolean timeEligible = createdAt.after(cutoffTime) || updatedAt.after(cutoffTime);
        boolean idEligible = !CollectionUtils.isEmpty(assetCopyProperties.getPageIds())
                && assetCopyProperties.getPageIds().contains(landingPage.getId());

        return timeEligible || idEligible;
    }

    private Map<String, LandingPageCopyDestinationData> initializeLandingPageCopyDestinations()  {
        List<Workspace> destinationWorkspaces = destinationUserManagementAPIClient.getWorkspaces();

        Map<String, LandingPageCopyDestinationData> result = destinationWorkspaces.stream()
                .filter(workspace -> assetCopyProperties.getWorkspaces().contains(workspace.getName()))
                .map(this::createLandingPageCopyDestination)
                .collect(Collectors.toMap(LandingPageCopyDestinationData::getWorkspaceName, workspaceFolder -> workspaceFolder));

        if (result.isEmpty()) {
            throw new AssetCopyRuntimeException("Workspaces in destination do not exist.");
        }

        return result;
    }

    private LandingPageCopyDestinationData createLandingPageCopyDestination(Workspace workspace)  {

        // Grab the destination Marketing Activities Root Folder
        FolderId workspaceMarketingActivitiesFolder = findDestinationFolder(workspace.getName(), workspace.getName(),
                MARKETING_ACTIVITIES_ROOT_FOLDER).getFirst().getFolderId();

        // Create the destination base campaign folder if it doesn't already exist
        String campaignFolderName = "Landing Pages - " + workspace.getName();
        FolderId campaignFolder = getOrCreateDestinationFolder(campaignFolderName, workspace.getName(),
                workspaceMarketingActivitiesFolder).getFolderId();

        // Creating the default program in destination if it doesn't already exist
        String programFolderName = "Landing Page Copy - " + workspace.getName();
        List<Folder> programFolderResponse = destinationFolderAPIClient.getFolderByName(GetFolderByNameRequest.builder()
                .name(programFolderName)
                .type("program")
                .root(campaignFolder)
                .build());

        FolderId programFolder = CollectionUtils.isEmpty(programFolderResponse)
                ? new FolderId(destinationProgramAPIClient.create(
                        CreateProgramRequest.builder()
                                .channel("Web Content")
                                .type("Default")
                                .name(programFolderName)
                                .folder(campaignFolder)
                                .build()).getId(), FolderType.PROGRAM)
                : programFolderResponse.getFirst().getFolderId();

        // Creating folder that holds Landing Pages in Destination if it doesn't already exist
        FolderId landingPageFolder = getOrCreateDestinationFolder("Landing Pages",
                workspace.getName(), programFolder).getFolderId();

        // Creating folder that holds Forms in Destination if it doesn't already exist
        FolderId formFolder = getOrCreateDestinationFolder("Forms", workspace.getName(), programFolder).getFolderId();

        // Grabbing the Root Design Studio folder for Global Assets
        FolderId workspaceDesignStudioFolder = findDestinationFolder(workspace.getName(), workspace.getName(),
                DESIGN_STUDIO_ROOT_FOLDER).getFirst().getFolderId();

        // Grabbing the root Global Landing Pages folder
        //this is the parent of LP Templates folder
        FolderId workspaceDesignStudioLPFolder = findDestinationFolder("Landing Pages", workspace.getName(),
                workspaceDesignStudioFolder).getFirst().getFolderId();

        // Grabbing the global Snippets folder in Destination
        FolderId snippetFolder = findDestinationFolder("Snippets", workspace.getName(),
                workspaceDesignStudioFolder).getFirst().getFolderId();

        // Grabbing the global Images and Files folder in Destination
        FolderId imageAndFileFolder = findDestinationFolder("Images and Files", workspace.getName(),
                workspaceDesignStudioFolder).getFirst().getFolderId();

        // Grabbing the global LP Templates folder
        FolderId lpTemplateFolder = findDestinationFolder("Templates", workspace.getName(),
                workspaceDesignStudioLPFolder).getFirst().getFolderId();

        // Building the LandingPageCopyDestinationData object that holds all of FolderIds and metadata for copying the landing pages
        return LandingPageCopyDestinationData.builder()
                .workspaceName(workspace.getName())
                .campaignFolder(campaignFolder)
                .programFolder(programFolder)
                .urlFormat(assetCopyProperties.getPageUrlFormat())
                .localAsset(LocalAsset.builder()
                        .landingPageFolder(landingPageFolder)
                        .formFolder(formFolder)
                        .build())
                .globalAsset(GlobalAsset.builder()
                        .snippetFolder(snippetFolder)
                        .imageAndFileFolder(imageAndFileFolder)
                        .landingPageTemplateFolder(lpTemplateFolder)
                        .build())
                .build();
    }

    private Folder getOrCreateDestinationFolder(String name, String workspaceName, FolderId parentFolder) {

        List<Folder> findFolderResponse = findDestinationFolder(name, workspaceName, parentFolder);

        return CollectionUtils.isEmpty(findFolderResponse)
                ? destinationFolderAPIClient.create(
                        CreateFolderRequest.builder()
                                .name(name)
                                .parent(parentFolder)
                                .build())
                : findFolderResponse.getFirst();
    }

    private List<Folder> findDestinationFolder(String name, String workspaceName, FolderId rootFolder)  {
        GetFolderByNameRequest request = GetFolderByNameRequest.builder()
                .name(name)
                .workspace(workspaceName)
                .root(rootFolder)
                .type("folder")
                .build();

        return destinationFolderAPIClient.getFolderByName(request);
    }
}

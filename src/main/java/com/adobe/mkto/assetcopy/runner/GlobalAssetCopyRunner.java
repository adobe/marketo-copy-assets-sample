/*
Copyright 2025 Adobe
All Rights Reserved.

NOTICE: Adobe permits you to use, modify, and distribute this file in
accordance with the terms of the Adobe license agreement accompanying
it.
*/
package com.adobe.mkto.assetcopy.runner;

import com.adobe.mkto.assetcopy.client.FolderAPIClient;
import com.adobe.mkto.assetcopy.client.SnippetAPIClient;
import com.adobe.mkto.assetcopy.client.UserManagementAPIClient;
import com.adobe.mkto.assetcopy.config.properties.AssetCopyProperties;
import com.adobe.mkto.assetcopy.constant.FolderType;
import com.adobe.mkto.assetcopy.constant.Subscription;
import com.adobe.mkto.assetcopy.exception.DoNotCopyException;
import com.adobe.mkto.assetcopy.model.Folder;
import com.adobe.mkto.assetcopy.model.FolderContent;
import com.adobe.mkto.assetcopy.model.FolderId;
import com.adobe.mkto.assetcopy.model.Workspace;
import com.adobe.mkto.assetcopy.service.CopyFileService;
import com.adobe.mkto.assetcopy.service.CopyFolderService;
import com.adobe.mkto.assetcopy.service.CopyLandingPageTemplateService;
import com.adobe.mkto.assetcopy.service.CopySnippetService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static com.adobe.mkto.assetcopy.constant.AssetCopyConstants.FOLDER;

@Component
@Profile("global-asset-copy")
@RequiredArgsConstructor
public class GlobalAssetCopyRunner implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalAssetCopyRunner.class);
    private static final FolderId DESIGN_STUDIO_ROOT_FOLDER = new FolderId(2, FolderType.FOLDER);
    private final UserManagementAPIClient destinationUserManagementAPIClient;
    private final FolderAPIClient sourceFolderAPIClient;
    private final AssetCopyProperties assetCopyProperties;
    private final CopyFileService copyFileService;
    private final CopyFolderService copyFolderService;
    private final CopySnippetService copySnippetService;
    private final SnippetAPIClient destinationSnippetAPIClient;
    private final CopyLandingPageTemplateService copyLandingPageTemplateService;

    @Override
    public void run(String... args)  {

        destinationUserManagementAPIClient.getWorkspaces()
                .stream()
                .filter(workspace -> assetCopyProperties.getWorkspaces().contains(workspace.getName()))
                .forEach(this::copyGlobalAssets);
    }

    private void copyGlobalAssets(Workspace workspace)  {

        FolderId sourceDesignStudioFolder = copyFolderService.getFolderByName(workspace.getName(), null,
                DESIGN_STUDIO_ROOT_FOLDER, workspace.getName(), Subscription.SOURCE).getFolderId();

        FolderId destinationDesignStudioFolder = copyFolderService.getFolderByName(workspace.getName(), null,
                DESIGN_STUDIO_ROOT_FOLDER, workspace.getName(), Subscription.DESTINATION).getFolderId();

        copyLPTemplates(sourceDesignStudioFolder, destinationDesignStudioFolder);
        copySnippets(sourceDesignStudioFolder, destinationDesignStudioFolder);
        //copying images and files takes too long
//        copyFiles(sourceDesignStudioFolder, destinationDesignStudioFolder);
    }

    private void copyLPTemplates(FolderId sourceRootFolder, FolderId destinationRootFolder)  {

        FolderId sourceLPFolder = copyFolderService.getFolderByName("Landing Pages", null,
                sourceRootFolder, null, Subscription.SOURCE).getFolderId();

        FolderId destinationLPFolder = copyFolderService.getFolderByName("Landing Pages", null,
                destinationRootFolder, null, Subscription.DESTINATION).getFolderId();

        Folder sourceTemplateFolder = copyFolderService.getFolderByName("Templates", FOLDER,
                sourceLPFolder, null, Subscription.SOURCE);

        Folder destinationTemplateFolder = copyFolderService.getFolderByName("Templates", FOLDER,
                destinationLPFolder, null, Subscription.DESTINATION);

        List<FolderContent> folderContents = sourceFolderAPIClient.getFolderContent(sourceTemplateFolder.getId());
        processFolderContents(folderContents, destinationTemplateFolder);
    }

//    private void copyFiles(FolderId sourceRootFolder, FolderId destinationRootFolder)  {
//
//        Folder sourceImagesAndFiles = folderHelper.getFolderByName("Images and Files", FOLDER,
//                sourceRootFolder.getId(), null, Subscription.SOURCE);
//
//        Folder destinationImagesAndFiles = folderHelper.getFolderByName("Images and Files", FOLDER,
//                destinationRootFolder.getId(), null, Subscription.DESTINATION);
//
//        List<FolderContent> folderContents = sourceFolderAPIClient.getFolderContent(sourceImagesAndFiles.getId());
//        processFolderContents(folderContents, destinationImagesAndFiles);
//    }

    private void copySnippets(FolderId sourceRootFolder, FolderId destinationRootFolder)  {

        Folder sourceSnippetsFolder = copyFolderService.getFolderByName("Snippets", FOLDER,
                sourceRootFolder, null, Subscription.SOURCE);

        Folder destinationSnippetsFolder = copyFolderService.getFolderByName("Snippets", FOLDER,
                destinationRootFolder, null, Subscription.DESTINATION);

        List<FolderContent> folderContents = sourceFolderAPIClient.getFolderContent(sourceSnippetsFolder.getId());
        if (!CollectionUtils.isEmpty(folderContents)) {
            processFolderContents(folderContents, destinationSnippetsFolder);
        }
    }

    private void processFolderContents(List<FolderContent> folderContents, Folder destinationFolder) {

        folderContents.forEach(folderContent -> {
                switch (folderContent.getType().toLowerCase()) {
                    case "file" ->
                            copyFileService.copyFileToFolder(folderContent.getId(), destinationFolder.getFolderId());

                    case "folder" -> //create folder and then recursion
                            copyFolderContents(folderContent, destinationFolder);

                    case "snippet" ->
                            copySnippetService.copySnippet(folderContent.getId(), destinationFolder.getFolderId());

                    case "landing page template" ->
                            copyLandingPageTemplateService.copyTemplate(folderContent.getId(), destinationFolder.getFolderId());

                    default -> LOGGER.warn("Unsupported content type: {}", folderContent.getType());
                }
        });
    }

    private void copyFolderContents(FolderContent folderContent, Folder destinationFolder) {
        try {
            Folder createdFolder = copyFolderService.copyFolder(folderContent.getId(), destinationFolder.getFolderId());
            List<FolderContent> currentFolderContents = sourceFolderAPIClient.getFolderContent(folderContent.getId());
            if (!CollectionUtils.isEmpty(currentFolderContents)) {
                processFolderContents(currentFolderContents, createdFolder);
            }
        }
        catch (DoNotCopyException e) {
            LOGGER.warn("Not copying file: {}", e.getMessage());
        }
    }

}

/*
Copyright 2025 Adobe
All Rights Reserved.

NOTICE: Adobe permits you to use, modify, and distribute this file in
accordance with the terms of the Adobe license agreement accompanying
it.
*/
package com.adobe.mkto.assetcopy.service;

import com.adobe.mkto.assetcopy.client.FolderAPIClient;
import com.adobe.mkto.assetcopy.constant.Subscription;
import com.adobe.mkto.assetcopy.exception.DoNotCopyException;
import com.adobe.mkto.assetcopy.model.Folder;
import com.adobe.mkto.assetcopy.model.FolderId;
import com.adobe.mkto.assetcopy.model.request.CreateFolderRequest;
import com.adobe.mkto.assetcopy.model.request.GetFolderByNameRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static com.adobe.mkto.assetcopy.constant.AssetCopyConstants.FOLDER;

@Component
@RequiredArgsConstructor
public class CopyFolderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CopyFolderService.class);
    private final FolderAPIClient sourceFolderAPIClient;
    private final FolderAPIClient destinationFolderAPIClient;

    public Folder copyFolder(Integer sourceFolderId, FolderId destinationFolder) throws DoNotCopyException {
        LOGGER.debug("Copying Folder {}", sourceFolderId);
        Folder sourceFolder = sourceFolderAPIClient.getById(sourceFolderId);

        if (Boolean.TRUE.equals(sourceFolder.getIsArchive())) {
            throw new DoNotCopyException("Cannot copy archive folder " + sourceFolder.getPath());
        }

        GetFolderByNameRequest getFolderByNameRequest = GetFolderByNameRequest.builder()
                .name(sourceFolder.getName())
                .type(FOLDER)
                .root(destinationFolder)
                .build();
        List<Folder> folderByNameResult = destinationFolderAPIClient.getFolderByName(getFolderByNameRequest);

        if (CollectionUtils.isEmpty(folderByNameResult)) {
            CreateFolderRequest createFolderRequest = CreateFolderRequest.builder()
                    .name(sourceFolder.getName())
                    .description(sourceFolder.getDescription())
                    .parent(destinationFolder)
                    .build();
            return destinationFolderAPIClient.create(createFolderRequest);
        }

        return folderByNameResult.getFirst();
    }

    public Folder getFolderByName(String name, String type, FolderId rootFolder, String workspace, Subscription subscription)  {

        GetFolderByNameRequest request = GetFolderByNameRequest.builder()
                .name(name)
                .type(type)
                .root(rootFolder)
                .workspace(workspace)
                .build();

        if (subscription.equals(Subscription.SOURCE)) {
            return sourceFolderAPIClient.getFolderByName(request).getFirst();
        }
        return destinationFolderAPIClient.getFolderByName(request).getFirst();
    }
}

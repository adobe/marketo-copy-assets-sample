/*
Copyright 2025 Adobe
All Rights Reserved.

NOTICE: Adobe permits you to use, modify, and distribute this file in
accordance with the terms of the Adobe license agreement accompanying
it.
*/
package com.adobe.mkto.assetcopy.service;

import com.adobe.mkto.assetcopy.client.FileAPIClient;
import com.adobe.mkto.assetcopy.exception.AssetCopyRuntimeException;
import com.adobe.mkto.assetcopy.model.File;
import com.adobe.mkto.assetcopy.model.FolderId;
import com.adobe.mkto.assetcopy.model.request.CreateFileRequest;
import lombok.RequiredArgsConstructor;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.system.ApplicationTemp;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class CopyFileService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CopyFileService.class);
    private final FileAPIClient sourceFileAPIClient;
    private final FileAPIClient destinationFileAPIClient;
    private final ApplicationTemp applicationTemp = new ApplicationTemp();

    // fileNamePrefix is used to make the file name unique across workspaces
    public File copyFile(Integer fileId, FolderId destinationFolder, String namePrefix) {
        LOGGER.debug("Beginning file copy with file id {} to destination folder {}", fileId, destinationFolder);

        File sourceFile = sourceFileAPIClient.getById(fileId);
        String fileName = StringUtils.hasLength(namePrefix)
                ? String.format("(%s) %s", namePrefix, sourceFile.getName())
                : sourceFile.getName();  //need unique name to not clash with other workspaces

        byte[] content = sourceFileAPIClient.getContent(fileId);

        Path filePath = createTempFile(sourceFile.getName(), sourceFile.getMimeType(), content);

        CreateFileRequest createFileRequest = CreateFileRequest.builder()
                .name(fileName)
                .description(sourceFile.getDescription())
                .folder(destinationFolder)
                .file(new FileSystemResource(filePath))
                .insertOnly(false)
                .build();
        return destinationFileAPIClient.create(createFileRequest);

    }

    public void copyFileToFolder(Integer sourceFileId, FolderId destinationFolder) {
       copyFile(sourceFileId, destinationFolder, null);
    }

    public Path createTempFile(String name, String mimeType, byte[] contents)  {
        LOGGER.debug("Creating temporary file with name {} and mimeType {}", name, mimeType);

        try {
            String fileName;
            if (mimeType != null) {
                fileName = name.contains(".")
                        ? name.substring(0, name.lastIndexOf("."))
                        : name;
                fileName += "." + MimeTypes.getDefaultMimeTypes().forName(mimeType).getExtension();
            }
            else {
                fileName = name;
            }

            Path tempFilePath = Path.of(applicationTemp.getDir().getPath(), fileName);
            Files.deleteIfExists(tempFilePath);
            Files.createFile(tempFilePath);
            Files.write(tempFilePath, contents);

            return tempFilePath;
        } catch (MimeTypeException | IOException e) {
            throw new AssetCopyRuntimeException("Unable to create temp file", e);
        }
    }
}

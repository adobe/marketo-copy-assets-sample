/*
Copyright 2025 Adobe
All Rights Reserved.

NOTICE: Adobe permits you to use, modify, and distribute this file in
accordance with the terms of the Adobe license agreement accompanying
it.
*/
package com.adobe.mkto.assetcopy.client;

import com.adobe.mkto.assetcopy.model.Folder;
import com.adobe.mkto.assetcopy.model.FolderContent;
import com.adobe.mkto.assetcopy.model.RestResponse;
import com.adobe.mkto.assetcopy.model.request.CreateFolderRequest;
import com.adobe.mkto.assetcopy.model.request.GetFolderByNameRequest;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class FolderAPIClient {

    private static final String GET_FOLDER = "/rest/asset/v1/folder/{id}.json";
    private static final String GET_FOLDER_BY_NAME = "/rest/asset/v1/folder/byName.json";
    private static final String CREATE_FOLDER = "/rest/asset/v1/folders.json";
    private static final String GET_FOLDER_CONTENT = "/rest/asset/v1/folder/{id}/content.json";

    private final MarketoRestAPIClient marketoRestAPIClient;

    public Folder create(CreateFolderRequest request) {
        RestResponse response = marketoRestAPIClient.post(CREATE_FOLDER, request);
        return marketoRestAPIClient.getResultFromResponse(response, Folder.class).getFirst();
    }

    public List<Folder> getFolderByName(GetFolderByNameRequest request) {
        RestResponse response = marketoRestAPIClient.get(GET_FOLDER_BY_NAME, request);
        return marketoRestAPIClient.getResultFromResponse(response, Folder.class);
    }

    public List<FolderContent> getFolderContent(Integer id) {
        RestResponse response = marketoRestAPIClient.get(GET_FOLDER_CONTENT, Map.of("maxReturn", 200), id);
        return marketoRestAPIClient.getResultFromResponse(response, FolderContent.class);
    }

    public Folder getById(Integer id) {
        RestResponse response = marketoRestAPIClient.get(GET_FOLDER, null, id);
        return marketoRestAPIClient.getResultFromResponse(response, Folder.class).getFirst();
    }
}

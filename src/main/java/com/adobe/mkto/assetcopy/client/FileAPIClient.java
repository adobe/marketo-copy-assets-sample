/*
Copyright 2025 Adobe
All Rights Reserved.

NOTICE: Adobe permits you to use, modify, and distribute this file in
accordance with the terms of the Adobe license agreement accompanying
it.
*/
package com.adobe.mkto.assetcopy.client;

import com.adobe.mkto.assetcopy.model.RestResponse;
import com.adobe.mkto.assetcopy.model.File;
import com.adobe.mkto.assetcopy.model.request.CreateFileRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Retryable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;

import java.util.List;


@RequiredArgsConstructor
public class FileAPIClient {

    private static final String GET_FILE = "/rest/asset/v1/file/{id}.json";
    private static final String GET_FILE_BY_NAME = "/rest/asset/v1/file/byName.json";
    private static final String GET_FILE_CONTENT = "/rest/asset/v1/file/{id}/content.json";
    private static final String CREATE_FILE = "/rest/asset/v1/files.json";

    private final MarketoRestAPIClient marketoRestAPIClient;

    public File getById(Integer id) {
        RestResponse response = marketoRestAPIClient.get(GET_FILE, null, id);
        return marketoRestAPIClient.getResultFromResponse(response, File.class).getFirst();
    }

    public List<File> getByName(String name) {
        RestResponse response = marketoRestAPIClient.get(GET_FILE_BY_NAME, null, name);
        return marketoRestAPIClient.getResultFromResponse(response, File.class);
    }

    public byte[] getContent(Integer id) {
        return marketoRestAPIClient.getResponseAsByteArray(GET_FILE_CONTENT, id);
    }

    @Retryable(retryFor = ResourceAccessException.class)
    public File create(CreateFileRequest requestObject) {

        MultiValueMap<String, Object> bodyParams = new LinkedMultiValueMap<>();
        bodyParams.add("name", requestObject.getName());
        bodyParams.add("file", requestObject.getFile());
        if (requestObject.getDescription() != null) {
            bodyParams.add("description", requestObject.getDescription());
        }
        if (requestObject.getFolder() != null) {
            bodyParams.add("folder", requestObject.getFolder());
        }
        if (requestObject.getInsertOnly() != null) {
            bodyParams.add("insertOnly", requestObject.getInsertOnly());
        }

        RestResponse response = marketoRestAPIClient.post(CREATE_FILE, bodyParams);
        return marketoRestAPIClient.getResultFromResponse(response, File.class).getFirst();
    }
}

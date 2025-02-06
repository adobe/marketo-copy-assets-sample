/*
Copyright 2025 Adobe
All Rights Reserved.

NOTICE: Adobe permits you to use, modify, and distribute this file in
accordance with the terms of the Adobe license agreement accompanying
it.
*/
package com.adobe.mkto.assetcopy.model.request;

import com.adobe.mkto.assetcopy.exception.AssetCopyRuntimeException;
import com.adobe.mkto.assetcopy.model.FolderId;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetFolderByNameRequest {

    private String name;
    private String type;
    private FolderId root;
    private String workspace;

    @JsonProperty("root")
    public String getRoot() {
        try {
            return (new ObjectMapper()).writeValueAsString(root);
        } catch (JsonProcessingException e) {
            throw new AssetCopyRuntimeException(e);
        }
    }

}

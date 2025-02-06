/*
Copyright 2025 Adobe
All Rights Reserved.

NOTICE: Adobe permits you to use, modify, and distribute this file in
accordance with the terms of the Adobe license agreement accompanying
it.
*/
package com.adobe.mkto.assetcopy.model;

import lombok.Data;

@Data
public class Folder {

    private Integer accessZoneId;
    private String createdAt;
    private String description;
    private FolderId folderId;
    private String folderType;
    private Integer id;
    private Boolean isArchive;
    private Boolean isSystem;
    private String name;
    private FolderId parent;
    private String path;
    private String updatedAt;
    private String url;
    private String workspace;
}

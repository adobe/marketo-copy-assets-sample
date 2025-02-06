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
public class File {

    private String createdAt;
    private String description;
    private FolderId folder;
    private Integer id;
    private String mimeType;
    private String name;
    private Integer size;
    private String updatedAt;
    private String url;
}

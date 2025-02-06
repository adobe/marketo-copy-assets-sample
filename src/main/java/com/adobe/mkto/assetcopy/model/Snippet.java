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
public class Snippet {

    private Integer id;
    private String name;
    private String description;
    private String createdAt;
    private SnippetFolder folder;
    private String status;
    private String updatedAt;
    private String url;
    private String workspace;
}

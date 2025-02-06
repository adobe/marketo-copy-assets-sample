/*
Copyright 2025 Adobe
All Rights Reserved.

NOTICE: Adobe permits you to use, modify, and distribute this file in
accordance with the terms of the Adobe license agreement accompanying
it.
*/
package com.adobe.mkto.assetcopy.model.request;

import com.adobe.mkto.assetcopy.model.FolderId;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateLandingPageRequest {
    private String customHeadHTML;
    private String description;
    private String facebookOgTags;
    private FolderId folder;
    private String keywords;
    private Boolean mobileEnabled;
    private String name;
    private Boolean prefillForm;
    private String robots;
    private Integer template;
    private String title;
    private String urlPageName;
    private String workspace;
}

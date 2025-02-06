/*
Copyright 2025 Adobe
All Rights Reserved.

NOTICE: Adobe permits you to use, modify, and distribute this file in
accordance with the terms of the Adobe license agreement accompanying
it.
*/
package com.adobe.mkto.assetcopy.model.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateLandingPageRequest {

    private String customHeadHTML;
    private String description;
    private String facebookOgTags;
    private String keywords;
    private String metaTagsDescription;
    private Boolean mobileEnabled;
    private String name;
    private String robots;
    private String styleOverRide;
    private String title;
    private String urlPageName;
}

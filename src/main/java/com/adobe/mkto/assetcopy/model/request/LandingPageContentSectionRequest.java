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
public class LandingPageContentSectionRequest {

    private String backgroundColor;
    private String borderColor;
    private String borderStyle;
    private String borderWidth;
    private String contentId;
    private String height;
    private Boolean hideDesktop;
    private Boolean hideMobile;
    private Boolean imageOpenNewWindow;
    private String left;
    private String linkUrl;
    private String opacity;
    private String top;
    private String type;
    private Object value;
    private String width;
    private Integer zIndex;
}

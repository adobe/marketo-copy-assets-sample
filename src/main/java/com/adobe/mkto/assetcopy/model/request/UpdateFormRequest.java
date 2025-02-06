/*
Copyright 2025 Adobe
All Rights Reserved.

NOTICE: Adobe permits you to use, modify, and distribute this file in
accordance with the terms of the Adobe license agreement accompanying
it.
*/
package com.adobe.mkto.assetcopy.model.request;

import com.adobe.mkto.assetcopy.model.KnownVisitor;
import lombok.Data;

@Data
public class UpdateFormRequest {

    private String customCss;
    private String description;
    private String fontFamily;
    private String fontSize;
    private KnownVisitor knownVisitor;
    private String labelPosition;
    private String language;
    private String locale;
    private String name;
    private Boolean progressiveProfiling;
    private String theme;
}

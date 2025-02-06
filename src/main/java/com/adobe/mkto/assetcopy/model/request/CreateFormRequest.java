/*
Copyright 2025 Adobe
All Rights Reserved.

NOTICE: Adobe permits you to use, modify, and distribute this file in
accordance with the terms of the Adobe license agreement accompanying
it.
*/
package com.adobe.mkto.assetcopy.model.request;

import com.adobe.mkto.assetcopy.model.FolderId;
import com.adobe.mkto.assetcopy.model.KnownVisitor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateFormRequest {

    private String description;
    private FolderId folder;
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

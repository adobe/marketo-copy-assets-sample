/*
Copyright 2025 Adobe
All Rights Reserved.

NOTICE: Adobe permits you to use, modify, and distribute this file in
accordance with the terms of the Adobe license agreement accompanying
it.
*/
package com.adobe.mkto.assetcopy.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AssetUri {

    private String byId;
    private String byName;
    private String browse;
    private String create;
    private String update;
    private String delete;
    private String approve;
}

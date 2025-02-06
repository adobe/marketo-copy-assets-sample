/*
Copyright 2025 Adobe
All Rights Reserved.

NOTICE: Adobe permits you to use, modify, and distribute this file in
accordance with the terms of the Adobe license agreement accompanying
it.
*/
package com.adobe.mkto.assetcopy.config.properties;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@ConfigurationProperties(prefix = "com.adobe.mkto.assetcopy")
@Component
@Data
public class AssetCopyProperties {
    private List<String> workspaces;
    private List<Integer> pageIds;
    private String cutoffTime;
    private String pageUrlFormat;
    private List<String> leadFields;
    private List<String> pmcfFields;
}

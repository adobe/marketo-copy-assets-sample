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

@ConfigurationProperties(prefix = "com.adobe.mkto.assetcopy.rest")
@Component
@Data
public class RestProperties {
    private String sourceUrl;
    private String destinationUrl;
}

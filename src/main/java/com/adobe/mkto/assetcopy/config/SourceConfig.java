/*
Copyright 2025 Adobe
All Rights Reserved.

NOTICE: Adobe permits you to use, modify, and distribute this file in
accordance with the terms of the Adobe license agreement accompanying
it.
*/
package com.adobe.mkto.assetcopy.config;

import com.adobe.mkto.assetcopy.client.FieldManagementAPIClient;
import com.adobe.mkto.assetcopy.client.FileAPIClient;
import com.adobe.mkto.assetcopy.client.FolderAPIClient;
import com.adobe.mkto.assetcopy.client.FormAPIClient;
import com.adobe.mkto.assetcopy.client.LandingPageAPIClient;
import com.adobe.mkto.assetcopy.client.MarketoRestAPIClient;
import com.adobe.mkto.assetcopy.client.ProgramAPIClient;
import com.adobe.mkto.assetcopy.client.SnippetAPIClient;
import com.adobe.mkto.assetcopy.client.UserManagementAPIClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@RequiredArgsConstructor
public class SourceConfig {

    private final RestClient sourceRestClient;
    private final ObjectMapper objectMapper;

    @Bean
    public MarketoRestAPIClient sourceMarketoAPIClient() {
        return new MarketoRestAPIClient(sourceRestClient, objectMapper);
    }
    
    @Bean
    public FileAPIClient sourceFileAPIClient() {
        return new FileAPIClient(sourceMarketoAPIClient());
    }

    @Bean
    public FolderAPIClient sourceFolderAPIClient() {
        return new FolderAPIClient(sourceMarketoAPIClient());
    }

    @Bean
    public FormAPIClient sourceFormAPIClient() {
        return new FormAPIClient(sourceMarketoAPIClient());
    }

    @Bean
    public FieldManagementAPIClient sourceFieldManagementAPIClient() {
        return new FieldManagementAPIClient(sourceMarketoAPIClient());
    }

    @Bean
    public LandingPageAPIClient sourceLandingPageAPIClient() {
        return new LandingPageAPIClient(sourceMarketoAPIClient());
    }

    @Bean
    public ProgramAPIClient sourceProgramAPIClient() {
        return new ProgramAPIClient(sourceMarketoAPIClient());
    }

    @Bean
    public SnippetAPIClient sourceSnippetAPIClient() {
        return new SnippetAPIClient(sourceMarketoAPIClient());
    }

    @Bean
    public UserManagementAPIClient sourceUserManagementAPIClient() {
        return new UserManagementAPIClient(sourceRestClient);
    }
}

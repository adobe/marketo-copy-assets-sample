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
public class DestinationConfig {

    private final RestClient destinationRestClient;
    private final ObjectMapper objectMapper;

    @Bean
    public MarketoRestAPIClient destinationMarketoAPIClient() {
        return new MarketoRestAPIClient(destinationRestClient, objectMapper);
    }

    @Bean
    public FileAPIClient destinationFileAPIClient() {
        return new FileAPIClient(destinationMarketoAPIClient());
    }

    @Bean
    public FolderAPIClient destinationFolderAPIClient() {
        return new FolderAPIClient(destinationMarketoAPIClient());
    }

    @Bean
    public FormAPIClient destinationFormAPIClient() {
        return new FormAPIClient(destinationMarketoAPIClient());
    }

    @Bean
    public FieldManagementAPIClient destinationFieldManagementAPIClient() {
        return new FieldManagementAPIClient(destinationMarketoAPIClient());
    }

    @Bean
    public LandingPageAPIClient destinationLandingPageAPIClient() {
        return new LandingPageAPIClient(destinationMarketoAPIClient());
    }

    @Bean
    ProgramAPIClient destinationProgramAPIClient() {
        return new ProgramAPIClient(destinationMarketoAPIClient());
    }

    @Bean
    public SnippetAPIClient destinationSnippetAPIClient() {
        return new SnippetAPIClient(destinationMarketoAPIClient());
    }

    @Bean
    public UserManagementAPIClient destinationUserManagementAPIClient() {
        return new UserManagementAPIClient(destinationRestClient);
    }
}

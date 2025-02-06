/*
Copyright 2025 Adobe
All Rights Reserved.

NOTICE: Adobe permits you to use, modify, and distribute this file in
accordance with the terms of the Adobe license agreement accompanying
it.
*/
package com.adobe.mkto.assetcopy.client;

import com.adobe.mkto.assetcopy.model.Workspace;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.util.List;

@RequiredArgsConstructor
public class UserManagementAPIClient {

    private static final String GET_WORKSPACES = "userservice/management/v1/users/workspaces.json";

    private final RestClient restClient;

    public List<Workspace> getWorkspaces()  {
        return restClient
                .get()
                .uri(GET_WORKSPACES)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}

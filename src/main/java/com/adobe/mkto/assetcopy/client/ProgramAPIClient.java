/*
Copyright 2025 Adobe
All Rights Reserved.

NOTICE: Adobe permits you to use, modify, and distribute this file in
accordance with the terms of the Adobe license agreement accompanying
it.
*/
package com.adobe.mkto.assetcopy.client;

import com.adobe.mkto.assetcopy.model.Program;
import com.adobe.mkto.assetcopy.model.RestResponse;
import com.adobe.mkto.assetcopy.model.request.CreateProgramRequest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ProgramAPIClient {

    private static final String CREATE_PROGRAM = "/rest/asset/v1/programs.json";
    private static final String GET_PROGRAM = "/rest/asset/v1/program/{id}.json";

    private final MarketoRestAPIClient marketoRestAPIClient;


    public Program create(CreateProgramRequest requestObject)  {
        RestResponse response = marketoRestAPIClient.post(CREATE_PROGRAM, requestObject);
        return marketoRestAPIClient.getResultFromResponse(response, Program.class).getFirst();
    }

    public Program getById(Integer id)  {
        RestResponse response = marketoRestAPIClient.get(GET_PROGRAM, null, id);
        return marketoRestAPIClient.getResultFromResponse(response, Program.class).getFirst();
    }
}

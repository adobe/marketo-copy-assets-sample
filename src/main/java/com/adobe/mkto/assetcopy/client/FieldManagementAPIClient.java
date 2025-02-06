/*
Copyright 2025 Adobe
All Rights Reserved.

NOTICE: Adobe permits you to use, modify, and distribute this file in
accordance with the terms of the Adobe license agreement accompanying
it.
*/
package com.adobe.mkto.assetcopy.client;

import com.adobe.mkto.assetcopy.exception.MktoRestClientRuntimeException;
import com.adobe.mkto.assetcopy.model.RestResponse;
import com.adobe.mkto.assetcopy.model.LeadField;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class FieldManagementAPIClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(FieldManagementAPIClient.class);
    private static final String GET_LEAD_FIELD_BY_NAME = "/rest/v1/leads/schema/fields/{fieldApiName}.json";
    private static final String CREATE_LEAD_FIELDS = "/rest/v1/leads/schema/fields.json";
    private static final String GET_PROGRAM_MEMBER_FIELD_BY_NAME = "/rest/v1/programs/members/schema/fields/{fieldApiName}.json";
    private static final String CREATE_PROGRAM_MEMBER_FIELDS = "/rest/v1/programs/members/schema/fields.json";

    private final MarketoRestAPIClient marketoRestAPIClient;

    public List<LeadField> getLeadFieldByName(String name) {
        RestResponse response = marketoRestAPIClient.get(GET_LEAD_FIELD_BY_NAME, null, name);
        return marketoRestAPIClient.getResultFromResponse(response, LeadField.class);
    }

    public List<LeadField> createLeadFields(List<LeadField> request) {
        RestResponse response = marketoRestAPIClient.postWithJsonInput(CREATE_LEAD_FIELDS, request);
        return validateCreateFieldResponse(response);
    }

    public List<LeadField> getProgramMemberFieldByName(String name) {
        RestResponse response = marketoRestAPIClient.get(GET_PROGRAM_MEMBER_FIELD_BY_NAME, null, name);
        return marketoRestAPIClient.getResultFromResponse(response, LeadField.class);
    }

    public List<LeadField> createProgramMemberFields(List<LeadField> request) {
        RestResponse response = marketoRestAPIClient.postWithJsonInput(CREATE_PROGRAM_MEMBER_FIELDS, request);
        return validateCreateFieldResponse(response);
    }

    private List<LeadField> validateCreateFieldResponse(RestResponse response) {
        if (response.isSuccess()) {
            return marketoRestAPIClient.getResultFromResponse(response, LeadField.class);
        }
        else if (response.getErrors().stream().allMatch(error -> "1003".equals(error.getCode()))) {
            LOGGER.error("Duplicate field name: {}", response.getErrors());
            return new ArrayList<>();
        }
        else {
            throw new MktoRestClientRuntimeException(String.format("Error creating custom fields: %s", response));
        }
    }
}

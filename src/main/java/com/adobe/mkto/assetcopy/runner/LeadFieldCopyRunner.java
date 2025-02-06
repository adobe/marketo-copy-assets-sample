/*
Copyright 2025 Adobe
All Rights Reserved.

NOTICE: Adobe permits you to use, modify, and distribute this file in
accordance with the terms of the Adobe license agreement accompanying
it.
*/
package com.adobe.mkto.assetcopy.runner;

import com.adobe.mkto.assetcopy.client.FieldManagementAPIClient;
import com.adobe.mkto.assetcopy.config.properties.AssetCopyProperties;
import com.adobe.mkto.assetcopy.model.LeadField;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("field-copy")
@RequiredArgsConstructor
public class LeadFieldCopyRunner implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(LeadFieldCopyRunner.class);

    private final FieldManagementAPIClient sourceFieldManagementAPIClient;
    private final FieldManagementAPIClient destinationFieldManagementAPIClient;
    private final AssetCopyProperties assetCopyProperties;

    @Override
    public void run(String[] args)  {

        List<LeadField> leadFields = assetCopyProperties.getLeadFields().stream()
                .flatMap(fieldName -> sourceFieldManagementAPIClient.getLeadFieldByName(fieldName).stream())
                .filter(leadField -> !("text".equals(leadField.getDataType()) || "formula".equals(leadField.getDataType())))
                .toList();

        List<LeadField> pmcfFields = assetCopyProperties.getPmcfFields().stream()
                .flatMap(fieldName -> sourceFieldManagementAPIClient.getProgramMemberFieldByName(fieldName).stream())
                .filter(leadField -> !("text".equals(leadField.getDataType()) || "formula".equals(leadField.getDataType())))
                .toList();

        if (!leadFields.isEmpty()) {
            List<LeadField> createdFields = destinationFieldManagementAPIClient.createLeadFields(leadFields);
            LOGGER.debug("Created following lead fields: {}", createdFields);
        }

        if (!pmcfFields.isEmpty()) {
            List<LeadField> createdFields = destinationFieldManagementAPIClient.createProgramMemberFields(pmcfFields);
            LOGGER.debug("Created following pmcf: {}", createdFields);
        }
    }
}

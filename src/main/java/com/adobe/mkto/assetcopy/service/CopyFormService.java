/*
Copyright 2025 Adobe
All Rights Reserved.

NOTICE: Adobe permits you to use, modify, and distribute this file in
accordance with the terms of the Adobe license agreement accompanying
it.
*/
package com.adobe.mkto.assetcopy.service;

import com.adobe.mkto.assetcopy.client.FormAPIClient;
import com.adobe.mkto.assetcopy.exception.AssetCopyRuntimeException;
import com.adobe.mkto.assetcopy.model.FieldPosition;
import com.adobe.mkto.assetcopy.model.FolderId;
import com.adobe.mkto.assetcopy.model.Form;
import com.adobe.mkto.assetcopy.model.FormField;
import com.adobe.mkto.assetcopy.model.FormFieldVisibilityRuleResponse;
import com.adobe.mkto.assetcopy.model.ThankYouPage;
import com.adobe.mkto.assetcopy.model.request.AddFormFieldRequest;
import com.adobe.mkto.assetcopy.model.request.AddFormFieldSetRequest;
import com.adobe.mkto.assetcopy.model.request.AddFormFieldVisibilityRequest;
import com.adobe.mkto.assetcopy.model.request.AddRichTextFieldRequest;
import com.adobe.mkto.assetcopy.model.request.CreateFormRequest;
import com.adobe.mkto.assetcopy.model.request.GetFormByNameRequest;
import com.adobe.mkto.assetcopy.model.request.UpdateFormFieldPositionsRequest;
import com.adobe.mkto.assetcopy.model.request.UpdateFormRequest;
import com.adobe.mkto.assetcopy.model.request.UpdateSubmitButtonRequest;
import com.adobe.mkto.assetcopy.model.request.UpdateThankYouPageRequest;
import com.adobe.mkto.assetcopy.model.request.VisibilityRuleRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.util.HtmlUtils;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.adobe.mkto.assetcopy.constant.AssetCopyConstants.APPROVED;

@Service
@RequiredArgsConstructor
public class CopyFormService {

    private final FormAPIClient sourceFormAPIClient;
    private final FormAPIClient destinationFormAPIClient;
    private final CopyFileService copyFileService;
    private final ObjectMapper objectMapper;


    public Form copyForm(Integer sourceFormId, FolderId destinationFolder, String namePrefix)  {

        Form sourceForm = sourceFormAPIClient.getById(sourceFormId, APPROVED);
        String formName = namePrefix != null ? namePrefix + "." + sourceForm.getName() : sourceForm.getName();
        GetFormByNameRequest getFormByNameRequest = GetFormByNameRequest.builder()
                .name(formName)
                .status(APPROVED)
                .folder(destinationFolder)
                .build();
        List<Form> destinationFormByNameResult = destinationFormAPIClient.getByName(getFormByNameRequest);

        if (CollectionUtils.isEmpty(destinationFormByNameResult)) {
            CreateFormRequest createRequest = CreateFormRequest.builder()
                    .folder(destinationFolder)
                    .fontFamily(sourceForm.getFontFamily())
                    .fontSize(sourceForm.getFontSize())
                    .knownVisitor(sourceForm.getKnownVisitor())
                    .labelPosition(sourceForm.getLabelPosition())
                    .language(sourceForm.getLanguage())
                    .locale(sourceForm.getLocale())
                    .name(formName)
                    .description(sourceForm.getDescription())
                    .progressiveProfiling(sourceForm.getProgressiveProfiling())
                    .build();

            Form destinationForm = destinationFormAPIClient.create(createRequest);
            destinationForm = updateTheme(sourceForm.getTheme(), destinationForm);
            updateThankYouPage(sourceForm.getThankYouList(), destinationForm.getId());
            copyFormFields(sourceForm, destinationForm);
            destinationFormAPIClient.approveForm(destinationForm.getId());

            return destinationForm;
        }

        return destinationFormByNameResult.getFirst();
    }

    private Form updateTheme(String sourceTheme, Form destinationForm)  {
        //theme can't be set at the same time as (font size, font type, and label position)
        if (StringUtils.hasLength(sourceTheme)) {
            UpdateFormRequest updateRequest = new UpdateFormRequest();
            updateRequest.setTheme(sourceTheme);

            destinationForm = destinationFormAPIClient.updateMetadata(destinationForm.getId(), updateRequest);
        }
        return destinationForm;
    }

    private void updateThankYouPage(List<ThankYouPage> sourceThankYouList, Integer destinationFormId)  {
        if (sourceThankYouList != null) {
            List<ThankYouPage> thankYouList = sourceThankYouList.stream()
                    .filter(page -> page.getFollowupType() != null && page.getFollowupValue() != null)
                    .toList();
            //thank you pages must have followup type and value or can't update
            if (!thankYouList.isEmpty()) {
                UpdateThankYouPageRequest updateThankYouPageRequest = UpdateThankYouPageRequest.builder()
                        .thankYouPage(thankYouList)
                        .build();
                destinationFormAPIClient.updateThankYouPage(destinationFormId, updateThankYouPageRequest);
            }
        }
    }


    private void copyFormFields(Form sourceForm, Form destinationForm)  {
        deleteDefaultFormFields(destinationForm.getId());
        // Retrieve Form Fields from Source
        List<FormField> sourceFormFields = sourceFormAPIClient.getFormFields(sourceForm.getId(), APPROVED);

        if (sourceFormFields != null){
            // Keep track of visibility rules that need to be updated
            Map<String, FormFieldVisibilityRuleResponse> visibilityRulesByFieldId = new HashMap<>();
            // Keep track of which source field was copied to which destination field by their IDs
            Map<String, String> sourceToDestinationFieldId = new HashMap<>();
            for (FormField formField : sourceFormFields){
                copyFormField(formField, destinationForm, visibilityRulesByFieldId, sourceToDestinationFieldId);
            }

            updateFormFieldVisibilityRules(visibilityRulesByFieldId, destinationForm);

            updateFormFieldPositions(destinationForm, sourceFormFields, sourceToDestinationFieldId);

            updateSubmitButton(destinationForm, sourceForm);
        }
    }

    private void copyFormField(FormField formField, Form destinationForm, Map<String, FormFieldVisibilityRuleResponse> visibilityRulesByFieldId,
                               Map<String, String> sourceToDestinationFieldId)  {
        AddFormFieldRequest addFormFieldRequest = AddFormFieldRequest.builder()
                .blankFields(formField.getBlankFields())
                .defaultValue(formField.getDefaultValue())
                .fieldType(formField.getDataType())
                .fieldId(formField.getId())
                .fieldWidth(formField.getFieldWidth())
                .formPrefill(formField.getFormPrefill())
                .isSensitive(formField.getIsSensitive())
                .hintText(formField.getHintText())
                .instructions(formField.getInstructions())
                .label(formField.getLabel() != null ? HtmlUtils.htmlEscape(formField.getLabel()) : null)
                .labelWidth(formField.getLabelWidth())
                .maxLength(formField.getMaxLength())
                .required(formField.getRequired())
                .validationMessage(String.valueOf(formField.getValidationMessage()))
                .build();

        // Update the request for each Form Field Metadata if present
        if (formField.getFieldMetaData() != null){
            addMetadataToRequest(formField.getFieldMetaData(), addFormFieldRequest);
        }


        FormField destinationFormField = addFormField(destinationForm, addFormFieldRequest, formField);

        visibilityRulesByFieldId.put(destinationFormField.getId(), formField.getVisibilityRules());
        sourceToDestinationFieldId.put(formField.getId(), destinationFormField.getId());
    }

    // private helper method for deleting the default Form Fields in the Destination Form that aren't needed
    private void deleteDefaultFormFields(Integer destinationFormId)  {
        Set<String> destinationFormFields = destinationFormAPIClient.getFormFields(destinationFormId, null)
                .stream()
                .map(FormField::getId)
                .collect(Collectors.toSet());

        //delete the default form fields that are added when a form is first created
        destinationFormFields
                .forEach(fieldId -> destinationFormAPIClient.deleteFormField(destinationFormId, fieldId));
    }

    // private helper method for adding metadata to AddFormFieldRequest if it is present
    private void addMetadataToRequest(Map<String, Object> formFieldMetadata,
                                      AddFormFieldRequest addFormFieldRequest)  {
        if (formFieldMetadata.containsKey("initiallyChecked")) {
            addFormFieldRequest.setInitiallyChecked(Boolean.parseBoolean(formFieldMetadata.get("initiallyChecked").toString()));
        }
        if (formFieldMetadata.containsKey("values")) {
            setMetadataValues(formFieldMetadata, addFormFieldRequest);
        }
        if (formFieldMetadata.containsKey("visibleLines")) {
            addFormFieldRequest.setVisibleLines(Integer.parseInt(formFieldMetadata.get("visibleLines").toString()));
        }
        if (formFieldMetadata.containsKey("maxValue")) {
            addFormFieldRequest.setMaxValue(Float.parseFloat(formFieldMetadata.get("maxValue").toString()));
        }
        if (formFieldMetadata.containsKey("minValue")) {
            addFormFieldRequest.setMinValue(Float.parseFloat(formFieldMetadata.get("minValue").toString()));
        }
        if (formFieldMetadata.containsKey("multiSelect")) {
            addFormFieldRequest.setMultiSelect(Boolean.parseBoolean(formFieldMetadata.get("multiSelect").toString()));
        }
        if (formFieldMetadata.containsKey("labelToRight")) {
            addFormFieldRequest.setLabelToRight(Boolean.parseBoolean(formFieldMetadata.get("labelToRight").toString()));
        }
        if (formFieldMetadata.containsKey("fieldMask")) {
            addFormFieldRequest.setMaskInput(formFieldMetadata.get("fieldMask").toString());
        }
    }

    private void setMetadataValues(Map<String, Object> formFieldMetadata, AddFormFieldRequest addFormFieldRequest)  {
        try {
            addFormFieldRequest.setValues(objectMapper.writeValueAsString(formFieldMetadata.get("values")));
        } catch (JsonProcessingException e) {
            throw new AssetCopyRuntimeException("Unable to process form field values", e);
        }
    }

    private void updateFormFieldPositions(Form destinationForm, List<FormField> sourceFormFields,
                                          Map<String, String> sourceToDestinationFieldId)  {
        UpdateFormFieldPositionsRequest updateFormFieldPositionsRequest = UpdateFormFieldPositionsRequest.builder()
                .positions(createFieldPositionsRequest(sourceFormFields, sourceToDestinationFieldId))
                .build();
        destinationFormAPIClient.updateFormFieldPositions(destinationForm.getId(), updateFormFieldPositionsRequest);
    }

    private void updateSubmitButton(Form destinationForm, Form sourceForm)  {
        UpdateSubmitButtonRequest updateSubmitButtonRequest = UpdateSubmitButtonRequest.builder()
                .buttonPosition(sourceForm.getButtonLocation())
                .buttonStyle(null) // button style is not obtainable via API so set as null
                .label(sourceForm.getButtonLabel())
                .waitingLabel(sourceForm.getWaitingLabel())
                .build();
        destinationFormAPIClient.updateSubmitButton(destinationForm.getId(), updateSubmitButtonRequest);
    }

    private FormField addFormField(Form destinationForm, AddFormFieldRequest addFormFieldRequest, FormField sourceField)  {

        FormField destinationFormField;

        if ("fieldset".equals(sourceField.getDataType())) {
            AddFormFieldSetRequest addFormFieldSetRequest = AddFormFieldSetRequest.builder().build();
            if (StringUtils.hasLength(addFormFieldRequest.getLabel())) {
                addFormFieldSetRequest.setLabel(addFormFieldRequest.getLabel());
                destinationFormField = destinationFormAPIClient.addFormFieldSet(destinationForm.getId(), addFormFieldSetRequest);
            }
            else {
                addFormFieldSetRequest.setLabel("placeholder");
                destinationFormField = destinationFormAPIClient.addFormFieldSet(destinationForm.getId(), addFormFieldSetRequest);
                AddFormFieldRequest updateLabelRequest = AddFormFieldRequest.builder()
                        .label("")
                        .build();
                destinationFormField = destinationFormAPIClient.updateFormField(destinationForm.getId(), destinationFormField.getId(), updateLabelRequest);
            }
        }
        else if ("htmltext".equals(sourceField.getDataType())) {
            Path filePath = copyFileService.createTempFile(sourceField.getId() + ".html", null, sourceField.getText().getBytes());
            AddRichTextFieldRequest addRichTextFieldRequest = AddRichTextFieldRequest.builder()
                    .text(new FileSystemResource(filePath))
                    .build();
            destinationFormField = destinationFormAPIClient.addRichTextField(destinationForm.getId(), addRichTextFieldRequest);
        }
        else {
            destinationFormField = destinationFormAPIClient.addFormField(destinationForm.getId(), addFormFieldRequest);
            if (sourceField.getAutoFill() != null) {
                destinationFormField = destinationFormAPIClient.updateFormFieldAutoFill(destinationForm.getId(),
                        destinationFormField.getId(), sourceField.getAutoFill());
            }
        }

        return destinationFormField;
    }

    private void updateFormFieldVisibilityRules(Map<String, FormFieldVisibilityRuleResponse> visibilityRulesByFieldId, Form destinationForm) {
        //update visibility rules
        visibilityRulesByFieldId.forEach((fieldId, visibilityRule) -> {
            if (visibilityRule != null && visibilityRule.getRuleType() != null && visibilityRule.getRules() != null) {
                AddFormFieldVisibilityRequest visibilityRequest = new AddFormFieldVisibilityRequest(visibilityRule.getRuleType(),
                        visibilityRule.getRules().stream()
                                .map(rule -> VisibilityRuleRequest.builder()
                                        .altLabel(rule.getAltLabel())
                                        .subjectField(rule.getSubjectField())
                                        .operator(rule.getOperator())
                                        .values(rule.getValues())
                                        .pickListValues(rule.getPicklistFilterValues())
                                        .build())
                                .toList());
                destinationFormAPIClient.addFormFieldVisibilityRules(destinationForm.getId(), fieldId, visibilityRequest);
            }
        });
    }

    private List<FieldPosition> createFieldPositionsRequest(List<FormField> sourceFormFields, Map<String, String> sourceToDestinationFieldId) {

        Map<String, FieldPosition> fieldPositionById = new LinkedHashMap<>();
        sourceFormFields
                .forEach(sourceField -> {
                    FieldPosition fieldPosition = FieldPosition.builder()
                            .fieldName(sourceToDestinationFieldId.get(sourceField.getId()))
                            .rowNumber(sourceField.getRowNumber())
                            .columnNumber(sourceField.getColumnNumber())
                            .fieldList(new ArrayList<>())
                            .build();

                    if (!CollectionUtils.isEmpty(sourceField.getFields())) {
                        sourceField.getFields()
                                .forEach(fieldId -> {
                                    FieldPosition nestedFieldPosition = fieldPositionById.remove(sourceToDestinationFieldId.get(fieldId));
                                    fieldPosition.getFieldList().add(nestedFieldPosition);
                                });
                    }

                    fieldPositionById.put(fieldPosition.getFieldName(), fieldPosition);
                });

        return fieldPositionById.values().stream().toList();
    }
}

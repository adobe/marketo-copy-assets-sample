/*
Copyright 2025 Adobe
All Rights Reserved.

NOTICE: Adobe permits you to use, modify, and distribute this file in
accordance with the terms of the Adobe license agreement accompanying
it.
*/
package com.adobe.mkto.assetcopy.client;

import com.adobe.mkto.assetcopy.model.RestResponse;
import com.adobe.mkto.assetcopy.model.AutoFill;
import com.adobe.mkto.assetcopy.model.Form;
import com.adobe.mkto.assetcopy.model.FormField;
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
import lombok.RequiredArgsConstructor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class FormAPIClient {

    private static final String GET_FORM = "/rest/asset/v1/form/{id}.json";
    private static final String GET_FORM_BY_NAME = "/rest/asset/v1/form/byName.json";
    private static final String CREATE_FORM = "/rest/asset/v1/forms.json";
    private static final String UPDATE_FORM = "/rest/asset/v1/form/{id}.json";
    private static final String UPDATE_THANK_YOU_PAGE = "/rest/asset/v1/form/{id}/thankYouPage.json";
    private static final String FORM_FIELDS = "/rest/asset/v1/form/{id}/fields.json";
    private static final String UPDATE_FORM_FIELD = "/rest/asset/v1/form/{id}/field/{fieldId}.json";
    private static final String UPDATE_FORM_FIELD_AUTOFILL = "/rest/asset/v1/form/{id}/field/{fieldId}/autofill.json";
    private static final String DELETE_FORM_FIELD = "/rest/asset/v1/form/{id}/field/{fieldId}/delete.json";
    private static final String ADD_RICH_TEXT_FIELD = "/rest/asset/v1/form/{id}/richText.json";
    private static final String ADD_FORM_FIELDSET = "/rest/asset/v1/form/{id}/fieldSet.json";
    private static final String ADD_FORM_FIELD_VISIBILITY_RULES = "/rest/asset/v1/form/{formId}/field/{fieldId}/visibility.json";
    private static final String UPDATE_FORM_FIELD_POSITIONS = "/rest/asset/v1/form/{id}/reArrange.json";
    private static final String UPDATE_SUBMIT_BUTTON = "/rest/asset/v1/form/{id}/submitButton.json";
    private static final String APPROVE_FORM = "/rest/asset/v1/form/{id}/approveDraft.json";

    private final MarketoRestAPIClient marketoRestAPIClient;


    public Form getById(Integer id, String status)  {
        RestResponse response = marketoRestAPIClient.get(GET_FORM, status != null ? Map.of("status", status) : null, id);
        return marketoRestAPIClient.getResultFromResponse(response, Form.class).getFirst();
    }

    public List<Form> getByName(GetFormByNameRequest requestObject)  {
        RestResponse response = marketoRestAPIClient.get(GET_FORM_BY_NAME, requestObject);
        return marketoRestAPIClient.getResultFromResponse(response, Form.class);
    }

    public Form create(CreateFormRequest requestObject)  {
        RestResponse response = marketoRestAPIClient.post(CREATE_FORM, requestObject);
        return marketoRestAPIClient.getResultFromResponse(response, Form.class).getFirst();
    }

    public Form updateMetadata(Integer id, UpdateFormRequest requestObject)  {
        RestResponse response = marketoRestAPIClient.post(UPDATE_FORM, requestObject, id);
        return marketoRestAPIClient.getResultFromResponse(response, Form.class).getFirst();
    }

    public void updateThankYouPage(Integer id, UpdateThankYouPageRequest requestObject)  {
        RestResponse response = marketoRestAPIClient.post(UPDATE_THANK_YOU_PAGE, requestObject, id);
        marketoRestAPIClient.validateResponse(response);
    }

    public List<FormField> getFormFields(Integer id, String status)  {
        RestResponse response = marketoRestAPIClient.get(FORM_FIELDS, status != null ? Map.of("status", status) : null, id);
        return marketoRestAPIClient.getResultFromResponse(response, FormField.class);
    }

    public FormField addFormField(Integer id, AddFormFieldRequest requestObject)  {
        RestResponse response = marketoRestAPIClient.post(FORM_FIELDS, requestObject, id);
        return marketoRestAPIClient.getResultFromResponse(response, FormField.class).getFirst();
    }

    public FormField updateFormField(Integer formId, String fieldId, AddFormFieldRequest requestObject)  {
        RestResponse response = marketoRestAPIClient.post(UPDATE_FORM_FIELD, requestObject, formId, fieldId);
        return marketoRestAPIClient.getResultFromResponse(response, FormField.class).getFirst();
    }

    public FormField addRichTextField(Integer id, AddRichTextFieldRequest requestObject)  {

        MultiValueMap<String, Object> bodyParams = new LinkedMultiValueMap<>();
        bodyParams.add("text", requestObject.getText());
        if (requestObject.getId() != null) {
            bodyParams.add("id", requestObject.getId());
        }

        RestResponse response = marketoRestAPIClient.post(ADD_RICH_TEXT_FIELD, bodyParams, id);
        return marketoRestAPIClient.getResultFromResponse(response, FormField.class).getFirst();
    }

    public FormField addFormFieldSet(Integer id, AddFormFieldSetRequest requestObject)  {
        RestResponse response = marketoRestAPIClient.post(ADD_FORM_FIELDSET, requestObject, id);
        return marketoRestAPIClient.getResultFromResponse(response, FormField.class).getFirst();
    }

    public void addFormFieldVisibilityRules(Integer formId, String fieldId, AddFormFieldVisibilityRequest requestObject)  {
        RestResponse response = marketoRestAPIClient.post(ADD_FORM_FIELD_VISIBILITY_RULES, requestObject, formId, fieldId);
        marketoRestAPIClient.validateResponse(response);
    }

    public void deleteFormField(Integer formId, String fieldId)  {
        RestResponse response = marketoRestAPIClient.post(DELETE_FORM_FIELD, null, formId, fieldId);
        marketoRestAPIClient.validateResponse(response);
    }

    public void updateFormFieldPositions(Integer id, UpdateFormFieldPositionsRequest requestObject)  {
        RestResponse response = marketoRestAPIClient.post(UPDATE_FORM_FIELD_POSITIONS, requestObject, id);
        marketoRestAPIClient.validateResponse(response);
    }

    public void updateSubmitButton(Integer id, UpdateSubmitButtonRequest requestObject)  {
        RestResponse response = marketoRestAPIClient.post(UPDATE_SUBMIT_BUTTON, requestObject, id);
        marketoRestAPIClient.validateResponse(response);
    }

    public FormField updateFormFieldAutoFill(Integer formId, String fieldId, AutoFill requestObject)  {
        RestResponse response = marketoRestAPIClient.post(UPDATE_FORM_FIELD_AUTOFILL, requestObject, formId, fieldId);
        return marketoRestAPIClient.getResultFromResponse(response, FormField.class).getFirst();
    }

    public void approveForm(Integer id)  {
        RestResponse response = marketoRestAPIClient.post(APPROVE_FORM, null, id);
        marketoRestAPIClient.validateResponse(response);
    }
}

/*
Copyright 2025 Adobe
All Rights Reserved.

NOTICE: Adobe permits you to use, modify, and distribute this file in
accordance with the terms of the Adobe license agreement accompanying
it.
*/
package com.adobe.mkto.assetcopy.client;

import com.adobe.mkto.assetcopy.model.RestResponse;
import com.adobe.mkto.assetcopy.model.LandingPage;
import com.adobe.mkto.assetcopy.model.LandingPageContent;
import com.adobe.mkto.assetcopy.model.LandingPageTemplate;
import com.adobe.mkto.assetcopy.model.LandingPageTemplateContent;
import com.adobe.mkto.assetcopy.model.LandingPageVariable;
import com.adobe.mkto.assetcopy.model.request.BrowseRequest;
import com.adobe.mkto.assetcopy.model.request.CreateLPTemplateRequest;
import com.adobe.mkto.assetcopy.model.request.CreateLandingPageRequest;
import com.adobe.mkto.assetcopy.model.request.LandingPageContentSectionRequest;
import com.adobe.mkto.assetcopy.model.request.UpdateLPTemplateContentRequest;
import com.adobe.mkto.assetcopy.model.request.UpdateLandingPageRequest;
import com.adobe.mkto.assetcopy.model.request.UpdateLandingPageVariableRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.adobe.mkto.assetcopy.constant.AssetCopyConstants.QUERY_PARAM_STATUS;

@RequiredArgsConstructor
public class LandingPageAPIClient {

    private static final String GET_LP = "/rest/asset/v1/landingPage/{id}.json";
    private static final String GET_LP_CONTENT = "/rest/asset/v1/landingPage/{id}/content.json";
    private static final String DELETE_LP_CONTENT = "/rest/asset/v1/landingPage/{id}/content/{contentId}/delete.json";
    private static final String GET_LP_BY_NAME = "/rest/asset/v1/landingPage/byName.json";
    private static final String GET_LP_TEMPLATE = "/rest/asset/v1/landingPageTemplate/{id}.json";
    private static final String GET_LP_TEMPLATE_BY_NAME = "/rest/asset/v1/landingPageTemplate/byName.json";
    private static final String GET_LP_TEMPLATE_CONTENT = "/rest/asset/v1/landingPageTemplate/{id}/content.json";
    private static final String UPDATE_LP_TEMPLATE_CONTENT = "/rest/asset/v1/landingPageTemplate/{id}/content";
    private static final String APPROVE_LP_TEMPLATE = "/rest/asset/v1/landingPageTemplate/{id}/approveDraft.json";
    private static final String CREATE_LP_TEMPLATE = "/rest/asset/v1/landingPageTemplates.json";
    private static final String CREATE_LP = "/rest/asset/v1/landingPages.json";
    private static final String BROWSE_LP = "/rest/asset/v1/landingPages.json";
    private static final String APPROVE_LP = "/rest/asset/v1/landingPage/{id}/approveDraft.json";
    private static final String GET_LP_VARIABLES = "/rest/asset/v1/landingPage/{id}/variables.json";
    private static final String UPDATE_LP_VARIABLE = "/rest/asset/v1/landingPage/{id}/variable/{variableId}.json";
    private static final String ADD_LP_CONTENT = "/rest/asset/v1/landingPage/{id}/content.json";
    private static final String UPDATE_LP_CONTENT = "/rest/asset/v1/landingPage/{id}/content/{contentId}.json";
    private static final String UPDATE_METADATA = "/rest/asset/v1/landingPage/{id}.json";
    private static final String DISCARD_DRAFT = "/rest/asset/v1/landingPage/{id}/discardDraft.json";

    private final MarketoRestAPIClient marketoRestAPIClient;


    public LandingPage getById(Integer id)  {
        RestResponse response = marketoRestAPIClient.get(GET_LP, null, id);
        return marketoRestAPIClient.getResultFromResponse(response, LandingPage.class).getFirst();
    }

    public List<LandingPageContent> getContentsById(Integer id)  {
        RestResponse response = marketoRestAPIClient.get(GET_LP_CONTENT, null, id);
        return marketoRestAPIClient.getResultFromResponse(response, LandingPageContent.class);
    }

    public void deleteContent(Integer pageId, String contentId) {
        RestResponse response = marketoRestAPIClient.post(DELETE_LP_CONTENT, null, pageId, contentId);
        marketoRestAPIClient.validateResponse(response);
    }

    public List<LandingPageTemplate> getTemplateById(Integer templateId, String status)  {
        RestResponse response = marketoRestAPIClient.get(GET_LP_TEMPLATE,
                status != null ? Map.of(QUERY_PARAM_STATUS, status) : null, templateId);
        return marketoRestAPIClient.getResultFromResponse(response, LandingPageTemplate.class);
    }

    public List<LandingPageTemplate> getTemplateByName(String templateName, String status)  {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("name", templateName);
        if (status != null) {
            queryParams.put("status", status);
        }
        RestResponse response = marketoRestAPIClient.get(GET_LP_TEMPLATE_BY_NAME, queryParams);
        return marketoRestAPIClient.getResultFromResponse(response, LandingPageTemplate.class);
    }

    public LandingPageTemplateContent getTemplateContentById(Integer templateId, String status)  {
        RestResponse response = marketoRestAPIClient.get(GET_LP_TEMPLATE_CONTENT,
                status != null ? Map.of(QUERY_PARAM_STATUS, status) : null, templateId);
        return marketoRestAPIClient.getResultFromResponse(response, LandingPageTemplateContent.class).getFirst();
    }

    public List<LandingPage> getByName(String name, String status) {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("name", name);
        if (status != null) {
            queryParams.put("status", status);
        }
        RestResponse response = marketoRestAPIClient.get(GET_LP_BY_NAME, queryParams);
        return marketoRestAPIClient.getResultFromResponse(response, LandingPage.class);
    }

    public List<LandingPage> browse(BrowseRequest requestObject)  {
        RestResponse response = marketoRestAPIClient.get(BROWSE_LP, requestObject);
        return marketoRestAPIClient.getResultFromResponse(response, LandingPage.class);
    }

    public LandingPage create(CreateLandingPageRequest requestObject)  {
        RestResponse response = marketoRestAPIClient.post(CREATE_LP, requestObject);
        return marketoRestAPIClient.getResultFromResponse(response, LandingPage.class).getFirst();
    }

    public LandingPageTemplate createTemplate(CreateLPTemplateRequest requestObject)  {
        RestResponse response = marketoRestAPIClient.post(CREATE_LP_TEMPLATE, requestObject);
        return marketoRestAPIClient.getResultFromResponse(response, LandingPageTemplate.class).getFirst();
    }

    public void updateTemplateContent(Integer id, UpdateLPTemplateContentRequest requestObject)  {
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("content", requestObject.getContent());
        if (requestObject.getId() != null) {
            params.add("id", requestObject.getId());
        }

        RestResponse response = marketoRestAPIClient.post(UPDATE_LP_TEMPLATE_CONTENT, params, id);
        marketoRestAPIClient.validateResponse(response);
    }

    public List<LandingPageVariable> getLandingPageVariables(Integer id, String status)  {
        RestResponse response = marketoRestAPIClient.get(GET_LP_VARIABLES,
                status != null ? Map.of(QUERY_PARAM_STATUS, status) : null, id);
        return marketoRestAPIClient.getResultFromResponse(response, LandingPageVariable.class);
    }

    public void updateLandingPageVariable(Integer id, String variableId, UpdateLandingPageVariableRequest requestObject)  {
        RestResponse response = marketoRestAPIClient.post(UPDATE_LP_VARIABLE, requestObject, id, variableId);
        marketoRestAPIClient.validateResponse(response);
    }

    public void addLandingPageContent(Integer id, LandingPageContentSectionRequest requestObject)  {
        RestResponse response = marketoRestAPIClient.post(ADD_LP_CONTENT, requestObject, id);
        marketoRestAPIClient.validateResponse(response);
    }

    public void updateLandingPageContent(Integer id, String contentId, LandingPageContentSectionRequest requestObject)  {
        RestResponse response = marketoRestAPIClient.post(UPDATE_LP_CONTENT, requestObject, id, contentId);
        marketoRestAPIClient.validateResponse(response);
    }

    public void approveTemplate(Integer id)  {
        RestResponse response = marketoRestAPIClient.post(APPROVE_LP_TEMPLATE, null, id);
        marketoRestAPIClient.validateResponse(response);
    }

    public void updateMetadata(Integer id, UpdateLandingPageRequest requestObject)  {
        RestResponse response = marketoRestAPIClient.post(UPDATE_METADATA, requestObject, id);
        marketoRestAPIClient.validateResponse(response);
    }

    public void discardDraft(Integer id) {
        RestResponse response = marketoRestAPIClient.post(DISCARD_DRAFT, null, id);
        marketoRestAPIClient.validateResponse(response);
    }
}

/*
Copyright 2025 Adobe
All Rights Reserved.

NOTICE: Adobe permits you to use, modify, and distribute this file in
accordance with the terms of the Adobe license agreement accompanying
it.
*/
package com.adobe.mkto.assetcopy.runner;

import com.adobe.mkto.assetcopy.client.APIClientHelper;
import com.adobe.mkto.assetcopy.client.LandingPageAPIClient;
import com.adobe.mkto.assetcopy.model.LandingPage;
import com.adobe.mkto.assetcopy.model.request.BrowseRequest;
import com.adobe.mkto.assetcopy.service.CopyLandingPageService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Profile("sitemap")
public class CreateSitemapRunner implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(CreateSitemapRunner.class);
    private static final Integer PROGRAM_ID = 1063;
    private static final Integer SITEMAP_ID = 1927;
    private final LandingPageAPIClient destinationLandingPageAPIClient;
    private final CopyLandingPageService copyLandingPageService;

    @Override
    public void run(String... args) {
        BrowseRequest browseRequest = BrowseRequest.builder().status("approved").build();
        List<LandingPage> siteMapList = APIClientHelper.browseAll(browseRequest, destinationLandingPageAPIClient::browse);
//        landingPageHelper.createSitemap(new FolderId(PROGRAM_ID, FolderType.PROGRAM), siteMapList);
        copyLandingPageService.updateExistingSitemap(SITEMAP_ID, siteMapList);
    }
}

# Marketo Copy Assets Sample Code

This is a sample Spring Boot application that uses Spring's [RestClient](https://docs.spring.io/spring-framework/reference/integration/rest-clients.html#rest-restclient)
to make protected resource requests to Marketo's Rest APIs in order to copy/sync assets from one subscription to another.

The code is not intended to be able to run as is and needs some manual setup in order to copy and/or update assets.

The purpose of this code is to provide samples that send requests and reads responses from Marketo's Rest APIs and applies business logic to execute various asset copying tasks

There are 5 [CommandLineRunners](https://docs.spring.io/spring-boot/docs/3.2.5/reference/htmlsingle/#features.spring-application.command-line-runner)
that show various tasks of reading data from a source subscription and writing to a destination subscription:
(Each CommandLineRunner has an associated spring profile)
1. GlobalAssetCopyRunner("global-asset-copy") - copies global assets (templates, snippets, files) that are found under the Design Studio tab
2. LeadFieldCopyRunner("field-copy") - copies custom lead and program member fields that may be referenced in LP forms
3. LandingPageCopyRunner("lp-copy") - copies landing pages and all contents excluding global assets into a program in the specified workspace(s)
4. CreateSitemapRunner("sitemap") - generates a landing page that contains links to all existing landing pages that can be used as a sitemap
5. UpdateLandingPageRunner("update-lp") - updates existing landing pages

The application also shows how to use Spring Security to enable [OAuth2 authorization](https://docs.spring.io/spring-security/reference/servlet/oauth2/client/authorized-clients.html#oauth2-client-rest-client) for the RestClient

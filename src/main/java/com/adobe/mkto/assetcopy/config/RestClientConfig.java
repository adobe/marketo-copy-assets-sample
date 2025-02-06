/*
Copyright 2025 Adobe
All Rights Reserved.

NOTICE: Adobe permits you to use, modify, and distribute this file in
accordance with the terms of the Adobe license agreement accompanying
it.
*/
package com.adobe.mkto.assetcopy.config;

import com.adobe.mkto.assetcopy.config.properties.RestProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.client.OAuth2ClientHttpRequestInterceptor;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;

import static org.springframework.security.oauth2.client.web.client.RequestAttributeClientRegistrationIdResolver.clientRegistrationId;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class RestClientConfig {

    private final RestProperties restProperties;

    /*
    Need to create an OAuth2AuthorizedClientManager bean of type AuthorizedClientServiceOAuth2AuthorizedClientManager,
    which is capable of operating outside of the context of a HttpServletRequest, e.g. in a scheduled/background
    thread and/or in the service-tier. The DefaultOAuth2AuthorizedClientManager does not work for our purposes.
     */
    @Bean
    public OAuth2AuthorizedClientManager authorizedClientManager(ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientService authorizedClientService) {

        OAuth2AuthorizedClientProvider authorizedClientProvider =
                OAuth2AuthorizedClientProviderBuilder.builder()
                        .clientCredentials()
                        .build();

        AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager =
                new AuthorizedClientServiceOAuth2AuthorizedClientManager(
                        clientRegistrationRepository, authorizedClientService);
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

        return authorizedClientManager;
    }

    @Bean
    public RestClient sourceRestClient(OAuth2AuthorizedClientManager authorizedClientManager) {
        return RestClient.builder()
                .baseUrl(restProperties.getSourceUrl())
                .requestInterceptor(requestInterceptor(authorizedClientManager))
                .defaultRequest(spec -> spec.attributes(clientRegistrationId("source"))) //tells the authorizedClientManager which provider to use
                .messageConverters(converters -> converters.add(new StringHttpMessageConverter(StandardCharsets.UTF_8)))
                .build();
    }

    @Bean
    public RestClient destinationRestClient(OAuth2AuthorizedClientManager authorizedClientManager) {
        return RestClient.builder()
                .baseUrl(restProperties.getDestinationUrl())
                .requestInterceptor(requestInterceptor(authorizedClientManager))
                .defaultRequest(spec -> spec.attributes(clientRegistrationId("destination"))) //tells the authorizedClientManager which provider to use
                .messageConverters(converters -> converters.add(new StringHttpMessageConverter(StandardCharsets.UTF_8)))
                .build();
    }

    @Bean
    public OAuth2ClientHttpRequestInterceptor requestInterceptor(OAuth2AuthorizedClientManager authorizedClientManager) {
        return new OAuth2ClientHttpRequestInterceptor(authorizedClientManager);
    }
}

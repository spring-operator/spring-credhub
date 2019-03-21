/*
 * Copyright 2016-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.credhub.configuration;

import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.credhub.autoconfig.CredHubAutoConfiguration;
import org.springframework.credhub.autoconfig.CredHubOAuth2TemplateAutoConfiguration;
import org.springframework.credhub.autoconfig.CredHubTemplateAutoConfiguration;
import org.springframework.credhub.core.CredHubTemplate;
import org.springframework.credhub.core.OAuth2CredHubTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Daniel Lavoie
 */
public class CredHubOAuth2TemplateAutoConfigurationTests {

	private ApplicationContextRunner context = new ApplicationContextRunner()
			.withConfiguration(AutoConfigurations.of(CredHubAutoConfiguration.class,
					CredHubOAuth2TemplateAutoConfiguration.class,
					CredHubTemplateAutoConfiguration.class))
			.withPropertyValues(
					"spring.credhub.url=https://localhost",
					"spring.credhub.oauth2.client-id=test-user",
					"spring.credhub.oauth2.client-secret=test-secret",
					"spring.credhub.oauth2.access-token-uri=https://uaa.example.com/oauth/token",
					"debug"
			);

	@Test
	public void contextLoadsWithSpringSecurityOAuth2() {
		context.run((context) -> {
			assertThat(context).hasSingleBean(OAuth2CredHubTemplate.class);

			assertThat(context).hasSingleBean(ClientCredentialsResourceDetails.class);
			ClientCredentialsResourceDetails credentialsDetails =
					context.getBean(ClientCredentialsResourceDetails.class);
			assertThat(credentialsDetails).isNotNull();
			assertThat(credentialsDetails.getClientId()).isEqualTo("test-user");
			assertThat(credentialsDetails.getClientSecret()).isEqualTo("test-secret");
			assertThat(credentialsDetails.getAccessTokenUri())
					.isEqualTo("https://uaa.example.com/oauth/token");
		});
	}

	@Test
	public void contextLoadsWithoutSpringSecurityOAuth2() {
		context.withClassLoader(new FilteredClassLoader(ClientCredentialsResourceDetails.class))
				.run((context) -> {
					assertThat(context).hasSingleBean(CredHubTemplate.class);
					assertThat(context).doesNotHaveBean(OAuth2CredHubTemplate.class);

					assertThat(context).doesNotHaveBean(ClientCredentialsResourceDetails.class);
				});
	}
}

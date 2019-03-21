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

package org.springframework.credhub.autoconfig;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.credhub.configuration.CredHubTemplateFactory;
import org.springframework.credhub.core.CredHubProperties;
import org.springframework.credhub.support.ClientOptions;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for Spring CredHub support beans.
 * 
 * @author Scott Frederick
 * @author Daniel Lavoie
 */
@Configuration
@EnableConfigurationProperties
public class CredHubAutoConfiguration {
	private final CredHubTemplateFactory credHubTemplateFactory = new CredHubTemplateFactory();

	/**
	 * Create a {@link CredHubProperties} bean and populate it from properties.
	 * 
	 * @return a {@link CredHubProperties} bean
	 */
	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnProperty(value = "spring.credhub.url")
	@ConfigurationProperties(prefix = "spring.credhub")
	public CredHubProperties credHubProperties() {
		return new CredHubProperties();
	}

	/**
	 * Create a {@link ClientOptions} bean and populate it from properties.
	 *
	 * @return a {@link ClientOptions} bean
	 */
	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnProperty(value = "spring.credhub.url")
	@ConfigurationProperties(prefix = "spring.credhub")
	public ClientOptions clientOptions() {
		return new ClientOptions();
	}

	/**
	 * Create a {@link ClientFactoryWrapper} containing a
	 * {@link ClientHttpRequestFactory}. {@link ClientHttpRequestFactory} is not exposed
	 * as root bean because {@link ClientHttpRequestFactory} is configured with
	 * {@link ClientOptions} which are not necessarily applicable for the whole
	 * application.
	 *
	 * @param clientOptions the populated {@link ClientOptions} bean
	 * @return the {@link ClientFactoryWrapper} to wrap a {@link ClientHttpRequestFactory}
	 * instance
	 */
	@Bean
	@ConditionalOnBean(ClientOptions.class)
	public ClientFactoryWrapper clientHttpRequestFactoryWrapper(ClientOptions clientOptions) {
		return new ClientFactoryWrapper(
				credHubTemplateFactory.clientHttpRequestFactoryWrapper(clientOptions));
	}

	/**
	 * Create a {@link ClientHttpConnector}.
	 *
	 * @param clientOptions the populated {@link ClientOptions} bean
	 * @return the {@link ClientHttpConnector}
	 */
	@Bean
	@ConditionalOnBean(ClientOptions.class)
	@ConditionalOnClass(WebClient.class)
	public ClientHttpConnector clientHttpConnector(ClientOptions clientOptions) {
		return credHubTemplateFactory.clientHttpConnector(clientOptions);
	}

	/**
	 * Wrapper for {@link ClientHttpRequestFactory} to not expose the bean globally.
	 */
	public static class ClientFactoryWrapper implements InitializingBean, DisposableBean {

		private final ClientHttpRequestFactory clientHttpRequestFactory;

		ClientFactoryWrapper(ClientHttpRequestFactory clientHttpRequestFactory) {
			this.clientHttpRequestFactory = clientHttpRequestFactory;
		}

		@Override
		public void destroy() throws Exception {
			if (clientHttpRequestFactory instanceof DisposableBean) {
				((DisposableBean) clientHttpRequestFactory).destroy();
			}
		}

		@Override
		public void afterPropertiesSet() throws Exception {
			if (clientHttpRequestFactory instanceof InitializingBean) {
				((InitializingBean) clientHttpRequestFactory).afterPropertiesSet();
			}
		}

		ClientHttpRequestFactory getClientHttpRequestFactory() {
			return clientHttpRequestFactory;
		}
	}
}

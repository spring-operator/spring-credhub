/*
 *
 *  * Copyright 2013-2017 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      https://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.springframework.credhub.core;

import org.springframework.beans.factory.annotation.Value;

/**
 * Properties containing information about a CredHub server.
 *
 * @author Scott Frederick
 */
public class CredHubProperties {
	@Value("${CREDHUB_API}")
	private String apiUriBase;

	/**
	 * Create a new instance without initializing properties.
	 */
	public CredHubProperties() {
		if (apiUriBase == null) {
			apiUriBase = System.getenv("CREDHUB_API");
		}
	}

	/**
	 * Create a new instance with the provided properties. Intended to be used
	 * internally for testing.
	 *
	 * @param apiUriBase the base URI for the CredHub server
	 */
	CredHubProperties(String apiUriBase) {
		this.apiUriBase = apiUriBase;
	}

	/**
	 * Get the base URI for the CredHub server (scheme, host, and port). This value
	 * will be prepended to all requests to CredHub.
	 *
	 * @return the base URI
	 */
	public String getApiUriBase() {
		return apiUriBase;
	}
}

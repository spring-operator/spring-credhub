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

package org.springframework.credhub.support;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility methods for configuring JSON serialization and deserialization.
 *
 * @author Scott Frederick
 */
public class JsonUtils {
	/**
	 * Create and configure the {@link ObjectMapper} used for serializing and deserializing
	 * JSON requests and responses.
	 *
	 * @return a configured {@link ObjectMapper}
	 */
	public static ObjectMapper buildObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setDateFormat(new ISO8601DateFormat());
		objectMapper.setPropertyNamingStrategy(new PropertyNamingStrategy.SnakeCaseStrategy());
		objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		configureCredentialDetailTypeMapping(objectMapper);

		return objectMapper;
	}

	/**
	 * Configure type mapping for the {@literal value} field in the {@literal CredentialDetails}
	 * object.
	 *
	 * @param objectMapper the {@link ObjectMapper} to configure
	 */
	private static void configureCredentialDetailTypeMapping(ObjectMapper objectMapper) {
		List<NamedType> subtypes = new ArrayList<NamedType>();
		for (CredentialType type : CredentialType.values()) {
			subtypes.add(new NamedType(type.getModelClass(), type.getValueType()));
		}

		objectMapper.registerSubtypes(subtypes.toArray(new NamedType[]{}));
	}
}

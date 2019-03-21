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

package org.springframework.credhub.support.rsa;

import org.junit.Before;
import org.junit.Test;

import org.springframework.credhub.support.CredentialRequestUnitTestsBase;
import org.springframework.credhub.support.SimpleCredentialName;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.valid4j.matchers.jsonpath.JsonPathMatchers.hasJsonPath;
import static org.valid4j.matchers.jsonpath.JsonPathMatchers.hasNoJsonPath;

public class RsaCredentialRequestUnitTests extends CredentialRequestUnitTestsBase {
	@Before
	public void setUp() {
		buildRequest(new RsaCredential("public-key", "private-key"));
	}

	@Test
	public void serializeWithPublicAndPrivateKey() throws Exception {
		String jsonValue = serializeToJson(requestBuilder);

		assertThat(jsonValue,
				allOf(hasJsonPath("$.overwrite", equalTo(true)),
						hasJsonPath("$.name", equalTo("/example/credential")),
						hasJsonPath("$.type", equalTo("rsa")),
						hasJsonPath("$.value.public_key", equalTo("public-key")),
						hasJsonPath("$.value.private_key", equalTo("private-key"))));

		assertNoPermissions(jsonValue);
	}

	@Test
	public void serializeWithPublicKey() throws Exception {
		buildRequest(new RsaCredential("public-key", null));

		String jsonValue = serializeToJson(requestBuilder);

		assertThat(jsonValue,
				allOf(hasJsonPath("$.overwrite", equalTo(true)),
						hasJsonPath("$.name", equalTo("/example/credential")),
						hasJsonPath("$.type", equalTo("rsa")),
						hasJsonPath("$.value.public_key", equalTo("public-key")),
						hasNoJsonPath("$.value.private_key")));

		assertNoPermissions(jsonValue);
	}

	@Test
	public void serializeWithPrivateKey() throws Exception {
		buildRequest(new RsaCredential(null, "private-key"));

		String jsonValue = serializeToJson(requestBuilder);

		assertThat(jsonValue,
				allOf(hasJsonPath("$.overwrite", equalTo(true)),
						hasJsonPath("$.name", equalTo("/example/credential")),
						hasJsonPath("$.type", equalTo("rsa")),
						hasNoJsonPath("$.value.public_key"),
						hasJsonPath("$.value.private_key", equalTo("private-key"))));

		assertNoPermissions(jsonValue);
	}

	@Test(expected = IllegalArgumentException.class)
	public void serializeWithNeitherKey() throws Exception {
		buildRequest(new RsaCredential(null, null));

		String jsonValue = serializeToJson(requestBuilder);
	}

	private void buildRequest(RsaCredential value) {
		requestBuilder = RsaCredentialRequest.builder()
				.name(new SimpleCredentialName("example", "credential"))
				.overwrite(true)
				.value(value);
	}
}
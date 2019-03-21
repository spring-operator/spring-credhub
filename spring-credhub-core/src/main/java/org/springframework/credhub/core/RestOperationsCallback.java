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

package org.springframework.credhub.core;

import org.springframework.web.client.RestOperations;

/**
 * A callback for executing arbitrary operations on {@link RestOperations}.
 *
 * @author Mark Paluch
 */
public interface RestOperationsCallback<T> {

	/**
	 * Callback method providing a {@link RestOperations} that is configured to interact
	 * with the CredHub server.
	 *
	 * @param restOperations restOperations to use, must not be {@literal null}.
	 * @return a result object or null if none.
	 */
	T doWithRestOperations(RestOperations restOperations);
}
/*
 *
 * Copyright 2013-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.springframework.credhub.support.permissions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.util.Assert;

import static org.springframework.credhub.support.permissions.ActorType.APP;
import static org.springframework.credhub.support.permissions.ActorType.OAUTH_CLIENT;
import static org.springframework.credhub.support.permissions.ActorType.USER;

/**
 * Identifies an entity that is authorized to perform operations on a CredHub credential.
 *
 * @author Scott Frederick
 */
public class Actor {
	private ActorType authType;
	private String primaryIdentifier;

	/**
	 * Create a new {@literal Actor}.
	 *
	 * @param actorType the type of the authorized entity
	 * @param primaryIdentifier the unique identifier of the authorized entity
	 */
	private Actor(ActorType actorType, String primaryIdentifier) {
		this.authType = actorType;
		this.primaryIdentifier = primaryIdentifier;
	}

	/**
	 * Create an application identifier. An application is identified by a GUID generated
	 * by Cloud Foundry when the application is created.
	 *
	 * @param appId the Cloud Foundry application GUID
	 * @return the created {@literal Actor}
	 */
	public static Actor app(String appId) {
		Assert.notNull(appId, "appId must not be null");
		return new Actor(APP, appId);
	}

	/**
	 * Create a user identifier. A user is identified by a GUID generated by UAA when
	 * a user account is created.
	 *
	 * @param userId the UAA user GUID
	 * @return the created {@literal Actor}
	 */
	public static Actor user(String userId) {
		Assert.notNull(userId, "userId must not be null");
		return new Actor(USER, userId);
	}

	/**
	 * Create a user identifier. A user is identified by a GUID generated by UAA when
	 * a user account is created and the ID of the identity zone the user was created in.
	 *
	 * @param zoneId the UAA identity zone ID
	 * @param userId the UAA user GUID
	 * @return the created {@literal Actor}
	 */
	public static Actor user(String zoneId, String userId) {
		Assert.notNull(zoneId, "zoneId must not be null");
		Assert.notNull(userId, "userId must not be null");
		return new Actor(USER, zoneId + "/" + userId);
	}

	/**
	 * Create an OAuth2 client identifier. A client identified by user-provided identifier.
	 *
	 * @param clientId the UAA client ID
	 * @return the created {@literal Actor}
	 */
	public static Actor client(String clientId) {
		Assert.notNull(clientId, "clientId must not be null");
		return new Actor(OAUTH_CLIENT, clientId);
	}

	/**
	 * Create an OAuth2 client identifier. A client identified by user-provided identifier
	 * and the ID of the identity zone the client was created in.
	 *
	 * @param zoneId the UAA identity zone ID
	 * @param clientId the UAA client ID
	 * @return the created {@literal Actor}
	 */
	public static Actor client(String zoneId, String clientId) {
		Assert.notNull(zoneId, "zoneId must not be null");
		Assert.notNull(clientId, "clientId must not be null");
		return new Actor(OAUTH_CLIENT, zoneId + "/" + clientId);
	}

	/**
	 * Get the type of the authorized entity.
	 *
	 * @return the entity type
	 */
	public ActorType getAuthType() {
		return authType;
	}

	/**
	 * Get the identity of the authorized entity.
	 *
	 * @return the identifier
	 */
	public String getPrimaryIdentifier() {
		return primaryIdentifier;
	}

	/**
	 * Get the full identifier for the authorized entity, which is a combination of the type and identity.
	 *
	 * @return the full identifier
	 */
	@JsonValue
	public String getIdentity() {
		return authType.getType() + ":" + primaryIdentifier;
	}

	@JsonCreator
	private static Actor createActor(String value) {
		for (ActorType type : ActorType.values()) {
			if (value.startsWith(type.getType())) {
				return new Actor(type, value.substring(type.getType().length() + 1));
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return "Actor{" +
				"authType=" + authType +
				", primaryIdentifier='" + primaryIdentifier + '\'' +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Actor)) return false;

		Actor actor = (Actor) o;

		if (authType != actor.authType) return false;
		return primaryIdentifier.equals(actor.primaryIdentifier);
	}

	@Override
	public int hashCode() {
		int result = authType.hashCode();
		result = 31 * result + primaryIdentifier.hashCode();
		return result;
	}
}

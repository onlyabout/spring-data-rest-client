package org.springframework.data.rest.example.builder;

import org.springframework.data.rest.example.Profile;

public class ProfileBuilder {

	public static ProfileBuilder aProfile() {
		return new ProfileBuilder();
	}

	private String type = "a type of profile";
	private String url = "http://www.profileurl.com";

	public ProfileBuilder withType(String type) {
		this.type = type;
		return this;
	}

	public ProfileBuilder withUrl(String url) {
		this.url = url;
		return this;
	}

	public Profile build() {
		return new Profile(type, url);
	}
}

package org.springframework.data.rest.example.builder;

import static java.util.Arrays.*;
import static org.springframework.data.rest.example.builder.AddressBuilder.*;
import static org.springframework.data.rest.example.builder.ProfileBuilder.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.rest.example.Address;
import org.springframework.data.rest.example.Person;
import org.springframework.data.rest.example.Profile;
import org.springframework.util.Assert;

public class PersonBuilder {

	public static PersonBuilder aPerson() {
		return new PersonBuilder();
	}

	private String name = "Jongjin Han";
	private List<Address> addresses = asList(anAddress().build());
	@SuppressWarnings("serial")
	private Map<String, Profile> profiles = new HashMap<String, Profile>() {
		{
			put("key", aProfile().build());
		}
	};

	public PersonBuilder withName(String name) {
		this.name = name;
		return this;
	}

	public PersonBuilder withAddresses(AddressBuilder... addresses) {
		this.addresses = new ArrayList<Address>();

		for (AddressBuilder each : addresses) {
			this.addresses.add(each.build());
		}

		return this;
	}

	public PersonBuilder withProfiles(Object... keysAndProfiles) {
		Assert.isTrue(keysAndProfiles.length % 2 == 0);

		this.profiles = new HashMap<String, Profile>();

		for (int i = 0; i < keysAndProfiles.length; i = i + 2) {
			this.profiles.put((String) keysAndProfiles[i], ((ProfileBuilder) keysAndProfiles[i + 1]).build());
		}

		return this;
	}

	public Person build() {
		return new Person(name, addresses, profiles);
	}

}

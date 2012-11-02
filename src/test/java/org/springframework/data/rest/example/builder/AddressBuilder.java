package org.springframework.data.rest.example.builder;

import org.springframework.data.rest.example.Address;

public class AddressBuilder {

	public static AddressBuilder anAddress() {
		return new AddressBuilder();
	}

	private String[] lines = {"1111", "coder's street"};
	private String city = "San Diego";
	private String province = "California";
	private String postalCode = "60707";

	public AddressBuilder withLines(String... lines) {
		this.lines = lines;
		return this;
	}

	public AddressBuilder withCity(String city) {
		this.city = city;
		return this;
	}

	public AddressBuilder withProvince(String province) {
		this.province = province;
		return this;
	}

	public AddressBuilder withPostalCode(String postalCode) {
		this.postalCode = postalCode;
		return this;
	}

	public Address build() {
		return new Address(lines, city, province, postalCode);
	}
}

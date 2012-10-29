package net.daum.clix.springframework.data.rest.client.http.request;

import net.daum.clix.springframework.data.rest.client.http.RestUrlBuilder;
import net.daum.clix.springframework.data.rest.client.repository.RestRepositories;

import org.springframework.util.Assert;

@Deprecated
public class RestRequestSupport implements RestRequest {

	protected RestRepositories repositories;
	private Class<?> domainClass;

	protected RestUrlBuilder urlbuilder;

	public RestRequestSupport(RestRepositories repositories, Class<?> domainClass) {
		Assert.notNull(domainClass);

		this.repositories = repositories;
		this.domainClass = domainClass;

	}

	public String getUrlWithPath(String restServerUrl) {
		return restServerUrl + path();
	}

	public String path() {
		return path(this.domainClass);
	}

	protected String path(Class<?> domainClass) {
		return urlbuilder.build(domainClass);
	}


}

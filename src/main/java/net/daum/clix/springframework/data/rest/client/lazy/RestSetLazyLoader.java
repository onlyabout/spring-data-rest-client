package net.daum.clix.springframework.data.rest.client.lazy;

import org.springframework.util.Assert;

import net.daum.clix.springframework.data.rest.client.http.RestClient;
import net.sf.cglib.proxy.LazyLoader;

public class RestSetLazyLoader implements LazyLoader {

	private RestClient restClient;
	private String href;
	private Class<?> valueType;

	public RestSetLazyLoader(RestClient restClient, String href, Class<?> valueType) {
		Assert.notNull(restClient);
		Assert.notNull(href);
		Assert.notNull(valueType);
		
		this.restClient = restClient;
		this.href = href;
		this.valueType = valueType;
	}

	public Object loadObject() throws Exception {
		return restClient.getForSet(href, valueType);
	}

}
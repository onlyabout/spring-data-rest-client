package net.daum.clix.springframework.data.rest.client.lazy;

import org.springframework.util.Assert;

import net.daum.clix.springframework.data.rest.client.http.RestClient;
import net.sf.cglib.proxy.LazyLoader;

public class RestMapLazyLoader implements LazyLoader {

	private RestClient restClient;
	private String href;
	private Class<?> keyType;
	private Class<?> valueType;

	public RestMapLazyLoader(RestClient restClient, String href, Class<?> keyType, Class<?> valueType) {
		Assert.notNull(restClient);
		Assert.notNull(href);
		Assert.notNull(keyType);
		Assert.notNull(valueType);
		
		this.restClient = restClient;
		this.href = href;
		this.keyType = keyType;
		this.valueType = valueType;
	}

	public Object loadObject() throws Exception {
		return restClient.getForMap(href, keyType, valueType);
	}

}

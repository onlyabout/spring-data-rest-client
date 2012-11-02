package net.daum.clix.springframework.data.rest.client.lazy;

import net.daum.clix.springframework.data.rest.client.http.RestClient;
import net.sf.cglib.proxy.LazyLoader;

public class RestCollectionLazyLoader implements LazyLoader {

	private RestClient restClient;
	private String href;
	private Class<?> type;

	public RestCollectionLazyLoader(RestClient restClient, String href, Class<?> type) {
		this.restClient = restClient;
		this.href = href;
		this.type = type;
	}

	public Object loadObject() throws Exception {
		return restClient.getForList(href, type);
	}

}

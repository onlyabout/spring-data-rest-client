package net.daum.clix.springframework.data.rest.client.lazy;

import net.daum.clix.springframework.data.rest.client.http.RestClient;
import net.sf.cglib.proxy.LazyLoader;

public class RestListLazyLoader implements LazyLoader {

	private RestClient restClient;
	private Class<?> type;
	private String href;

	public RestListLazyLoader(RestClient restClient, String href, Class<?> type) {
		this.restClient = restClient;
		this.type = type;
		this.href = href;
	}

	public Object loadObject() throws Exception {
		return restClient.getForList(href, type);
	}

}

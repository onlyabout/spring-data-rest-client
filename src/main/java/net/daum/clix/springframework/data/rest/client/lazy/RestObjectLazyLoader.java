package net.daum.clix.springframework.data.rest.client.lazy;

import net.daum.clix.springframework.data.rest.client.http.RestClient;
import net.daum.clix.springframework.data.rest.client.metadata.RestEntityInformation;
import net.daum.clix.springframework.data.rest.client.metadata.RestEntityInformationSupport;
import net.sf.cglib.proxy.LazyLoader;

import org.springframework.util.Assert;

public class RestObjectLazyLoader implements LazyLoader {

	private RestClient restClient;
	private Class<?> type;
	private String href;

	public RestObjectLazyLoader(RestClient restClient, Class<?> type, String href) {
		Assert.notNull(restClient);
		Assert.notNull(type);
		Assert.notNull(href);

		this.restClient = restClient;
		this.type = type;
		this.href = href;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object loadObject() throws Exception {
		return restClient.getForObjectForLocation((RestEntityInformation) RestEntityInformationSupport.getMetadata(type), href);
	}

}

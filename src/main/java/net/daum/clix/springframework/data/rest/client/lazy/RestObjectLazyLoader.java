package net.daum.clix.springframework.data.rest.client.lazy;

import org.springframework.util.Assert;

import net.daum.clix.springframework.data.rest.client.http.RestClient;
import net.daum.clix.springframework.data.rest.client.metadata.RestEntityInformation;
import net.daum.clix.springframework.data.rest.client.metadata.RestEntityInformationSupport;
import net.daum.clix.springframework.data.rest.client.repository.SimpleRestRepository;
import net.daum.clix.springframework.data.rest.client.util.RestUrlUtil;
import net.sf.cglib.proxy.LazyLoader;

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

		SimpleRestRepository repo = new SimpleRestRepository(
				(RestEntityInformation) RestEntityInformationSupport.getMetadata(type), restClient);

		return repo.findOne(RestUrlUtil.getIdFrom(href));
	}

}

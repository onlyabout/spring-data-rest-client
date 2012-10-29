package net.daum.clix.springframework.data.rest.client.lazy;

import net.daum.clix.springframework.data.rest.client.http.RestClient;
import net.daum.clix.springframework.data.rest.client.metadata.RestEntityInformation;
import net.daum.clix.springframework.data.rest.client.metadata.RestEntityInformationSupport;
import net.daum.clix.springframework.data.rest.client.repository.SimpleRestRepository;
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object loadObject() throws Exception {
		SimpleRestRepository repo = new SimpleRestRepository(
				(RestEntityInformation) RestEntityInformationSupport.getMetadata(type), restClient);

		return repo.findAll();
	}

}

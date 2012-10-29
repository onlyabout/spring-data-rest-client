package net.daum.clix.springframework.data.rest.client.repository;

import java.io.Serializable;

import net.daum.clix.springframework.data.rest.client.http.RestClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactoryBeanSupport;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

public class RestRepositoryFactoryBean<T extends Repository<S, ID>, S, ID extends Serializable> extends
		RepositoryFactoryBeanSupport<T, S, ID> {

	private RestClient restClient;

	@Autowired
	public void setDataRestClient(RestClient restClient) {
		this.restClient = restClient;
	}

	@Override
	protected RepositoryFactorySupport createRepositoryFactory() {
		return new RestRepositoryFactory(restClient);
	}

}
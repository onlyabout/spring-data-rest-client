package net.daum.clix.springframework.data.rest.client.repository;

import java.io.Serializable;

import net.daum.clix.springframework.data.rest.client.http.RestClient;
import net.daum.clix.springframework.data.rest.client.metadata.RestEntityInformation;
import net.daum.clix.springframework.data.rest.client.metadata.RestEntityInformationSupport;
import net.daum.clix.springframework.data.rest.client.query.RestQueryLookupStrategy;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.QueryLookupStrategy.Key;

public class RestRepositoryFactory extends RepositoryFactorySupport {

	private RestClient restClient;

	public RestRepositoryFactory(RestClient restClient) {
		this.restClient = restClient;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected Object getTargetRepository(RepositoryMetadata metadata) {
		return new SimpleRestRepository((RestEntityInformation) getEntityInformation(metadata.getDomainType()),
				restClient);
	}

	@Override
	public <T, ID extends Serializable> EntityInformation<T, ID> getEntityInformation(Class<T> domainClass) {
		return RestEntityInformationSupport.getMetadata(domainClass);
	}

	@Override
	protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
		Class<?> repositoryInterface = metadata.getRepositoryInterface();
		if (PagingAndSortingRepository.class.isAssignableFrom(repositoryInterface)) {
			return PagingAndSortingRepository.class;
		} else {
			return CrudRepository.class;
		}
	}

	@Override
	protected QueryLookupStrategy getQueryLookupStrategy(Key key) {
		return new RestQueryLookupStrategy(restClient);
	}

}

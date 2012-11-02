package net.daum.clix.springframework.data.rest.client.query;

import java.lang.reflect.Method;

import net.daum.clix.springframework.data.rest.client.http.RestClient;

import org.springframework.data.repository.core.NamedQueries;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.RepositoryQuery;

public class RestQueryLookupStrategy implements QueryLookupStrategy {

	private RestClient restClient;

	public RestQueryLookupStrategy(RestClient restClient) {
		this.restClient = restClient;
	}

	@SuppressWarnings({ "rawtypes" })
	public final RepositoryQuery resolveQuery(Method method, RepositoryMetadata metadata, NamedQueries namedQueries) {
		return new RestRepositoryQueryExecutor(restClient, method, metadata);
	}

}

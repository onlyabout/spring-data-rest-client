package net.daum.clix.springframework.data.rest.client.query;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;

import net.daum.clix.springframework.data.rest.client.http.RestClient;

import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.repository.query.RepositoryQuery;

public class RestRepositoryQueryExecutor<T, ID extends Serializable> implements RepositoryQuery {

	private RestClient restClient;

	private Method method;

	private RepositoryMetadata metadata;

	public RestRepositoryQueryExecutor(RestClient restClient, Method method, RepositoryMetadata metadata) {
		this.restClient = restClient;
		this.method = method;
		this.metadata = metadata;
	}

	public Object execute(Object[] parameters) {

		if (List.class.isAssignableFrom(method.getReturnType())) {
			return restClient.queryForList(metadata.getDomainType(), method, parameters);
		}

		throw new IllegalAccessError("RestRepositoryQuery#execute for return type " + method.getReturnType()
				+ " has not implemented yet!");
	}

	public QueryMethod getQueryMethod() {
		return new QueryMethod(method, metadata);
	}

}

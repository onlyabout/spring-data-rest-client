package net.daum.clix.springframework.data.rest.client.http.request;

import net.daum.clix.springframework.data.rest.client.repository.RestRepositories;

import org.springframework.data.rest.repository.invoke.CrudMethod;

@Deprecated
public class RequestFactory {

	private RestRepositories repositories;
	private Class<?> domainType;

	public RequestFactory(RestRepositories repositories, Class<?> domainType) {
		this.repositories = repositories;
		this.domainType = domainType;
	}
	
	public RestRequest create(CrudMethod crudMethod, Object... entities) {
		if (CrudMethod.SAVE_ONE.equals(crudMethod)) {
			return new SaveOneRequest(repositories, domainType, entities);
		}
		
		throw new UnsupportedOperationException("Unsupported method : " + crudMethod.name());
	}

}

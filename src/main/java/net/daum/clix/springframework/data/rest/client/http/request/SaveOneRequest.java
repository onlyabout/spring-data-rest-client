package net.daum.clix.springframework.data.rest.client.http.request;

import net.daum.clix.springframework.data.rest.client.repository.RestRepositories;

import org.springframework.util.Assert;

@Deprecated
public class SaveOneRequest extends RestRequestSupport {
	
	@SuppressWarnings("unused")
	private Object entity;

	public SaveOneRequest(RestRepositories repositories, Class<?> domainClass, Object entity) {
		super(repositories, domainClass);
		Assert.notNull(entity);
		this.entity = entity;
	}

	/**
	 * Create a new request instance.
	 * 
	 * @param entity
	 * @return 
	 */
	public SaveOneRequest create(Object entity) {
		return new SaveOneRequest(repositories, entity.getClass(), entity);
	}

}

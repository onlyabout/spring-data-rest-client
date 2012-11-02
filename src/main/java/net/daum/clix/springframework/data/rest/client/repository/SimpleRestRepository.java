package net.daum.clix.springframework.data.rest.client.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.daum.clix.springframework.data.rest.client.http.RestClient;
import net.daum.clix.springframework.data.rest.client.metadata.RestEntityInformation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Default implementation of the
 * {@link org.springframework.data.repository.CrudRepository} interface.
 * 
 * @author 84june
 * 
 * @param <T>
 *            the type of the entity to handle
 * @param <ID>
 *            the type of the entity's identifier
 */
@org.springframework.stereotype.Repository
public class SimpleRestRepository<T, ID extends Serializable> implements PagingAndSortingRepository<T, ID> {

	private RestClient restClient;
	private RestEntityInformation<T, ID> entityInformation;

	public SimpleRestRepository(RestEntityInformation<T, ID> entityInformation, RestClient restClient) {
		this.entityInformation = entityInformation;
		this.restClient = restClient;
	}

	public T findOne(ID id) {
		return restClient.getForObject(entityInformation, id);
	}

	/**
	 * Save the given entity by using http post or put(when a field with @Id appeared and is not null) method.
	 * 
	 * @param entity Entity to save
	 * @return The saved entity(will be lazily loaded). Can be null if request failed.
	 */
	public <S extends T> S save(S entity) {
		return restClient.saveForObject(entity);
	}

	public <S extends T> Iterable<S> save(Iterable<S> entities) {
		return restClient.saveForObjects(entities);
	}

	public boolean exists(ID id) {
		return findOne(id) != null;
	}

	public long count() {
		return restClient.count(entityInformation);
	}

	public void delete(ID id) {
		restClient.delete(entityInformation, id);
	}

	public void delete(T entity) {
		delete(entityInformation.getId(entity));
	}

	@SuppressWarnings("unchecked")
	public void delete(Iterable<? extends T> entities) {
		for (Object entity : entities)
			delete(entityInformation.getId((T) entity));
	}

	public void deleteAll() {
		restClient.deleteAll(entityInformation);
	}
	
	public Iterable<T> findAll() {
		return restClient.getForIterable(entityInformation);
	}

	public Iterable<T> findAll(Iterable<ID> ids) {
		List<T> list = new ArrayList<T>();
		for (ID id : ids)
			list.add(findOne(id));

		return list;
	}

	public Iterable<T> findAll(Sort sort) {
		return restClient.getForIterable(entityInformation, sort);
	}

	public Page<T> findAll(Pageable pageable) {
		return restClient.getForPageable(entityInformation, pageable);
	}

}

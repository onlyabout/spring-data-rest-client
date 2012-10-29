package net.daum.clix.springframework.data.rest.client.http;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.daum.clix.springframework.data.rest.client.lazy.ProxyCreator;
import net.daum.clix.springframework.data.rest.client.lazy.RestIterableLoader;
import net.daum.clix.springframework.data.rest.client.metadata.RestEntityInformation;
import net.daum.clix.springframework.data.rest.client.metadata.RestEntityInformationSupport;
import net.daum.clix.springframework.data.rest.client.repository.RestRepositories;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.Resources;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

public abstract class RestClientBase implements RestClient, ApplicationContextAware {

	private ApplicationContext applicationContext;

	private String restServerUrl;

	private RestUrlBuilder urlBuilder;

	private RestRepositories repositories;

	public RestClientBase(String restServerUrl) {
		Assert.notNull(restServerUrl);
		this.restServerUrl = restServerUrl;
	}

	@SuppressWarnings("unchecked")
	public <T, ID extends Serializable> T getForObject(RestEntityInformation<T, ID> entityInfo, ID id) {
		refresh();

		String url = urlBuilder.build(entityInfo.getJavaType(), id);

		Resource<T> resource = (Resource<T>) executeGet(url, Resource.class, entityInfo.getJavaType());

		T entity = getLazyLoadingObjectFrom(resource, entityInfo);

		if (entity != null) {
			entityInfo.setId(entity, id);
		}

		return entity;
	}

	@SuppressWarnings("unchecked")
	public <T, ID extends Serializable> long count(RestEntityInformation<T, ID> entityInfo) {
		refresh();

		String url = urlBuilder.build(entityInfo.getJavaType());

		PagedResources<T> resource = (PagedResources<T>) executeGet(url, PagedResources.class, entityInfo.getJavaType());

		return resource.getMetadata().getTotalElements();
	}

	public <T, ID extends Serializable> void delete(RestEntityInformation<T, ID> entityInfo, ID id) {
		refresh();

		String url = urlBuilder.build(entityInfo.getJavaType(), id);

		executeDelete(url);
	}

	public <T, ID extends Serializable> void deleteAll(RestEntityInformation<T, ID> entityInfo) {
		refresh();

		String url = urlBuilder.build(entityInfo.getJavaType());

		executeDelete(url);
	}

	@SuppressWarnings("unchecked")
	public <S> S saveForObject(S entity) {
		refresh();

		String url = urlBuilder.build(entity.getClass());

		// for (Field field : entity.getClass().getDeclaredFields()) {
		// if (field.isAnnotationPresent(OneToOne.class)) {
		// field.setAccessible(true);
		// Object o = ReflectionUtils.getField(field, entity);
		//
		// Object saved = saveForObject(o);
		// ReflectionUtils.setField(field, entity, saved);

		// } else if (field.isAnnotationPresent(OneToMany.class)) {
		// field.setAccessible(true);
		// Object o = ReflectionUtils.getField(field, entity);

		// if (!Iterable.class.isAssignableFrom(o.getClass())) {

		// }

		// Object saved = saveForObjects((Iterable<S>) o);
		// ReflectionUtils.setField(field, entity, saved);
		// }
		// }

		return (S) ProxyCreator.createObject(this, executePost(url, entity), entity.getClass());
	}

	@SuppressWarnings("unchecked")
	public <S> Iterable<S> saveForObjects(Iterable<S> entities) {
		List<S> saved = new ArrayList<S>();
		for (S s : entities)
			saved.add(saveForObject(s));

		return (Iterable<S>) saved.iterator();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T, ID extends Serializable> Iterable<T> getForIterable(RestEntityInformation<T, ID> entityInfo) {
		refresh();

		String url = urlBuilder.build(entityInfo.getJavaType());
		if (isPageableRepository(entityInfo.getJavaType()))
			return new RestIterableLoader(this, url, entityInfo.getJavaType());
		
		return getForList(url, entityInfo.getJavaType());
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> getForList(String href, Class<T> type) {
		if (isPageableRepository(type)) {
			throw new UnsupportedOperationException("findAll for PagedAndSortingRepository is not supported.");
		} else {
			Resources<Resource<T>> res = (Resources<Resource<T>>) executeGet(href, Resources.class, type);
			return resourcesToIterable(res);
		}
	}
	
	// TODO : paging 파라메터가 있을 경우에 대한 처리
	@SuppressWarnings("unchecked")
	public <T> List<T> queryForList(String query, Class<T> type) {
		String href = urlBuilder.buildQueryUrl(query, type);
		Resources<Resource<T>> res = (Resources<Resource<T>>) executeGet(href, Resources.class, type);
		return resourcesToIterable(res);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <K, V> Map<K, V> getForMap(String href, Class<K> keyType, Class<V> valueType) {
		Resource<Map<K, Resource<V>>> resource = executeGetForMap(href, Resource.class, keyType, valueType);

		Map<K, Resource<V>> content = resource.getContent();
		Map<K, V> lazyObjectsMap = new HashMap<K, V>();

		for (K key : content.keySet()) {
			V value = (V) getLazyLoadingObjectFrom(content.get(key), (RestEntityInformation) RestEntityInformationSupport.getMetadata(valueType));
			lazyObjectsMap.put(key, value);
		}

		return lazyObjectsMap;
	}

	@SuppressWarnings("unchecked")
	public <T, ID extends Serializable> Iterable<T> getForIterable(RestEntityInformation<T, ID> entityInfo, Sort sort) {
		refresh();

		String url = urlBuilder.buildWithParameters(entityInfo.getJavaType(), sort);

		PagedResources<Resource<T>> resources = (PagedResources<Resource<T>>) executeGet(url, PagedResources.class,
				entityInfo.getJavaType());

		return resourcesToIterable(resources);
	}

	@SuppressWarnings("unchecked")
	public <T, ID extends Serializable> Page<T> getForPageable(RestEntityInformation<T, ID> entityInfo,
			Pageable pageable) {
		refresh();

		String url = urlBuilder.buildWithParameters(entityInfo.getJavaType(), pageable);
		PagedResources<Resource<T>> resources = (PagedResources<Resource<T>>) executeGet(url, PagedResources.class,
				entityInfo.getJavaType());

		if (pageable == null) {
			Long number = resources.getMetadata().getNumber();
			Long size = resources.getMetadata().getSize();
			pageable = new PageRequest(number.intValue(), size.intValue());
		}

		return new PageImpl<T>(resourcesToIterable(resources), pageable, resources.getMetadata().getTotalElements());
	}

	private boolean isPageableRepository(Class<?> domainClass) {
		return PagingAndSortingRepository.class.isAssignableFrom(repositories.getRepositoryFor(domainClass).getClass());
	}

	private <T, ID extends Serializable> List<T> resourcesToIterable(Resources<Resource<T>> resources) {
		List<T> list = new ArrayList<T>();

		for (Resource<T> r : (Collection<Resource<T>>) resources.getContent()) {
			list.add(r.getContent());
		}

		return list;
	}

	private <T, ID extends Serializable> T getLazyLoadingObjectFrom(Resource<T> resource,
			RestEntityInformation<T, ID> entityInfo) {
		if (resource == null)
			return null;

		T entity = resource.getContent();

		if (entity == null)
			return null;

		for (Link link : resource.getLinks()) {
			if (Link.REL_SELF.equals(link.getRel()))
				continue;

			Field field = entityInfo.findFieldByRel(link.getRel());
			
			// A link rel like people.Person.profiles.Profile should not be found.
			if (field == null)
				continue;
			
			Object lazyObjecy = ProxyCreator.create(this, link.getHref(), field);
			ReflectionUtils.makeAccessible(field);
			ReflectionUtils.setField(field, entity, lazyObjecy);
		}

		return entity;
	}

	public abstract ResourceSupport executeGet(String url, Type resourceType, Type objectType);

	protected abstract <K, V> Resource<Map<K, Resource<V>>> executeGetForMap(String url, Type resourceType,
			Type keyType, Type valueType);

	protected abstract void executeDelete(String url);

	/**
	 * 
	 * @param url
	 * @param entity
	 * @return Returns a href for saved entity from response header "Location".
	 *         Returned string can be null if request failed.
	 */
	protected abstract <S> String executePost(String url, S entity);

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	private void refresh() {
		Assert.notNull(applicationContext);

		if (null != repositories)
			return;

		repositories = new RestRepositories(applicationContext);
		urlBuilder = new RestUrlBuilder(restServerUrl, repositories);
	}

}

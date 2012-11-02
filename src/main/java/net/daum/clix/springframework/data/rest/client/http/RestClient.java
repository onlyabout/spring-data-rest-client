package net.daum.clix.springframework.data.rest.client.http;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import net.daum.clix.springframework.data.rest.client.metadata.RestEntityInformation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public interface RestClient {
	
	<T, ID extends Serializable> T getForObjectForLocation(RestEntityInformation<T, ID> entityInfo, String url);

	<T, ID extends Serializable> T getForObject(RestEntityInformation<T, ID> domainType, ID id);

	<T, ID extends Serializable> long count(RestEntityInformation<T, ID> entityInfo);

	<T, ID extends Serializable> void delete(RestEntityInformation<T, ID> entityInfo, ID id);

	<T, ID extends Serializable> void deleteAll(RestEntityInformation<T, ID> entityInfo);

	<S> S saveForObject(S entity);

	<S> Iterable<S> saveForObjects(Iterable<S> entities);

	<T, ID extends Serializable> Iterable<T> getForIterable(RestEntityInformation<T, ID> entityInfo);

	<T, ID extends Serializable> Iterable<T> getForIterable(RestEntityInformation<T, ID> entityInfo, Sort sort);

	<T, ID extends Serializable> Page<T> getForPageable(RestEntityInformation<T, ID> entityInfo, Pageable pageable);

	<T> List<T> getForList(String href, Class<T> type);

	<K, V> Map<K, V> getForMap(String href, Class<K> keyType, Class<V> valueType);
	
	<T> List<T> queryForList(Class<T> type, Method queryMethod, Object[] parameters);

}

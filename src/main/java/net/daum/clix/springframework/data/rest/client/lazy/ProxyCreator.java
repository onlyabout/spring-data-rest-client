package net.daum.clix.springframework.data.rest.client.lazy;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.daum.clix.springframework.data.rest.client.http.RestClient;
import net.sf.cglib.proxy.Enhancer;

/**
 * Create a proxy object properly by using given type.
 * OneToOne or OneToMany(Refering to JPA specification (Section 2.1.1 pg19), List, Set, Map or Collection are supported.) relation.
 * 
 * @author 84june
 */
public class ProxyCreator {
	
	public static Object createObject(RestClient restClient, String href, Class<?> type) {
		return Enhancer.create(type, new RestObjectLazyLoader(restClient, type, href));
	}
	
	public static Object create(RestClient restClient, String href, Field field) {
		Class<?> type = field.getType();

		if (List.class.isAssignableFrom(type)) {
			return createList(restClient, href, field);
		}

		if (Set.class.isAssignableFrom(type)) {
			return createSet(restClient, href, field);
		}

		if (Map.class.isAssignableFrom(type)) {
			return createMap(restClient, href, field);
		}

		if (Collection.class.isAssignableFrom(type)) {
			return createCollection(restClient, href, field);
		}

		return createObject(restClient, href, type);
	}

	private static Object createList(RestClient restClient, String href, Field field) {
		return Enhancer.create(field.getType(), new RestListLazyLoader(restClient, href, getGenericTypeFrom(field)));
	}

	private static Object createSet(RestClient restClient, String href, Field field) {
		return Enhancer.create(field.getType(), new RestSetLazyLoader(restClient, href, getGenericTypeFrom(field)));
	}

	private static Object createMap(RestClient restClient, String href, Field field) {
		Class<?> keyType = getGenericTypeFrom(field, 0);
		Class<?> valueType = getGenericTypeFrom(field, 1);
		
		return Enhancer.create(field.getType(), new RestMapLazyLoader(restClient, href, keyType, valueType));
	}

	private static Object createCollection(RestClient restClient, String href, Field field) {
		return Enhancer.create(field.getType(), new RestCollectionLazyLoader(restClient, href, getGenericTypeFrom(field)));
	}
	
	private static Class<?> getGenericTypeFrom(Field field) {
		return getGenericTypeFrom(field, 0);
	}

	@SuppressWarnings("rawtypes")
	private static Class<?> getGenericTypeFrom(Field field, int indexOfTypeArguments) {
		if (ParameterizedType.class.isAssignableFrom(field.getGenericType().getClass())) {
			ParameterizedType genericType = (ParameterizedType) field.getGenericType();
			Class clazz = ((Class) (genericType.getActualTypeArguments()[indexOfTypeArguments]));
			return clazz;
		}

		throw new IllegalAccessError("Given field must be Generic type.\t" + field.getType());
	}

}

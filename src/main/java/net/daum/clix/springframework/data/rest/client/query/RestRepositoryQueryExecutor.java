package net.daum.clix.springframework.data.rest.client.query;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

import net.daum.clix.springframework.data.rest.client.http.RestClient;
import net.daum.clix.springframework.data.rest.client.metadata.RestEntityInformation;

import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.data.rest.repository.annotation.RestResource;
import org.springframework.util.StringUtils;

public class RestRepositoryQueryExecutor<T, ID extends Serializable> implements RepositoryQuery {

	private RestClient restClient;

	private Method method;

	private RepositoryMetadata metadata;

	private RestEntityInformation<T, ID> entityInformation;

	public RestRepositoryQueryExecutor(RestClient restClient, Method method, RepositoryMetadata metadata,
			RestEntityInformation<T, ID> entityInformation) {
		this.restClient = restClient;
		this.method = method;
		this.metadata = metadata;
		this.entityInformation = entityInformation;
	}

	public Object execute(Object[] parameters) {

		String queryName = getQueryName();
		String queryParam = getQueryParameters(parameters);
		
		if (List.class.isAssignableFrom(method.getReturnType())) {
			return restClient.queryForList(queryName + queryParam, entityInformation.getJavaType());
		}

		throw new IllegalAccessError("RestRepositoryQuery#execute for return type " + method.getReturnType()
				+ " has not implemented yet!");
	}

	public QueryMethod getQueryMethod() {
		return new RestQueryMethod(method, metadata);
	}

	private String getQueryName() {
		String path = method.getName();

		RestResource ann = method.getAnnotation(RestResource.class);
		if (ann != null && StringUtils.hasText(ann.path())) {
			path = ann.path();
		}

		return path;
	}

	@SuppressWarnings("unused")
	private String getQueryParameters(Object[] parameters) {
		StringBuilder sb = new StringBuilder();

		Class<?>[] parameterTypes = method.getParameterTypes();
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();

		for (int i = 0; i < parameterTypes.length; i++) {

			Class<?> param = parameterTypes[i];
			Annotation[] anns = parameterAnnotations[i];

			for (int j = 0; j < anns.length; j++) {
				Annotation ann = anns[j];
				if (ann instanceof Param) {

					Param paramAnn = (Param) ann;

					if (ann != null && StringUtils.hasText(paramAnn.value())) {

						String name = paramAnn.value();
						String value = String.valueOf(parameters[i]);

						sb.append(i == 0 ? "?" : "&");
						sb.append(name);
						sb.append("=");
						sb.append(value);
					}
				}
			}
		}

		return sb.toString();
	}

}

package net.daum.clix.springframework.data.rest.client.http;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import net.daum.clix.springframework.data.rest.client.repository.RestRepositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.repository.annotation.RestResource;
import org.springframework.util.StringUtils;

public class RestUrlBuilder {

	private RestRepositories repositories;
	private String restServerUrl;
	private ParameterBuilder pBuilder;

	public RestUrlBuilder(String restServerUrl, RestRepositories repositories) {
		this.restServerUrl = restServerUrl;
		this.repositories = repositories;
		this.pBuilder = new ParameterBuilder();
	}

	public String build(Class<?> domainClass) {
		return generateUrl(getResourcePathProperly(domainClass));
	}

	public String build(Class<?> domainClass, Serializable id) {
		return generateUrl(getResourcePathProperly(domainClass), id);
	}

	private String getResourcePathProperly(Class<?> domainClass) {
		String resourcePath = repositories.getRepositoryNameFor(domainClass);
		RestResource resourceAnno = repositories.getRestResourceAnnotationFor(domainClass);
		if (null != resourceAnno) {
			if (StringUtils.hasText(resourceAnno.path())) {
				resourcePath = resourceAnno.path();
			}
		}

		return resourcePath;
	}

	private String generateUrl(Object... path) {
		StringBuilder sb = new StringBuilder(restServerUrl);
		for (Object s : path) {
			sb.append("/");
			sb.append(s);
		}

		return sb.toString();
	}

	public <T> String buildWithParameters(Class<T> domainClass, Object... parameters) {
		return build(domainClass) + pBuilder.build(parameters);
	}

	public String buildQueryUrl(Class<?> domainClass, Method queryMethod, Object... parameters) {
		return build(domainClass) + "/search/" + getQueryName(queryMethod) + pBuilder.build(queryMethod, parameters);
	}

	private String getQueryName(Method queryMethod) {
		String path = queryMethod.getName();

		RestResource ann = queryMethod.getAnnotation(RestResource.class);
		if (ann != null && StringUtils.hasText(ann.path())) {
			path = ann.path();
		}

		return path;
	}

	private class ParameterBuilder {

		private String build(Object... parameters) {
			return build(null, parameters);
		}

		private String build(Method queryMethod, Object... parameters) {
			Map<String, String> pMap = new TreeMap<String, String>();

			for (int i = 0; i < parameters.length; i++) {
				Object parameter = parameters[i];

				if (Pageable.class.isAssignableFrom(parameter.getClass())) {
					addPageable(pMap, (Pageable) parameter);
				} else if (Sort.class.isAssignableFrom(parameter.getClass())) {
					addSort(pMap, (Sort) parameter);
				} else {
					if (queryMethod == null)
						continue;

					Annotation[] anns = queryMethod.getParameterAnnotations()[i];

					for (int j = 0; j < anns.length; j++) {
						Annotation ann = anns[j];
						if (ann instanceof Param) {

							Param paramAnn = (Param) ann;

							if (ann != null && StringUtils.hasText(paramAnn.value())) {

								String name = paramAnn.value();
								String value = null;
								try {
									value = URLEncoder.encode(String.valueOf(parameter), "UTF-8");
								} catch (UnsupportedEncodingException e) {
									throw new IllegalArgumentException("Query method's parameter must be UTF-8 encoded");
								}
								
								pMap.put(name, value);
							}
						}
					}
				}
			}

			return convertToString(pMap);
		}

		private String convertToString(Map<String, String> pMap) {
			if (pMap == null || pMap.size() == 0)
				return "";

			StringBuilder sb = new StringBuilder();

			int i = 0;
			for (String key : pMap.keySet()) {
				String sep = ((i++ == 0) ? "?" : "&");
				sb.append(sep + key + "=" + pMap.get(key));
			}

			return sb.toString();
		}

		private void addPageable(Map<String, String> pMap, Pageable pageable) {
			if (pageable == null)
				return;

			addSort(pMap, pageable.getSort());

			pMap.put("page", String.valueOf(pageable.getPageNumber()));
			pMap.put("limit", String.valueOf(pageable.getPageSize()));
		}

		private void addSort(Map<String, String> pMap, Sort sort) {
			if (sort == null)
				return;

			Iterator<Sort.Order> it = sort.iterator();

			while (it.hasNext()) {
				Sort.Order order = it.next();

				pMap.put("sort", order.getProperty());
				pMap.put(order.getProperty() + ".dir", order.getDirection().toString());
			}
		}

	}

}

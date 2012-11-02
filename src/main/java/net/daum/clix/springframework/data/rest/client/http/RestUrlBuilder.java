package net.daum.clix.springframework.data.rest.client.http;

import java.io.Serializable;
import java.util.Iterator;

import net.daum.clix.springframework.data.rest.client.repository.RestRepositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.rest.repository.annotation.RestResource;
import org.springframework.util.StringUtils;

public class RestUrlBuilder {

	private RestRepositories repositories;
	private String restServerUrl;

	public RestUrlBuilder(String restServerUrl, RestRepositories repositories) {
		this.restServerUrl = restServerUrl;
		this.repositories = repositories;
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

	public <T> String buildWithParameters(Class<T> domainClass, Pageable pageable) {
		if (pageable == null)
			return build(domainClass);

		String baseUrl = buildWithParameters(domainClass, pageable.getSort());

		StringBuilder sb = new StringBuilder(baseUrl);
		if (baseUrl.indexOf("?") == -1)
			sb.append("?page=" + pageable.getPageNumber());
		else
			sb.append("&page=" + pageable.getPageNumber());
		
		sb.append("&limit=" + pageable.getPageSize());

		return sb.toString();
	}

	public <T> String buildQueryUrl(String query, Class<T> domainClass) {
		return build(domainClass) + "/search/" + query;
	}

	public <T> String buildWithParameters(Class<T> domainClass, Sort sort) {
		if (sort == null)
			return build(domainClass);

		StringBuilder sb = new StringBuilder(build(domainClass));

		Iterator<Sort.Order> it = sort.iterator();
		
		int i = 0;
		while (it.hasNext()) {
			Sort.Order order = it.next();

			if (i == 0)
				sb.append("?sort=" + order.getProperty());
			else 
				sb.append("&sort=" + order.getProperty());
			sb.append("&" + order.getProperty() + ".dir=" + order.getDirection());
		}

		return sb.toString();
	}

}

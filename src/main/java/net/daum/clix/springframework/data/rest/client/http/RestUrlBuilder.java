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
		Class<?> repoClass = repositories.getRepositoryInformationFor(domainClass).getRepositoryInterface();
		String resourcePath = StringUtils.uncapitalize(repoClass.getSimpleName().replaceAll("Repository", ""));
		RestResource resourceAnno = repoClass.getAnnotation(RestResource.class);
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

		StringBuilder sb = new StringBuilder(buildWithParameters(domainClass, pageable.getSort()));
		
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

		StringBuilder sb = new StringBuilder("?");

		Iterator<Sort.Order> it = sort.iterator();

		while (it.hasNext()) {
			Sort.Order order = it.next();

			sb.append("&sort=" + order.getProperty());
			sb.append("&" + order.getProperty() + ".dir=" + order.getDirection());
		}

		return build(domainClass) + sb.toString();
	}

}

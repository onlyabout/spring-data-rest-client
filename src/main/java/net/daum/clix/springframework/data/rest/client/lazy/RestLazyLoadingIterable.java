package net.daum.clix.springframework.data.rest.client.lazy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import net.daum.clix.springframework.data.rest.client.http.RestClient;
import net.daum.clix.springframework.data.rest.client.http.RestClientBase;
import net.daum.clix.springframework.data.rest.client.metadata.RestEntityInformation;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Iterable implementation for PagingAndSortingRepository#findAll() to load data
 * lazily
 * 
 * @author 84june
 * 
 * @param <T>
 */
public class RestLazyLoadingIterable<T, ID extends Serializable> implements Iterable<T> {

	private RestClient restClient;

	private String nextHref;

	private Collection<Resource<T>> collection;

	private RestEntityInformation<T, ID> entityInfo;

	public RestLazyLoadingIterable(RestClientBase restClient, String href, RestEntityInformation<T, ID> entityInfo) {
		this.entityInfo = entityInfo;
		Assert.isAssignable(RestClientBase.class, restClient.getClass());
		Assert.hasText(href);
		Assert.notNull(entityInfo);

		this.restClient = restClient;
		this.nextHref = href;

		loadNext();
	}

	@SuppressWarnings("unchecked")
	private boolean loadNext() {
		PagedResources<Resource<T>> res = (PagedResources<Resource<T>>) ((RestClientBase) restClient).executeGet(
				nextHref, PagedResources.class, entityInfo.getJavaType());

		nextHref = null;
		for (Link link : res.getLinks()) {
			if (link.getRel().endsWith(".next"))
				nextHref = link.getHref();
		}

		collection = res.getContent();

		if (collection == null)
			collection = new ArrayList<Resource<T>>();

		return StringUtils.hasText(nextHref);
	}

	public Iterator<T> iterator() {
		return new LazyLoadingIterator();
	}

	private class LazyLoadingIterator implements Iterator<T> {

		Iterator<Resource<T>> iterator;

		public LazyLoadingIterator() {
			iterator = collection.iterator();
		}

		public boolean hasNext() {
			boolean hasNext = iterator.hasNext();
			if (!hasNext) {
				hasNext = StringUtils.hasText(nextHref);
			}

			return hasNext;
		}

		public T next() {
			if (hasNext() && !iterator.hasNext()) {
				loadNext();
				iterator = collection.iterator();
			}

			Resource<T> resource = iterator.next();
			return (T) ((RestClientBase) restClient).getLazyLoadingObjectFrom(resource, entityInfo);
		}

		public void remove() {
			iterator.remove();
		}

	}
}

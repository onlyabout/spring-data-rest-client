package net.daum.clix.springframework.data.rest.client.lazy;

import java.util.Iterator;
import java.util.List;

import net.daum.clix.springframework.data.rest.client.http.RestClient;
import net.daum.clix.springframework.data.rest.client.http.RestClientBase;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.util.Assert;

/**
 * Iterable implementation for PagingAndSortingRepository#findAll() to load data
 * lazily
 * 
 * @author 84june
 * 
 * @param <T>
 */
public class RestIterableLoader<T> implements Iterable<T> {

	private RestClient restClient;
	
	private Class<T> domainType;
	
	private String nextHref;
	
	private List<T> collection;

	public RestIterableLoader(RestClientBase restClient, String href, Class<T> domainType) {
		Assert.isAssignable(restClient.getClass(), RestClientBase.class);
		Assert.hasText(href);
		Assert.notNull(domainType);

		this.restClient = restClient;
		this.nextHref = href;
		this.domainType = domainType;

		loadNext();
	}

	private void loadNext() {
		PagedResources<T> res = (PagedResources<T>) ((RestClientBase) restClient).executeGet(nextHref,
				PagedResources.class, domainType);
		
		//TODO add to collection
		nextHref = null;
		for (Link link : res.getLinks()) {
			if (link.getRel().endsWith(".next"))
				nextHref = link.getRel();
		}
	}

	public Iterator<T> iterator() {
		return new LazyLoadingIterator();
	}

	private class LazyLoadingIterator implements Iterator<T> {
		
		int pos;
		
		public LazyLoadingIterator() {
			pos = 0;
		}

		public boolean hasNext() {
			return pos < collection.size();
		}

		public T next() {
//			T object = collection.
			throw new IllegalAccessError("Iterator<T>#next has not implemented yet!");
		}

		public void remove() {
			// TODO Auto-generated method stub
			throw new IllegalAccessError("Iterator<T>#remove has not implemented yet!");
		}

	}
}

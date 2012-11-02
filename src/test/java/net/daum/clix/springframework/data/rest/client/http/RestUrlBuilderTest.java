package net.daum.clix.springframework.data.rest.client.http;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import net.daum.clix.springframework.data.rest.client.repository.RestRepositories;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.rest.example.Person;
import org.springframework.data.rest.repository.annotation.RestResource;

@RunWith(MockitoJUnitRunner.class)
public class RestUrlBuilderTest {

	@Mock
	private RestRepositories repositories;

	@Mock
	private RestResource restResource;

	private Class<?> domainClass = Person.class;

	private RestUrlBuilder urlBuilder;

	@Before
	public void setUp() throws Exception {
		urlBuilder = new RestUrlBuilder("http://1.2.3.4", repositories);
		when(repositories.getRepositoryNameFor(domainClass)).thenReturn("person");
		when(repositories.getRestResourceAnnotationFor(domainClass)).thenReturn(restResource);
		when(restResource.path()).thenReturn("people");
	}

	@Test
	public void testBuild() throws Exception {
		String url = urlBuilder.build(domainClass);
		assertEquals("http://1.2.3.4/people", url);
		
		url = urlBuilder.build(domainClass, 1L);
		assertEquals("http://1.2.3.4/people/1", url);
	}

	@Test
	public void testBuildWithParameters() throws Exception {
		String url = urlBuilder.buildWithParameters(domainClass, new PageRequest(1, 10));
		assertEquals("http://1.2.3.4/people?page=1&limit=10", url);
		
		url = urlBuilder.buildWithParameters(domainClass, new Sort(Direction.DESC, "name"));
		assertEquals("http://1.2.3.4/people?sort=name&name.dir=DESC", url);
		
		url = urlBuilder.buildWithParameters(domainClass, new PageRequest(1, 10, new Sort(Direction.DESC, "name")));
		assertEquals("http://1.2.3.4/people?sort=name&name.dir=DESC&page=1&limit=10", url);
	}

	@Test
	public void testBuildQueryUrl() throws Exception {
		String url = urlBuilder.buildQueryUrl("findByName", Person.class);
		assertEquals("http://1.2.3.4/people/search/findByName", url);
	}
}

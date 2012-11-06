package net.daum.clix.springframework.data.rest.client.lazy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.data.rest.example.builder.PersonBuilder.aPerson;

import java.util.Arrays;
import java.util.List;

import net.daum.clix.springframework.data.rest.client.http.RestClient;
import net.sf.cglib.proxy.LazyLoader;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.rest.example.Person;

@RunWith(MockitoJUnitRunner.class)
public class RestListLazyLoaderTest {
	
	@Mock
	private RestClient restClient;
	
	private String href = "http://1.2.3.4/repository/id/list_property_name";
	
	private Class<?> type = Person.class;
	
	private LazyLoader lazyloader;

	private List<Person> listObject;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Before
	public void setUp() throws Exception {
		listObject = Arrays.asList(aPerson().build());
		lazyloader = new RestCollectionLazyLoader(restClient, href, type);
		
		when(restClient.getForList(href, type)).thenReturn((List) listObject);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void lazyLoaderReturnsListObjectForGivenHrefByRestClient() throws Exception {
		Object loadedObject = lazyloader.loadObject();
		
		assertTrue(loadedObject instanceof List);
		assertEquals(1, ((List<Person>)loadedObject).size());
		
		verify(restClient).getForList(href, type);
	}

}

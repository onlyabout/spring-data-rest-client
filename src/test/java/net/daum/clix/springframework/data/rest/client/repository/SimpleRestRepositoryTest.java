package net.daum.clix.springframework.data.rest.client.repository;

import java.io.IOException;
import java.io.Serializable;

import net.daum.clix.springframework.data.rest.client.http.RestClient;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.repository.support.Repositories;

@RunWith(MockitoJUnitRunner.class)
public class SimpleRestRepositoryTest {

	@Mock
	private Repositories repositories;

	@Mock
	private RestClient restClient;

	@InjectMocks
	private SimpleRestRepository repository;

	@Test
	public void testFindOne() {
		Serializable id = 1;

		repository.findOne(id);
		
//		verify(restClient, times(1)).getForObject(Any, id);
	}

	@Test
	public void testDeleteAll() throws Exception {
		repository.deleteAll();

//		verify(restClient).deleteAll();
	}

	@Test
	public void testCount() throws Exception {
		repository.count();

//		verify(restClient).count(resourceInformation);
	}

	@Test
	public void testDelete() throws Exception {
		Serializable id = 1;

		repository.delete(id);

//		verify(restClient, times(1)).delete(resourceInformation, id);
	}

}

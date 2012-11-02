package net.daum.clix.springframework.data.rest.client.repository;

import static org.mockito.Mockito.mock;
import net.daum.clix.springframework.data.rest.client.http.RestClient;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

// TODO  needs to veriy constructor call. Should I use powermock or something?
public class RestRepositoryFactoryBeanTest {
	
	@Mock
	private RestClient restClient;

	@SuppressWarnings("rawtypes")
	@InjectMocks
	private RestRepositoryFactoryBean restRepositoryFactoryBean;
	
	RestRepositoryFactory restRepositoryFactory = mock(RestRepositoryFactory.class);

	@Test
	public void testCreateRepositoryFactory() throws Exception {
//		restRepositoryFactoryBean.createRepositoryFactory();
	}

}

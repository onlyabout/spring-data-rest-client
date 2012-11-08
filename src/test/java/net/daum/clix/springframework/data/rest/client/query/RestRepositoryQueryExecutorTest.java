package net.daum.clix.springframework.data.rest.client.query;

import java.lang.reflect.Method;

import net.daum.clix.springframework.data.rest.client.http.RestClient;

import org.junit.Before;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.repository.core.RepositoryMetadata;

// TODO Should I use powermock to mock the return type of @{link Method}
public class RestRepositoryQueryExecutorTest {
	
	@Mock
	private RestClient restClient;
	
	@Mock
	private Method method;
	
	@Mock
	private RepositoryMetadata repositoryMetadata;
	
	@SuppressWarnings("rawtypes")
	@InjectMocks
	private RestRepositoryQueryExecutor restRepositoryQueryExecutor;
	
	@Before
	public void setup() {
//		when(method.getReturnType()).thenReturn(listType);
	}

}

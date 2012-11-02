package net.daum.clix.springframework.data.rest.client.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.daum.clix.springframework.data.rest.client.http.RestClient;
import net.daum.clix.springframework.data.rest.client.metadata.RestEntityInformation;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

@RunWith(MockitoJUnitRunner.class)
public class SimpleRestRepositoryTest {

	@Mock
	private RestClient restClient;

	@SuppressWarnings("rawtypes")
	@Mock
	private RestEntityInformation restEntityInformation;

	@SuppressWarnings("rawtypes")
	@InjectMocks
	private SimpleRestRepository repository;

	@SuppressWarnings("unchecked")
	@Test
	public void testFindOne() {
		Serializable id = 1;
		repository.findOne(id);
		verify(restClient, times(1)).getForObject(restEntityInformation, id);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSave() throws Exception {
		Object entity = new Object();
		repository.save(entity);
		verify(restClient).saveForObject(entity);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testSaveIterable() throws Exception {
		Iterable entity = mock(ArrayList.class);
		repository.save(entity);
		verify(restClient).saveForObjects(entity);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testExists() throws Exception {
		Serializable id = 1;
		when(restClient.getForObject(restEntityInformation, id)).thenReturn(null);
		assertFalse(repository.exists(id));

		when(restClient.getForObject(restEntityInformation, id)).thenReturn(new Object());
		assertTrue(repository.exists(id));

		verify(restClient, times(2)).getForObject(restEntityInformation, id);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testCount() throws Exception {
		long expected = 111L;

		when(restClient.count(restEntityInformation)).thenReturn(expected);
		assertEquals(expected, repository.count());

		verify(restClient).count(restEntityInformation);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testDelete() throws Exception {
		Serializable id = 1;
		repository.delete(id);
		verify(restClient).delete(restEntityInformation, id);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testDeleteEntity() throws Exception {
		Object entity = new Object();
		Serializable id = 1L;
		when(restEntityInformation.getId(entity)).thenReturn(id);

		repository.delete(entity);

		verify(restEntityInformation).getId(entity);
		verify(restClient, times(1)).delete(restEntityInformation, id);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testDeleteIterable() throws Exception {
		List entities = new ArrayList();
		entities.add(new Object());
		entities.add(new Object());

		when(restEntityInformation.getId(Mockito.anyObject())).thenReturn(1L);

		repository.delete(entities);

		verify(restEntityInformation, times(2)).getId(Mockito.anyObject());
		verify(restClient, times(2)).delete(restEntityInformation, 1L);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testFindAll() throws Exception {
		repository.findAll();
		
		verify(restClient).getForIterable(restEntityInformation);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testFindAllIterable() throws Exception {
		List<Serializable> ids = new ArrayList<Serializable>();
		ids.add(1L);
		ids.add(2L);
		
		repository.findAll(ids);
		
		verify(restClient, times(1)).getForObject(restEntityInformation, 1L);
		verify(restClient, times(1)).getForObject(restEntityInformation, 2L);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testFindAllSort() throws Exception {
		Sort sort = new Sort(Direction.DESC, "sortBy");
		
		repository.findAll(sort);
		
		verify(restClient, times(1)).getForIterable(restEntityInformation, sort);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testFindAllPageable() throws Exception {
		Sort sort = new Sort(Direction.DESC, "sortBy");
		Pageable pageable = new PageRequest(1, 10, sort);
		
		repository.findAll(pageable);
		
		verify(restClient, times(1)).getForPageable(restEntityInformation, pageable);
		verify(restClient, times(0)).getForIterable(restEntityInformation, sort);
	}
}
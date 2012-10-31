package net.daum.clix.springframework.data.rest.client.http;

import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

//@RunWith(MockitoJUnitRunner.class)
public class RestClientBaseTest {

//	@SuppressWarnings({ "rawtypes", "unchecked" })
//	@InjectMocks
//	private RestClientBase restClientBase = new RestClientBase("url") {
//		@Override
//		protected void executeDelete(String url) {
//			// should be called by delete all.
//			if (url.equals(expectedResourcesUrl))
//				return;
//
//			// should be called by delete one entity.
//			if (url.equals(expectedResourceUrl))
//				return;
//			
//			throw new RuntimeException("FAILED");
//		}
//
//		@Override
//		protected ResourceSupport executeGet(String url, Type resourceType, Type objectType) {
//			if (url.equals(expectedResourceUrl) && resourceType.equals(Resource.class))
//				return new Resource(entity);
//
//			if (url.equals(expectedResourcesUrl) && resourceType.equals(PagedResources.class)) {
//				throw new RuntimeException("Method Properly Called");
//			}
//			return null;
//		}
//
//		@Override
//		protected ResourceSupport executePost(String url, Object entity) {
//			// TODO Auto-generated method stub
//			throw new IllegalAccessError("RestClientBase#executePost has not implemented yet!");
//		}
//
//		public <S> Iterable<S> saveForObjects(Iterable<S> entities) {
//			// TODO Auto-generated method stub
//			throw new IllegalAccessError("RestClient#saveForObjects has not implemented yet!");
//		}
//
//		@Override
//		protected <S> ResourceSupport executePost(String url, S entity) {
//			// TODO Auto-generated method stub
//			throw new IllegalAccessError("RestClientBase#executePost has not implemented yet!");
//		}
//	};
//
//	@Mock
//	RestUrlBuilder urlBuilder;
//
//	@Mock
//	private RestRequest restRequest;
//
//	private Person entity;
//
//	private Serializable id;
//	private String expectedResourceUrl = "http://testurl/a/1";
//	private String expectedResourcesUrl = "http://testurl/a";

//	@Before
//	public void setUp() {
//		entity = new Person();
//		entity.setName("name");
//		when(urlBuilder.build(restRequest, id)).thenReturn(expectedResourceUrl);
//		when(urlBuilder.buildResourcesUrl(restRequest)).thenReturn(expectedResourcesUrl);
//	}
//
//	@SuppressWarnings("unchecked")
//	@Test
//	public void testGetForObject() throws Exception {
//		Person person = (Person) restClientBase.getForObject(restRequest, id);
//		Assert.assertEquals(entity, person);
//
//		verify(urlBuilder).build(restRequest, id);
//		verify(restRequest).getEntityType();
//	}
//
//	@Test
//	public void testDeleteAll() throws Exception {
//		restClientBase.deleteAll(restRequest);
//		verify(urlBuilder).buildResourcesUrl(restRequest);
//	}
//
//	@Test
//	public void testCount() throws Exception {
//		try {
//			restClientBase.count(restRequest);
//			fail("RestClientBase#executeGet should be called");
//		} catch (RuntimeException e) {
//			assertEquals("Method Properly Called", e.getMessage());
//		}
//
//		verify(urlBuilder).buildResourcesUrl(restRequest);
//	}
//
//	@SuppressWarnings("unchecked")
//	@Test
//	public void testDelete() throws Exception {
//		restClientBase.delete(restRequest, id);
//		verify(urlBuilder).build(restRequest, id);
//	}

}

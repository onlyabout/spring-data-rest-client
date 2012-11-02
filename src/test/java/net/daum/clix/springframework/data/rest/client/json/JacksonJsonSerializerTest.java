package net.daum.clix.springframework.data.rest.client.json;

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.lang.reflect.Type;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.hateoas.Resource;

@RunWith(MockitoJUnitRunner.class)
public class JacksonJsonSerializerTest {

	@Mock
	private ObjectMapper mapper;
	
	@Mock
	private JacksonResourceTypeFactory typeFactory;

	@InjectMocks
	private JacksonJsonSerializer serializer;

	@Test
	public void serializeSimplyDelegatesToMapper() throws Exception {
		Object object = new Object();
		serializer.serialize(object);
		
		verify(mapper, times(1)).writeValueAsBytes(object);
	}
	
	@Test
	public void serializeReturnsNullIfAnyExceptionCatched() {
		assertNull(serializer.serialize(null));
		assertNull(serializer.serialize("{A string which is not json format"));
	}

	@Test
	public void deserializeDelegatesToMapperWithCreatedType() throws Exception { 
		byte[] jsonData = "{\"key\":\"value\"}".getBytes();
		Type resourceType = Resource.class;
		Type objectType = Object.class;
		
		JavaType createdType = mock(JavaType.class);
		when(typeFactory.getResourceType(resourceType, objectType)).thenReturn(createdType);
		
		serializer.deserialize(jsonData, resourceType, objectType);
		
		verify(typeFactory).getResourceType(resourceType, objectType);
		verify(mapper).readValue(jsonData, createdType);
	}
	
	@Test
	public void deserializeMapResource() throws JsonParseException, JsonMappingException, IOException {
		byte[] jsonData = "{\"key\":\"value\"}".getBytes();
		Type resourceType = Resource.class;
		Type keyType = String.class;
		Type valueType = String.class;
		
		JavaType createdType = mock(JavaType.class);
		when(typeFactory.getMapResourceType(resourceType, keyType, valueType)).thenReturn(createdType);
		
		serializer.deserializeMapResource(jsonData, resourceType, keyType, valueType);
		
		verify(typeFactory).getMapResourceType(resourceType, keyType, valueType);
		verify(mapper).readValue(jsonData, createdType);
	}

}

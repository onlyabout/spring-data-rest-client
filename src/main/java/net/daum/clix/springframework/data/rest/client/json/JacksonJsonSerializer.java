package net.daum.clix.springframework.data.rest.client.json;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.type.JavaType;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;

public class JacksonJsonSerializer implements JsonSerializer {

	private ObjectMapper mapper;

	public JacksonJsonSerializer() {
		this.mapper = new ObjectMapper();
		this.mapper.setSerializationInclusion(Inclusion.NON_EMPTY);
		this.mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	public byte[] serialize(Object object) {
		try {
			return mapper.writeValueAsBytes(object);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public Object deserialize(byte[] jsonData, Type resourceType, Type objectType) {
		if (jsonData == null)
			return null;

		return readValue(jsonData, getResourceType(resourceType, objectType));
	}

	public Object deserializeMapResource(byte[] jsonData, Type resourceType, Type keyType, Type valueType) {
		if (jsonData == null)
			return null;

		JavaType mapResourceType = getMapResourceType(resourceType, keyType, valueType);

		return readValue(jsonData, mapResourceType);

	}

	private JavaType getResourceType(Type resourceType, Type objectType) {
		TypeFactory typeFactory = mapper.getTypeFactory();

		if (Resources.class.isAssignableFrom((Class<?>) resourceType)) {
			JavaType wrappedType = typeFactory.constructParametricType(Resource.class, (Class<?>) objectType);
			return typeFactory.constructParametricType((Class<?>) resourceType, wrappedType);
		}

		return typeFactory.constructParametricType((Class<?>) resourceType, (Class<?>) objectType);
	}

	private JavaType getMapResourceType(Type resourceType, Type keyType, Type valueType) {
		TypeFactory typeFactory = mapper.getTypeFactory();

		JavaType simpleKeyType = typeFactory.constructType(keyType);
		JavaType wrappedValueType = typeFactory.constructParametricType(Resource.class, (Class<?>) valueType);
		JavaType mapType = typeFactory.constructMapType(Map.class, simpleKeyType, wrappedValueType);

		return typeFactory.constructParametricType((Class<?>) resourceType, mapType);
	}

	private Object readValue(byte[] jsonData, JavaType type) {
		try {
			return mapper.readValue(jsonData, type);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

}

package net.daum.clix.springframework.data.rest.client.json;

import java.lang.reflect.Type;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jackson.type.JavaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JacksonJsonSerializer implements JsonSerializer {

	private final Logger LOG = LoggerFactory.getLogger(JacksonJsonSerializer.class);

	private ObjectMapper mapper;

	private JacksonResourceTypeFactory typeFactory;

	public JacksonJsonSerializer() {
		this.mapper = new ObjectMapper();
		this.mapper.setSerializationInclusion(Inclusion.NON_EMPTY);
		this.mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//		this.mapper.setAnnotationIntrospector(new JpaAwareJacksonAnnotationIntrospector());

		this.typeFactory = new JacksonResourceTypeFactory(mapper.getTypeFactory());
	}

	public byte[] serialize(Object object) {
		try {
			return mapper.writeValueAsBytes(object);
		} catch (Exception e) {
			LOG.error("Serialization failed for " + object.getClass().getName(), e);
		}

		return null;
	}

	public Object deserialize(byte[] jsonData, Type resourceType, Type objectType) {
		if (jsonData == null)
			return null;

		return readValue(jsonData, typeFactory.getResourceType(resourceType, objectType));
	}

	public Object deserializeSetResource(byte[] jsonData, Type resourceType, Type valueType) {
		if (jsonData == null)
			return null;
		
		JavaType setResourceType = typeFactory.getSetResourceType(resourceType, valueType);
		
		return readValue(jsonData, setResourceType);
	}

	public Object deserializeMapResource(byte[] jsonData, Type resourceType, Type keyType, Type valueType) {
		if (jsonData == null)
			return null;

		JavaType mapResourceType = typeFactory.getMapResourceType(resourceType, keyType, valueType);

		return readValue(jsonData, mapResourceType);
	}

	private Object readValue(byte[] jsonData, JavaType type) {
		try {
			return mapper.readValue(jsonData, type);
		} catch (Exception e) {
			LOG.error("Deserialization failed for exception : ", e);
			LOG.error("Deserialization failed for json body.", new String(jsonData));
		}

		return null;
	}

}

package net.daum.clix.springframework.data.rest.client.json;

import java.lang.reflect.Type;

public interface JsonSerializer {

	byte[] serialize(Object object);

	Object deserialize(byte[] jsonData, Type resourceType, Type objectType);

	Object deserializeSetResource(byte[] body, Type resourceType, Type valueType);

	Object deserializeMapResource(byte[] body, Type resourceType, Type keyType, Type valueType);

}

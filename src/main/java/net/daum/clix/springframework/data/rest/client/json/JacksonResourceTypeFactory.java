package net.daum.clix.springframework.data.rest.client.json;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.type.JavaType;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;

public class JacksonResourceTypeFactory {

	private TypeFactory typeFactory;

	public JacksonResourceTypeFactory(TypeFactory typeFactory) {
		this.typeFactory = typeFactory;
	}

	public JavaType getResourceType(Type resourceType, Type objectType) {
		if (Resources.class.isAssignableFrom((Class<?>) resourceType)) {
			JavaType wrappedType = typeFactory.constructParametricType(Resource.class, (Class<?>) objectType);
			return typeFactory.constructParametricType((Class<?>) resourceType, wrappedType);
		}

		return typeFactory.constructParametricType((Class<?>) resourceType, (Class<?>) objectType);
	}

	public JavaType getMapResourceType(Type resourceType, Type keyType, Type valueType) {
		JavaType simpleKeyType = typeFactory.constructType(keyType);
		JavaType wrappedValueType = typeFactory.constructParametricType(Resource.class, (Class<?>) valueType);
		JavaType mapType = typeFactory.constructMapType(Map.class, simpleKeyType, wrappedValueType);

		return typeFactory.constructParametricType((Class<?>) resourceType, mapType);
	}

	public JavaType getSetResourceType(Type resourceType, Type valueType) {
		JavaType wrappedValueType = typeFactory.constructParametricType(Resource.class, (Class<?>) valueType);
		JavaType setType = typeFactory.constructParametricType(Set.class, wrappedValueType);

		return typeFactory.constructParametricType((Class<?>) resourceType, setType);
	}

}

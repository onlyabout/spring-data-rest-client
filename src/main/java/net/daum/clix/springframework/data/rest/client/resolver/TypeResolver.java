package net.daum.clix.springframework.data.rest.client.resolver;

import java.lang.reflect.Type;

/**
 * Resolve the type of object from json resource body.
 * 
 * @author 84june
 *
 */
@Deprecated
public interface TypeResolver {

	boolean canResolve(Type objectType);

	Type resolve(byte[] body, Type typeToResolve);

}

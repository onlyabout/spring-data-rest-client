package net.daum.clix.springframework.data.rest.client.json;

import javax.persistence.Transient;

import org.codehaus.jackson.map.introspect.Annotated;
import org.codehaus.jackson.map.introspect.JacksonAnnotationIntrospector;

public class JpaAwareJacksonAnnotationIntrospector extends JacksonAnnotationIntrospector {

	@Override
	protected boolean _isIgnorable(Annotated a) {
		boolean isTransientPresent = a.getAnnotation(Transient.class) != null;
		boolean isSpringTransientPresent = a.getAnnotation(org.springframework.data.annotation.Transient.class) != null;
		return super._isIgnorable(a) || isTransientPresent || isSpringTransientPresent;
	}
}

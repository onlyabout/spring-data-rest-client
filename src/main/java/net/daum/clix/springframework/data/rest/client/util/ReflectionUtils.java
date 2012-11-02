package net.daum.clix.springframework.data.rest.client.util;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReflectionUtils {

	private static Logger LOG = LoggerFactory.getLogger(ReflectionUtils.class);

	public static Object valueOf(String value, Class<?> toType) {
		if (String.class.isAssignableFrom(toType))
			return value;

		try {
			Method m = toType.getMethod("valueOf", String.class);
			int modifiers = m.getModifiers();
			if (Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers)) {
				return m.invoke(null, value);
			}
		} catch (Exception e) {
			LOG.error("Convert string value:" + value + " to type:" + toType.getSimpleName() + " failed.", e);
		}

		return null;
	}
}

package net.daum.clix.springframework.data.rest.client.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils.FieldFilter;

public class ReflectionUtils {

	private static Logger LOG = LoggerFactory.getLogger(ReflectionUtils.class);

	public static FieldFilter JPA_ID_FILED_FILTER = new FieldFilter() {

		@Override
		public boolean matches(Field field) {
			if (field.isAnnotationPresent(org.springframework.data.annotation.Id.class)
					|| field.isAnnotationPresent(javax.persistence.Id.class)) {
				return true;
			}

			PropertyDescriptor propertyDescriptor = BeanUtils.getPropertyDescriptor(field.getDeclaringClass(),
					field.getName());
			if (propertyDescriptor != null && propertyDescriptor.getReadMethod() != null) {
				if (null != propertyDescriptor.getReadMethod().getAnnotation(
						org.springframework.data.annotation.Id.class))
					return true;

				if (null != propertyDescriptor.getReadMethod().getAnnotation(
						org.springframework.data.annotation.Id.class))
					return true;
			}

			return false;
		}
	};

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

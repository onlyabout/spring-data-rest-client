package net.daum.clix.springframework.data.rest.client.metadata;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import net.sf.cglib.proxy.Enhancer;

import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/**
 * @author 84june
 */
public class RestEntityInformation<T, ID extends Serializable> extends RestEntityInformationSupport<T, ID> {

	public RestEntityInformation(Class<T> domainClass) {
		super(domainClass);
	}

	@SuppressWarnings("unchecked")
	public Class<ID> getIdType() {
		Field idField = getIdField();

		Assert.notNull(idField);

		return (Class<ID>) idField.getType();
	}

	@SuppressWarnings("unchecked")
	public ID getId(T entity) {

		Field idField = getIdField();
		Assert.notNull(idField);

		if (Enhancer.isEnhanced(entity.getClass())) {

			try {
				return (ID) getJavaType().getMethod(getGetterMethodName(idField.getName())).invoke(entity);
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}

		try {
			idField.setAccessible(true);
			return (ID) idField.get(entity);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		return null;
	}

	private String getGetterMethodName(String fieldName) {
		StringBuilder sb = new StringBuilder("get");
		for (int i = 0; i < fieldName.length(); i++) {
			if (i == 0)
				sb.append(fieldName.substring(0, 1).toUpperCase());
			else
				sb.append(fieldName.charAt(i));
		}

		return sb.toString();
	}

	public void setId(T entity, ID id) {
		Object value = id;

		Field idField = getIdField();

		if (String.class.isAssignableFrom(id.getClass()))
			value = net.daum.clix.springframework.data.rest.client.util.ReflectionUtils.valueOf(id.toString(),
					idField.getType());

		if (idField != null) {
			idField.setAccessible(true);
			ReflectionUtils.setField(idField, entity, value);
		}
	}

	private Field getIdField() {
		for (Field f : getJavaType().getDeclaredFields()) {
			if (f.isAnnotationPresent(org.springframework.data.annotation.Id.class)
					|| f.isAnnotationPresent(javax.persistence.Id.class)) {
				return f;
			}
		}

		return null;
	}

	public Field findFieldByRel(String rel) {
		String[] tkns = StringUtils.tokenizeToStringArray(rel, ".");
		String fieldName = tkns[tkns.length - 1];
		
		return ReflectionUtils.findField(getJavaType(), fieldName);
	}

}

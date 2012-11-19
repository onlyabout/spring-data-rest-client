package net.daum.clix.springframework.data.rest.client.metadata;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;
import org.springframework.util.StringUtils;

/**
 * @author 84june
 */
public class RestEntityInformation<T, ID extends Serializable> extends RestEntityInformationSupport<T, ID> {

	private static final Map<Class<?>, Field> idFieldForDomainClass = new HashMap<Class<?>, Field>();

	public RestEntityInformation(Class<T> domainClass) {
		super(domainClass);
	}

	@SuppressWarnings("unchecked")
	public Class<ID> getIdType() {
		Field idField = getIdField();

		Assert.notNull(idField, "Not able to find @Id field for the given entity type : " + getJavaType().getName());

		return (Class<ID>) idField.getType();
	}

	@SuppressWarnings("unchecked")
	public ID getId(T entity) {

		Field idField = getIdField();

		Assert.notNull(idField, "Not able to find @Id field for the given entity type : " + getJavaType().getName());

		Method getterMethod = BeanUtils.findMethodWithMinimalParameters(getJavaType(),
				getGetterMethodName(idField.getName()));

		// if (Enhancer.isEnhanced(entity.getClass())) {
		if (null != getterMethod)
			return (ID) ReflectionUtils.invokeMethod(getterMethod, entity);

		try {
			idField.setAccessible(true);
			return (ID) idField.get(entity);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	public void setId(final T entity, final ID id) {
		ReflectionUtils.doWithFields(getJavaType(), new FieldCallback() {

			@Override
			public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
				Object value = id;

				if (String.class.isAssignableFrom(id.getClass()))
					value = net.daum.clix.springframework.data.rest.client.util.ReflectionUtils.valueOf(id.toString(),
							field.getType());

				field.setAccessible(true);
				ReflectionUtils.setField(field, entity, value);
			}
		}, net.daum.clix.springframework.data.rest.client.util.ReflectionUtils.JPA_ID_FILED_FILTER);
	}

	public Field findFieldByRel(String rel) {
		String[] tkns = StringUtils.tokenizeToStringArray(rel, ".");
		String fieldName = tkns[tkns.length - 1];

		return ReflectionUtils.findField(getJavaType(), fieldName);
	}

	private Field getIdField() {
		if (idFieldForDomainClass.containsKey(getJavaType()))
			return idFieldForDomainClass.get(getJavaType());

		Class<?> javaType = getJavaType();

		while (javaType != Object.class) {
			for (Field field : javaType.getDeclaredFields()) {
				boolean matched = net.daum.clix.springframework.data.rest.client.util.ReflectionUtils.JPA_ID_FILED_FILTER
						.matches(field);
				if (matched) {
					idFieldForDomainClass.put(getJavaType(), field);
					return field;
				}
			}

			javaType = javaType.getSuperclass();
		}

		throw new IllegalArgumentException("Not able to find @Id field for the given entity type : "
				+ getJavaType().getName());
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

}

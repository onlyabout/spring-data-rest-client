package net.daum.clix.springframework.data.rest.client.metadata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.Serializable;
import java.lang.reflect.Field;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.rest.example.Person;
import org.springframework.util.ReflectionUtils;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class RestEntityInformationTest<T, ID extends Serializable> {

	private RestEntityInformation<T, ID> restEntityInformation;

	private Person person;

	@Before
	public void setUp() throws SecurityException, NoSuchFieldException {
		person = new Person();
		restEntityInformation = new RestEntityInformation(Person.class);

		// set id field
		Field field = ReflectionUtils.findField(Person.class, "id");
		field.setAccessible(true);
		ReflectionUtils.setField(field, person, 1L);
	}

	@Test
	public void testGetId() throws Exception {
		assertEquals(Long.class, restEntityInformation.getIdType());

	}

	@Test
	public void testGetIdType() throws Exception {
		assertEquals(1L, restEntityInformation.getId((T) person));
	}

	@Test
	public void testSetId() throws Exception {
		T person = (T) new Person("name", null, null);
		ID id = (ID) new Long(1);
		
		assertNull(((Person) person).getId());
		restEntityInformation.setId(person, id);
		assertEquals(id, ((Person) person).getId());
	}

}

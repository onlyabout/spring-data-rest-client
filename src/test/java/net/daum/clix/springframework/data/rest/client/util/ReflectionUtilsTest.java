package net.daum.clix.springframework.data.rest.client.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ReflectionUtilsTest {

	@Test
	public void testValueOf() throws Exception {
		
		assertEquals(1L, ReflectionUtils.valueOf("1", Long.class));
		assertEquals(1, ReflectionUtils.valueOf("1", Integer.class));
		assertEquals("1", ReflectionUtils.valueOf("1", String.class));

	}

}

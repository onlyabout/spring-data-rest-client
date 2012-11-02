package net.daum.clix.springframework.data.rest.client.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class RestUrlUtilTest {

	@Test
	public void testGetIdFrom() throws Exception {
		assertEquals("1", RestUrlUtil.getIdFrom("http://1.2.3.4/repositoryId/1"));
	}

	@Test
	public void testNormalize() throws Exception {
		String expected = "http://www.daum.net";
		assertEquals(expected, RestUrlUtil.normalize("http://www.daum.net////"));
		assertEquals(expected, RestUrlUtil.normalize("www.daum.net////"));
		assertEquals(expected, RestUrlUtil.normalize("www.daum.net"));
	}

}

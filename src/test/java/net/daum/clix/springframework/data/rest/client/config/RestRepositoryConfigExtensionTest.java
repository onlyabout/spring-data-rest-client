package net.daum.clix.springframework.data.rest.client.config;

import static org.junit.Assert.assertEquals;

import net.daum.clix.springframework.data.rest.client.repository.RestRepositoryFactoryBean;

import org.junit.Before;
import org.junit.Test;

public class RestRepositoryConfigExtensionTest {

	private RestRepositoryConfigExtension extension;

	@Before
	public void setup() {
		extension = new RestRepositoryConfigExtension();
	}

	@Test
	public void testGetRepositoryFactoryClassName() throws Exception {
		assertEquals(RestRepositoryFactoryBean.class.getName(), extension.getRepositoryFactoryClassName());
	}

}

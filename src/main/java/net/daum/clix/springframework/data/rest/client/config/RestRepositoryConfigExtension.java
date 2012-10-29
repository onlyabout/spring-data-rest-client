package net.daum.clix.springframework.data.rest.client.config;

import net.daum.clix.springframework.data.rest.client.repository.RestRepositoryFactoryBean;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.data.repository.config.AnnotationRepositoryConfigurationSource;
import org.springframework.data.repository.config.RepositoryConfigurationExtensionSupport;
import org.springframework.data.repository.config.XmlRepositoryConfigurationSource;

public class RestRepositoryConfigExtension extends RepositoryConfigurationExtensionSupport {

	public String getRepositoryFactoryClassName() {
		return RestRepositoryFactoryBean.class.getName();
	}

	/**
	 * Used by RepositoryConfigurationExtension#getDefaultNamedQueryLocation()
	 * to retrieve named queries which isn't supported by spring-data-rest.
	 */
	@Override
	protected String getModulePrefix() {
		return "SHOULD-NOT-BE-USED-FOR-SDR";
	}

	@Override
	public void postProcess(BeanDefinitionBuilder builder, AnnotationRepositoryConfigurationSource config) {
		super.postProcess(builder, config);
	}

	@Override
	public void postProcess(BeanDefinitionBuilder builder, XmlRepositoryConfigurationSource config) {
		super.postProcess(builder, config);
	}
}

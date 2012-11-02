package net.daum.clix.springframework.data.rest.client.config;

import net.daum.clix.springframework.data.rest.client.repository.RestRepositoryFactoryBean;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.data.repository.config.AnnotationRepositoryConfigurationSource;
import org.springframework.data.repository.config.RepositoryConfigurationExtensionSupport;
import org.springframework.data.repository.config.XmlRepositoryConfigurationSource;

/**
 * Spring-data-rest-client specific configuration extension.
 * 
 * @author 84june
 * 
 */
public class RestRepositoryConfigExtension extends RepositoryConfigurationExtensionSupport {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.data.repository.config.RepositoryConfigurationExtensionSupport#getRepositoryFactoryClassName()
	 */
	public String getRepositoryFactoryClassName() {
		return RestRepositoryFactoryBean.class.getName();
	}

	/*
	 * Used by RepositoryConfigurationExtension#getDefaultNamedQueryLocation() 
	 * to retrieve named queries which isn't supported by spring-data-rest.
	 */
	@Override
	protected String getModulePrefix() {
		return "SHOULD-NOT-BE-USED-FOR-SDR";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.data.repository.config.RepositoryConfigurationExtensionSupport#postProcess()
	 */
	@Override
	public void postProcess(BeanDefinitionBuilder builder, AnnotationRepositoryConfigurationSource config) {
		super.postProcess(builder, config);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.data.repository.config.RepositoryConfigurationExtensionSupport#postProcess()
	 */
	@Override
	public void postProcess(BeanDefinitionBuilder builder, XmlRepositoryConfigurationSource config) {
		super.postProcess(builder, config);
	}
}

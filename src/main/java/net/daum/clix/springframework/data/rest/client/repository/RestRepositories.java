package net.daum.clix.springframework.data.rest.client.repository;

/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.support.RepositoryFactoryInformation;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.rest.repository.annotation.RestResource;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Wrapper class to access repository instances obtained from a
 * {@link ListableBeanFactory}.
 * 
 * @author Oliver Gierke
 */
public class RestRepositories implements Iterable<Class<?>> {

	private final Map<Class<?>, RepositoryFactoryInformation<Object, Serializable>> domainClassToBeanName = new HashMap<Class<?>, RepositoryFactoryInformation<Object, Serializable>>();
	private final Map<String, RepositoryFactoryInformation<Object, Serializable>> resourcePathToBeanName = new HashMap<String, RepositoryFactoryInformation<Object, Serializable>>();
	private final Map<RepositoryFactoryInformation<Object, Serializable>, CrudRepository<Object, Serializable>> repositories = new HashMap<RepositoryFactoryInformation<Object, Serializable>, CrudRepository<Object, Serializable>>();

	/**
	 * Creates a new {@link RestRepositories} instance by looking up the
	 * repository instances and meta information from the given
	 * {@link ListableBeanFactory}.
	 * 
	 * @param factory
	 *            must not be {@literal null}.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public RestRepositories(ListableBeanFactory factory) {

		Assert.notNull(factory);

		Collection<RestRepositoryFactoryBean> providers = BeanFactoryUtils.beansOfTypeIncludingAncestors(factory,
				RestRepositoryFactoryBean.class, true, false).values();

		for (RepositoryFactoryInformation<Object, Serializable> info : providers) {

			RepositoryInformation information = info.getRepositoryInformation();
			Class repositoryInterface = information.getRepositoryInterface();

			if (CrudRepository.class.isAssignableFrom(repositoryInterface)) {
				Class<CrudRepository<Object, Serializable>> objectType = repositoryInterface;
				CrudRepository<Object, Serializable> repository = BeanFactoryUtils.beanOfTypeIncludingAncestors(
						factory, objectType);

				this.domainClassToBeanName.put(information.getDomainType(), info);
				this.resourcePathToBeanName.put(getResourcePath(repositoryInterface), info);
				this.repositories.put(info, repository);

			}
		}
	}

	private String getResourcePath(Class<?> repositoryInterface) {
		String path = StringUtils.uncapitalize(repositoryInterface.getSimpleName().replaceAll("Repository", ""));
		RestResource restResource = repositoryInterface.getAnnotation(RestResource.class);
		if (restResource != null && StringUtils.hasText(restResource.path())) {
			path = restResource.path();
		}
		return path;
	}

	/**
	 * Returns whether we have a repository instance registered to manage
	 * instances of the given domain class.
	 * 
	 * @param domainClass
	 *            must not be {@literal null}.
	 * @return
	 */
	public boolean hasRepositoryFor(Class<?> domainClass) {
		return domainClassToBeanName.containsKey(domainClass);
	}

	/**
	 * Returns the repository managing the given domain class.
	 * 
	 * @param domainClass
	 *            must not be {@literal null}.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T, S extends Serializable> CrudRepository<T, S> getRepositoryFor(Class<?> domainClass) {
		return (CrudRepository<T, S>) repositories.get(domainClassToBeanName.get(domainClass));
	}

	/**
	 * Returns the {@link EntityInformation} for the given domain class.
	 * 
	 * @param domainClass
	 *            must not be {@literal null}.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T, S extends Serializable> EntityInformation<T, S> getEntityInformationFor(Class<?> domainClass) {

		RepositoryFactoryInformation<Object, Serializable> information = getRepoInfoFor(domainClass);
		return information == null ? null : (EntityInformation<T, S>) information.getEntityInformation();
	}

	/**
	 * Returns the {@link EntityInformation} for the given domain class.
	 * 
	 * @param domainClass
	 *            must not be {@literal null}.
	 * @return the {@link EntityInformation} for the given domain class or
	 *         {@literal null} if no repository registered for this domain
	 *         class.
	 */
	public RepositoryInformation getRepositoryInformationFor(Class<?> domainClass) {

		RepositoryFactoryInformation<Object, Serializable> information = getRepoInfoFor(domainClass);
		return information == null ? null : information.getRepositoryInformation();
	}

	/**
	 * Returns the {@link Repository} class interface for the given domain
	 * class. Simply delegates to {@RepositoryInformation
	 * 
	 * 
	 * }
	 * 
	 * @param domainCall
	 *            must not be {@literal null}.
	 * @return
	 */
	public Class<?> getRepositoryInterfaceFor(Class<?> domainClass) {
		RepositoryInformation repositoryInformation = getRepositoryInformationFor(domainClass);
		return repositoryInformation == null ? null : repositoryInformation.getRepositoryInterface();
	}

	/**
	 * Returns the {@link EntityInformation} for the given resource path.
	 * 
	 * @param resourcePath
	 *            must not be {@literal null}.
	 * @return the {@link EntityInformation} for the given resource path or
	 *         {@literal null} if no repository registered for this domain
	 *         class.
	 */
	public RepositoryInformation findRepositoryInformationFor(String resourcePath) {
		RepositoryFactoryInformation<Object, Serializable> repositoryFactoryInformation = this.resourcePathToBeanName
				.get(resourcePath);
		return repositoryFactoryInformation == null ? null : repositoryFactoryInformation.getRepositoryInformation();
	}

	/**
	 * Returns the {@link String} for the given resource path class.
	 * 
	 * @param resourcePath
	 *            must not be {@literal null}.
	 * @return the {@link String} for the given resource path or {@literal null}
	 *         if no repository registered for this resource path
	 */
	public String findDomainClassNameFor(String resourcePath) {
		RepositoryInformation repoInfo = findRepositoryInformationFor(resourcePath);
		return repoInfo == null ? null : repoInfo.getDomainType().getName();
	}

	/**
	 * Returns the {@link RestResource} for the given domain class's repository.
	 * 
	 * @param domainCall
	 *            must not be {@literal null}.
	 * @return the {@link RestResource} for the given domain class or
	 *         {@literal null} if no @RestResource presents.
	 */
	public RestResource getRestResourceAnnotationFor(Class<?> domainClass) {
		Class<?> repositoryInterface = getRepositoryInterfaceFor(domainClass);
		return repositoryInterface == null ? null : repositoryInterface.getAnnotation(RestResource.class);
	}

	public String getRepositoryNameFor(Class<?> domainClass) {
		Class<?> repositoryInterface = getRepositoryInterfaceFor(domainClass);
		return repositoryInterface == null ? null : StringUtils.uncapitalize(repositoryInterface.getSimpleName()
				.replaceAll("Repository", ""));
	}

	/**
	 * /** Returns the {@link QueryMethod}s contained in the repository managing
	 * the given domain class.
	 * 
	 * @param domainClass
	 *            must not be {@literal null}.
	 * @return
	 */
	public List<QueryMethod> getQueryMethodsFor(Class<?> domainClass) {

		RepositoryFactoryInformation<Object, Serializable> information = getRepoInfoFor(domainClass);
		return information == null ? Collections.<QueryMethod> emptyList() : information.getQueryMethods();
	}

	private RepositoryFactoryInformation<Object, Serializable> getRepoInfoFor(Class<?> domainClass) {

		Assert.notNull(domainClass);

		for (RepositoryFactoryInformation<Object, Serializable> information : repositories.keySet()) {
			if (domainClass.equals(information.getEntityInformation().getJavaType())) {
				return information;
			}
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<Class<?>> iterator() {
		return domainClassToBeanName.keySet().iterator();
	}

}

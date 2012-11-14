package net.daum.clix.springframework.data.rest.client.resolver;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

import net.daum.clix.springframework.data.rest.client.repository.RestRepositories;

import org.springframework.util.Assert;

/**
 * Resolve concrete type from abstract class by looking up json body.
 * 
 * @author 84june
 *
 */
@Deprecated
public class ConcreateTypeResolver implements TypeResolver {

	@SuppressWarnings("unused")
	private RestRepositories repositories;

	public ConcreateTypeResolver(RestRepositories repositories) {
		Assert.notNull(repositories);
		
		this.repositories = repositories;
	}

	@Override
	public boolean canResolve(Type objectType) {
		return Modifier.isAbstract(objectType.getClass().getModifiers());
	}

	@SuppressWarnings("unused")
	@Override
	public Type resolve(byte[] body, Type typeToResolve) {
		String resourcePath = getPathFromSelfHref(body);
		
//		RepositoryFactoryInformation<Object, Serializable> repoInfo = repositories.findRepositoryInformationFor(resourcePath);
//		return repoInfo.getRepositoryInformation().getDomainType();
		throw new IllegalAccessError("ConcreateTypeResolver#resolve has not implemented yet!");
	}

	private String getPathFromSelfHref(byte[] body) {
		// TODO Auto-generated method stub
		throw new IllegalAccessError("ConcreateTypeResolver#getPathFromSelfHref has not implemented yet!");
	}

}

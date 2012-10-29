package net.daum.clix.springframework.data.rest.client.metadata;

import java.io.Serializable;

import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.core.support.AbstractEntityInformation;

/**
 * @author 84june
 */
public abstract class RestEntityInformationSupport<T, ID extends Serializable> extends AbstractEntityInformation<T, ID> {

	/**
	 * Creates a new {@link org.springframework.data.repository.core.support.AbstractEntityInformation} from the given domain class.
	 *
	 * @param domainClass
	 */
	public RestEntityInformationSupport(Class<T> domainClass) {
		super(domainClass);
	}

	public static <T, ID extends Serializable> EntityInformation<T, ID> getMetadata(Class<T> domainClass) {
		return new RestEntityInformation<T, ID>(domainClass);
	}
}

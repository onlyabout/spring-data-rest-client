package net.daum.clix.springframework.data.rest.client.query;

import java.lang.reflect.Method;

import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.QueryMethod;

public class RestQueryMethod extends QueryMethod {

	public RestQueryMethod(Method method, RepositoryMetadata metadata) {
		super(method, metadata);
	}

}

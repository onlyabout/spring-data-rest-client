package net.daum.clix.springframework.data.rest.client.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import net.daum.clix.springframework.data.rest.client.json.domain.Animal;
import net.daum.clix.springframework.data.rest.client.json.domain.Cat;
import net.daum.clix.springframework.data.rest.client.json.domain.Dog;
import net.daum.clix.springframework.data.rest.client.repository.RestRepositories;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.type.JavaType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.PagedResources.PageMetadata;
import org.springframework.hateoas.Resource;

@RunWith(MockitoJUnitRunner.class)
public class JacksonPolymorphicDeserializationPreProcessorTest {

	@Mock
	RestRepositories repositories;

	@InjectMocks
	JacksonPolymorphicDeserializationPreProcessor preProcessor;

	private PagedResources<Resource<Animal>> pagedResources;
	
	private Resource<Animal> resource;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		Resource<Animal> dog = new Resource<Animal>(new Dog("nameofdog", 1.0), new Link(
				"http://anydomainaddr.com/dog/1", "self"));
		Resource<Animal> cat = new Resource<Animal>(new Cat("nameofcat", 3), new Link(
				"http://anydomainaddr.com/cat/1", "self"));
		Resource<Animal> dog2 = new Resource<Animal>(new Dog("nameofdog2", 10.2), new Link(
				"http://anydomainaddr.com/dog/2", "self"));

		Collection<Resource<Animal>> animals = Arrays.asList(dog, cat, dog2);
		PageMetadata metadata = new PageMetadata(10, 1, 3, 1);
		List<Link> asList = Arrays.asList(new Link("http://1.2.3.4/dog/2", "next"), new Link(
				"http://1.2.3.4/dog/search", "search"));

		pagedResources = new PagedResources<Resource<Animal>>(animals, metadata, asList);
		resource = dog;
		
		when(repositories.findDomainClassNameFor("dog")).thenReturn(Dog.class.getName());
		when(repositories.findDomainClassNameFor("cat")).thenReturn(Cat.class.getName());
	}

	@Test
	public void canProcessPolymorphicTypeOnly() {
		assertTrue(preProcessor.canProcess(Animal.class));
		assertFalse(preProcessor.canProcess(Dog.class));
		assertFalse(preProcessor.canProcess(Cat.class));
	}
	
	@Test
	public void processShouldAddClassPropertyProperly() throws JsonParseException, JsonMappingException, IOException {
		byte[] processed = preProcessor.process(new ObjectMapper().writeValueAsBytes(resource), Resource.class, Animal.class);
		
		ObjectMapper mapper = new ObjectMapper();
		JavaType valueType = mapper.getTypeFactory().constructParametricType(Resource.class, Animal.class);
		Resource<Animal> processedResource = mapper.readValue(processed, valueType);
		
		assertEquals(resource, processedResource);
	}

	@Test
	public void processShouldAddClassPropertyProperlyToResources() throws JsonParseException, JsonMappingException, IOException {
		byte[] processed = preProcessor.process(new ObjectMapper().writeValueAsBytes(pagedResources), PagedResources.class, Animal.class);
		
		ObjectMapper mapper = new ObjectMapper();
		TypeFactory typeFactory = mapper.getTypeFactory();
		JavaType wrappedType = typeFactory.constructParametricType(Resource.class, Animal.class);
		JavaType valueType = typeFactory.constructParametricType(PagedResources.class, wrappedType);
		PagedResources<Resource<Animal>> processedPagedResources = mapper.readValue(processed, valueType);
		
		assertEquals(pagedResources, processedPagedResources);
	}
}

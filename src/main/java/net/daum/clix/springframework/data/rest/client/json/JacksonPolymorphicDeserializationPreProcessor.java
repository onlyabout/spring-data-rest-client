package net.daum.clix.springframework.data.rest.client.json; 
import java.lang.reflect.Modifier;

import net.daum.clix.springframework.data.rest.client.repository.RestRepositories;

import org.springframework.hateoas.Link;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Used to prepare json before polymorphic deserialization process by adding
 * '@class' property to json object.
 * 
 * @author 84june
 * 
 */
public class JacksonPolymorphicDeserializationPreProcessor implements JsonProcessor {

	private RestRepositories repositories;

	public JacksonPolymorphicDeserializationPreProcessor(RestRepositories repositories) {
		Assert.notNull(repositories);

		this.repositories = repositories;
	}

	@Override
	public boolean canProcess(Class<?> objectType) {
		return Modifier.isAbstract(objectType.getModifiers());
	}

	@Override
	public byte[] process(byte[] jsonBody, Class<?> resourceType, Class<?> objectType) {
		JsonParser parser = new JsonParser();
		JsonObject json = parser.parse(new String(jsonBody)).getAsJsonObject();
		boolean hasMultipleObjects = json.has("content");

		if (hasMultipleObjects) {
			for (JsonElement element : json.get("content").getAsJsonArray()) {
				addClassProperty(element.getAsJsonObject());
			}
		} else {
			addClassProperty(json);
		}

		return json.toString().getBytes();
	}
	
	private void addClassProperty(JsonObject object) {
		JsonArray links = object.get("links").getAsJsonArray();
		
		for (JsonElement ele : links) {
			JsonObject link = ele.getAsJsonObject();
			if (Link.REL_SELF.equals(link.get("rel").getAsString())) {
				String resourcePath = getResourcePathFromSelfHref(link.get("href").getAsString());
				String className = repositories.findDomainClassNameFor(resourcePath);
				object.addProperty("@class", className);
				break;
			}
		}
	}

	private String getResourcePathFromSelfHref(String selfHref) {
		String[] tkns = StringUtils.tokenizeToStringArray(selfHref, "/");
		return tkns[tkns.length - 2];
	}

}

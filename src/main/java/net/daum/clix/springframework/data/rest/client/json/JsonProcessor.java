package net.daum.clix.springframework.data.rest.client.json;


/**
 * Used to prepare json body before (de)serialization process.
 * 
 * @author 84june
 *
 */
public interface JsonProcessor {

	boolean canProcess(Class<?> objectType);

	byte[] process(byte[] jsonBody, Class<?> resourceType, Class<?> objectType);

}

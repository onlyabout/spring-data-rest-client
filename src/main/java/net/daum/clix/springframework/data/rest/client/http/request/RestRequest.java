package net.daum.clix.springframework.data.rest.client.http.request;

@Deprecated
public interface RestRequest {

	String getUrlWithPath(String restServerUrl);
	
	String path();


}

package net.daum.clix.springframework.data.rest.client.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.Map;

import net.daum.clix.springframework.data.rest.client.json.JacksonJsonSerializer;
import net.daum.clix.springframework.data.rest.client.json.JsonSerializer;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpMessage;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.util.StringUtils;

public class CommonsRestClient extends RestClientBase {

	private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

	private HttpClient client;

	private JsonSerializer jsonSerializer;

	private BasicHeader defaultHeader;

	public CommonsRestClient(String restServerUrl) {
		super(restServerUrl);

		// SchemeRegistry schemeRegistry = new SchemeRegistry();
		// schemeRegistry.register(
		// new Scheme("http", 8080, PlainSocketFactory.getSocketFactory()));
		// schemeRegistry.register(
		// new Scheme("https", 443, SSLSocketFactory.getSocketFactory()));
		// PoolingClientConnectionManager cm = new
		// PoolingClientConnectionManager(schemeRegistry);
		//
		// cm.setMaxTotal(100);
		// // Increase default max connection per route to 20
		// cm.setDefaultMaxPerRoute(20);
		// // Increase max connections for localhost:80 to 50
		// HttpHost localhost = new HttpHost("locahost", 8080);
		// cm.setMaxPerRoute(new HttpRoute(localhost), 50);

		this.client = new DefaultHttpClient();
		this.defaultHeader = new BasicHeader("accept", "application/json");

		this.jsonSerializer = new JacksonJsonSerializer();
	}

	@Override
	public ResourceSupport executeGet(String url, Type resourceType, Type objectType) {
		HttpGet req = (HttpGet) setDefaultHeader(new HttpGet(url));
		HttpResponse res = execute(req);

		return (ResourceSupport) jsonSerializer.deserialize(getBody(res), resourceType, objectType);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected <K, V> Resource<Map<K, Resource<V>>> executeGetForMap(String url, Type resourceType, Type keyType,
			Type valueType) {
		HttpGet req = (HttpGet) setDefaultHeader(new HttpGet(url));
		HttpResponse res = execute(req);

		return (Resource<Map<K, Resource<V>>>) jsonSerializer.deserializeMapResource(getBody(res), resourceType,
				keyType, valueType);
	}

	@Override
	protected void executeDelete(String url) {
		HttpDelete req = (HttpDelete) setDefaultHeader(new HttpDelete(url));
		HttpResponse res = execute(req);

		int statusCode = res.getStatusLine().getStatusCode();

		EntityUtils.consumeQuietly(res.getEntity());

		if (statusCode == HttpStatus.SC_INTERNAL_SERVER_ERROR)
			throw new UnsupportedOperationException(req.getMethod() + " failed for " + url + "\treason : "
					+ res.getStatusLine().getReasonPhrase());
	}

	@Override
	protected <S> String executePost(String url, S entity) {
		HttpPost req = (HttpPost) setDefaultHeader(new HttpPost(url));
		return executePutOrPostRequest(req, entity);
	}

	@Override
	protected <S> String executePut(String url, S entity) {
		if (url.indexOf("?") != -1)
			url += "&returnBody=true";
		else
			url += "?returnBody=true";

		HttpPut req = (HttpPut) setDefaultHeader(new HttpPut(url));
		return executePutOrPostRequest(req, entity);
	}

	private <S> String executePutOrPostRequest(HttpEntityEnclosingRequest httpRequest, S entity) {
		byte[] body = jsonSerializer.serialize(entity);

		ByteArrayEntity httpEntity = new ByteArrayEntity(body);
		httpEntity.setContentEncoding(Charset.forName("UTF-8").name());
		httpEntity.setContentType("application/json");

		httpRequest.setEntity(httpEntity);

		HttpResponse res = execute((HttpUriRequest) httpRequest);
		String savedLocation = null;
		if (res.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED)
			savedLocation = res.getHeaders("Location")[0].getValue();

		if (!StringUtils.hasText(savedLocation) && res.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
			savedLocation = ((HttpUriRequest)httpRequest).getURI().toString();
			
		return savedLocation;
	}

	private byte[] getBody(HttpResponse httpResponse) {

		try {

			if (HttpStatus.SC_OK != httpResponse.getStatusLine().getStatusCode()) {
				return null;
			}

			InputStream is = httpResponse.getEntity().getContent();
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			int nRead;
			// byte[] data = new byte[16384];
			byte[] data = new byte[DEFAULT_BUFFER_SIZE];

			while ((nRead = is.read(data, 0, data.length)) != -1) {
				buffer.write(data, 0, nRead);
			}

			is.close();
			buffer.flush();
			EntityUtils.consumeQuietly(httpResponse.getEntity());

			return buffer.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	private HttpResponse execute(HttpUriRequest req) {
		try {
			return client.execute(req);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			((HttpRequestBase) req).releaseConnection();
		}
		return null;
	}

	private HttpMessage setDefaultHeader(HttpMessage httpMessage) {
		httpMessage.setHeader(defaultHeader);
		return httpMessage;
	}

}

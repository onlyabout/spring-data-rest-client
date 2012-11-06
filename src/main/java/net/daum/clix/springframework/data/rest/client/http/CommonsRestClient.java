package net.daum.clix.springframework.data.rest.client.http;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;

import net.daum.clix.springframework.data.rest.client.json.JacksonJsonSerializer;
import net.daum.clix.springframework.data.rest.client.json.JsonSerializer;
import net.daum.clix.springframework.data.rest.client.util.RestUrlUtil;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
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
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class CommonsRestClient extends RestClientBase implements DisposableBean {

	private HttpClient client;

	private JsonSerializer jsonSerializer;

	private BasicHeader defaultHeader;

	public CommonsRestClient(String restServerUrl) {
		super(restServerUrl);

		URL url = null;
		try {
			url = new URL(RestUrlUtil.normalize(restServerUrl));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		Assert.notNull(url);

		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme(url.getProtocol(), url.getPort(), PlainSocketFactory.getSocketFactory()));

		PoolingClientConnectionManager cm = new PoolingClientConnectionManager(schemeRegistry);
		cm.setMaxTotal(200);
		cm.setDefaultMaxPerRoute(20);
		cm.setMaxPerRoute(new HttpRoute(new HttpHost(url.getHost(), url.getPort())), 50);

		this.client = new DefaultHttpClient(cm);
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
			savedLocation = ((HttpUriRequest) httpRequest).getURI().toString();

		return savedLocation;
	}

	private byte[] getBody(HttpResponse httpResponse) {

		byte[] bytes = null;
		HttpEntity entity = httpResponse.getEntity();

		try {
			if (HttpStatus.SC_OK == httpResponse.getStatusLine().getStatusCode())
				bytes = EntityUtils.toByteArray(entity);
		} catch (IOException e) {
			e.printStackTrace();
		}

		EntityUtils.consumeQuietly(entity);
		return bytes;
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

	public void destroy() throws Exception {
		this.client.getConnectionManager().shutdown();
	}

}

package net.daum.clix.springframework.data.rest.client.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RestUrlUtil {

	public static String getIdFrom(String href) {
		String[] arr = href.split("/");
		return arr[arr.length - 1];
	}

	public static String normalize(String url) {
		url = removeLastSlash(url);
		url = addProtocolAndLowerDomain(url);
		return url;
	}

	private static String addProtocolAndLowerDomain(String url) {
		url = (url.startsWith("http://")) ? url : "http://" + url;
		String prefix = "http://" + getHost(url);
		url = prefix + url.substring(prefix.length());
		return url;
	}

	private static String getHost(String url) {
		url = url.trim();
		try {
			URI uri = new URI(url);
			return uri.getHost();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return url;
	}

	private static String removeLastSlash(String url) {
		if (url == null)
			return null;
		url = url.trim();

		Pattern pattern = Pattern.compile("(/)+$");
		Matcher matcher = pattern.matcher(url);
		if (matcher.find()) {
			url = url.substring(0, matcher.start());
		}
		url = url.trim();

		int idx = url.lastIndexOf("/");
		if (idx < 0)
			return url;
		if (idx == url.length() - 1) {
			return url.substring(0, idx);
		} else {
			return url;
		}
	}
}
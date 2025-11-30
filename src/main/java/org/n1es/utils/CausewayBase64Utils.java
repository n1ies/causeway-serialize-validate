package org.n1es.utils;

import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.function.UnaryOperator;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


public class CausewayBase64Utils {

	public static final BytesOperator asCompressedUrlBase64 = operator()
			.andThen(CausewayBase64Utils::compress)
			.andThen(bytes -> Base64.getUrlEncoder().encode(bytes));

	public static final BytesOperator ofCompressedUrlBase64 = operator()
			.andThen(bytes -> Base64.getUrlDecoder().decode(bytes))
			.andThen(CausewayBase64Utils::decompress);

	public static BytesOperator operator() {
		return new BytesOperator(UnaryOperator.identity());
	}

	static byte[] compress(final byte[] input) {
		if (input.length < 18) {
			return input;
		} else {
			return input.length < 256 ? prepend(input, new byte[]{0}) : prepend(gzip_compress(input), new byte[]{1});
		}
	}

	static byte[] decompress(final byte[] input) {
		if (input != null && input.length >= 18) {
			byte[] inputWithoutPrefix = Arrays.copyOfRange(input, 1, input.length);
			return gzip_decompress(inputWithoutPrefix);
		} else {
			return input;
		}
	}

	public static final byte[] prepend(@Nullable final byte[] target, @Nullable final byte... bytes) {
		if (target == null) {
			return bytes == null ? null : (bytes).clone();
		} else if (bytes == null) {
			return (target).clone();
		} else {
			byte[] result = new byte[target.length + bytes.length];
			System.arraycopy(bytes, 0, result, 0, bytes.length);
			System.arraycopy(target, 0, result, bytes.length, target.length);
			return result;
		}
	}


	public static byte[] gzip_compress(final byte[] input) {
		try {
			int                   BUFFER_SIZE = Math.max(256, input.length);
			ByteArrayOutputStream os          = new ByteArrayOutputStream(BUFFER_SIZE);
			GZIPOutputStream      gos         = new GZIPOutputStream(os);
			gos.write(input);
			gos.close();
			return os.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static byte[] gzip_decompress(final byte[] compressed) {
		try {
			ByteArrayInputStream  is   = new ByteArrayInputStream(compressed);
			GZIPInputStream       gis  = new GZIPInputStream(is, 32);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[]                data = new byte[32];

			int bytesRead;
			while ((bytesRead = gis.read(data)) != -1) {
				baos.write(data, 0, bytesRead);
			}

			gis.close();
			return baos.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}


	public static final class BytesOperator {

		private final UnaryOperator<byte[]> operator;

		private BytesOperator(final UnaryOperator<byte[]> operator) {
			if (operator == null) {
				throw new NullPointerException("operator cannot be null");
			}
			this.operator = operator;
		}

		public byte[] apply(final byte[] input) {
			return operator.apply(input);
		}

		public BytesOperator andThen(final UnaryOperator<byte[]> andThen) {
			try {
				return new BytesOperator(s -> andThen.apply(operator.apply(s)));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

}

package uk.co.alumeni.prism.interceptors;

import javax.crypto.SecretKey;

public final class KeyContextHolder {

    private KeyContextHolder() {
    }

	private static final ThreadLocal<SecretKey> threadLocal = new ThreadLocal<SecretKey>();

	public static void setContext(SecretKey key) {
		threadLocal.set(key);
	}

	public static SecretKey getContext() {
		return threadLocal.get();
	}

	public static void clearContext() {
		threadLocal.remove();

	}
}

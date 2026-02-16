package io.clavis.core.http;

import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Factory for creating configured OkHttpClient instances.
 *
 * <p>
 * Each client is preconfigured with appropriate timeouts,
 * logging, and optional rate limiting.
 * </p>
 *
 * @author CLAVIS Team
 * @version 1.0.0
 * @since 2025-01-01
 */
public final class HttpClientFactory {

    private static final Logger logger = LoggerFactory.getLogger(HttpClientFactory.class);

    private static final int DEFAULT_CONNECT_TIMEOUT = 30;
    private static final int DEFAULT_READ_TIMEOUT = 30;
    private static final int DEFAULT_WRITE_TIMEOUT = 30;

    private HttpClientFactory() {
        // Static factory — no instances
    }

    /**
     * Creates an OkHttpClient with default timeouts.
     *
     * @return a new OkHttpClient instance
     */
    public static OkHttpClient createDefault() {
        return createBuilder()
                .build();
    }

    /**
     * Creates an OkHttpClient with custom timeouts.
     *
     * @param connectTimeoutSeconds connect timeout in seconds
     * @param readTimeoutSeconds    read timeout in seconds
     * @return a new OkHttpClient instance
     */
    public static OkHttpClient create(int connectTimeoutSeconds, int readTimeoutSeconds) {
        return createBuilder()
                .connectTimeout(connectTimeoutSeconds, TimeUnit.SECONDS)
                .readTimeout(readTimeoutSeconds, TimeUnit.SECONDS)
                .build();
    }

    /**
     * Creates a preconfigured OkHttpClient builder.
     *
     * @return an OkHttpClient.Builder with default settings
     */
    public static OkHttpClient.Builder createBuilder() {
        logger.debug("Creating HTTP client with default timeouts");
        return new OkHttpClient.Builder()
                .connectTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_WRITE_TIMEOUT, TimeUnit.SECONDS);
    }
}

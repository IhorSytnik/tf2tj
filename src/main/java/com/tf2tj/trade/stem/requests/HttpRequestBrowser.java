package com.tf2tj.trade.stem.requests;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A class for different HTTP requests.
 *
 * @author Ihor Sytnik
 */
public class HttpRequestBrowser implements GetBrowser {
    private final WebClient client;
    private final String cookie;
    private final HttpHeaders commonHeaders;
    private final long sleepMilliseconds;

    /**
     *
     * @param baseUrl a URL to be used as a base (e.g. "https://steamcommunity.com"), to be extended later by additional
     *                paths (e.g. "/discussions/").
     * @param followRedirects {@code true} if you want it to follow redirects, {@code false} - otherwise.
     * @param cookieFileName name of the cookie file.
     * @param commonHeaders headers that are usually shared between requests on the <b>baseUrl</b> web-site.
     * @param sleepMilliseconds delay between requests.
     * @throws IOException see {@link Files#readString(Path)}, occurs when trying to read a cookie file.
     */
    public HttpRequestBrowser(String baseUrl, boolean followRedirects, String cookieFileName,
                              HttpHeaders commonHeaders, long sleepMilliseconds)
            throws IOException {
        this.client = WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create().followRedirect(followRedirects)
                ))
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize(16 * 1024 * 1024))
                        .build())
                .build();
        cookie = Files.readString(Path.of(cookieFileName));
        this.commonHeaders = commonHeaders;
        this.sleepMilliseconds = sleepMilliseconds;
    }

    /**
     * Makes a request to <b>uri</b>, with http method <b>method</b> and headers <b>headers</b>.
     *
     * @param uri a URI the request should be sent to.
     * @param method http request method.
     * @param headers request headers.
     * @return response.
     * @throws InterruptedException if any thread has interrupted the current thread. See {@link Thread#sleep(long)}.
     */
    public WebClient.ResponseSpec request(String uri, HttpMethod method, HttpHeaders headers)
            throws InterruptedException {
        Thread.sleep(sleepMilliseconds);
        return client.method(method)
                .uri(uri)
                .headers(httpHeaders -> {
                    httpHeaders.setAll(headers.toSingleValueMap());
                    httpHeaders.add(HttpHeaders.COOKIE, cookie);
                })
                .retrieve();
    }

    /**
     * A shortcut method to make a GET request, with common headers to the given <b>uri</b>.
     *
     * @param uri an uri the request should be sent to.
     * @return response.
     * @throws InterruptedException see {@link #request(String, HttpMethod, HttpHeaders)}.
     */
    public WebClient.ResponseSpec get(String uri) throws InterruptedException {
        return request(uri, HttpMethod.GET, commonHeaders);
    }

    /**
     * Gets web-page's source code.
     *
     * @param uri URI of the page which source we will be getting.
     * @return web-page's source code by url.
     */
    @Override
    public String getSource(String uri) throws InterruptedException {
        return get(uri).bodyToMono(String.class).doOnError(RuntimeException::new).block();
    }
}

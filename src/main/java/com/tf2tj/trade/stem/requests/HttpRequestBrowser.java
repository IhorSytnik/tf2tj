package com.tf2tj.trade.stem.requests;

import com.google.common.util.concurrent.RateLimiter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import org.springframework.core.io.buffer.DataBufferUtils;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * A class for different HTTP requests.
 *
 * @author Ihor Sytnik
 */
public class HttpRequestBrowser implements GetBrowser {
    private final WebClient client;
    private String cookie;
    private final String cookieFileName;
    private final HttpHeaders commonHeaders;
    private final RateLimiter limiter;

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
        this.client = createBasicClient(followRedirects).baseUrl(baseUrl).build();
        this.cookieFileName = cookieFileName;
        updateCookies();
        this.commonHeaders = commonHeaders;
        this.limiter = RateLimiter.create(1000d / sleepMilliseconds);
    }

    public HttpRequestBrowser(boolean followRedirects, long sleepMilliseconds) {
        this.client = createBasicClient(followRedirects).build();
        this.cookieFileName = "";
        this.commonHeaders = new HttpHeaders();
        this.limiter = RateLimiter.create(1000d / sleepMilliseconds);
    }

    private WebClient.Builder createBasicClient(boolean followRedirects) {
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create().followRedirect(followRedirects)
                ))
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize(16 * 1024 * 1024))
                        .build());
    }

    @Override
    public void updateCookies() throws IOException {
        if (new File(cookieFileName).exists())
            cookie = Files.readString(Path.of(cookieFileName));
    }

    /**
     * Makes a request to <b>uri</b>, with http method <b>method</b> and headers <b>headers</b>.
     *
     * @param uri a URI the request should be sent to.
     * @param method http request method.
     * @param headers request headers.
     * @return response.
     */
    public WebClient.ResponseSpec request(String uri, HttpMethod method, HttpHeaders headers, String body) {
        limiter.acquire();
        return client.method(method)
                .uri(uri)
                .body(BodyInserters.fromValue(body))
                .headers(httpHeaders -> {
                    httpHeaders.setAll(headers.toSingleValueMap());
                    httpHeaders.add(HttpHeaders.COOKIE, cookie);
                })
                .retrieve()
//    todo check if works
                .onStatus(HttpStatusCode::isError, clientResponse -> {
                    logTraceResponse(clientResponse);
                    return Mono.error(new IllegalStateException());
                });
    }

//    todo check if works (sout logging is temporary)
    private void logTraceResponse(ClientResponse response) {
        System.out.println(response.statusCode());
        System.out.println(response.headers().asHttpHeaders());
        response.bodyToMono(String.class)
                .subscribe(System.out::println);
    }

//    public void requestToMono(String uri, HttpMethod method, String body)
//            throws InterruptedException {
//        Thread.sleep(sleepMilliseconds);
//
//        client.method(method)
//                .uri(uri)
//                .body(BodyInserters.fromValue(body))
//                .headers(httpHeaders -> {
//                    httpHeaders.setAll(commonHeaders.toSingleValueMap());
//                    httpHeaders.add(HttpHeaders.COOKIE, cookie);
//                })
//                .exchangeToMono(response -> {
//                    if (response.statusCode().is2xxSuccessful() || response.statusCode().is3xxRedirection()) {
//                        MultiValueMap<String, ResponseCookie> cookieMultiValueMap = response.cookies();
//                        ClientResponse.Headers headerMultiValueMap = response.headers();
//                        DefaultClientResponse response1 = response;
//                        return response.bodyToMono(String.class);
//                    } else {
//                        return response.createError();
//                    }
//                }).block();
//    }
//
//    public WebClient.ResponseSpec request(String uri, HttpMethod method, HttpHeaders headers,
//                                           String body)
//            throws InterruptedException {
//        Thread.sleep(sleepMilliseconds);
//        return client.method(method)
//                .uri(uri)
//                .body(BodyInserters.fromValue(body))
//                .headers(httpHeaders -> {
//                    httpHeaders.setAll(headers.toSingleValueMap());
//                    httpHeaders.add(HttpHeaders.COOKIE, cookie);
//                })
//                .retrieve();
//    }

//    public WebClient.ResponseSpec post(String uri, HttpHeaders addedOrChangedHeaders,
//                                       MultiValueMap<String, String> body) throws InterruptedException {
//        return request(uri, HttpMethod.POST, addedOrChangedHeaders, body);
//    }

    public WebClient.ResponseSpec post(String uri, HttpHeaders addedOrChangedHeaders,
                                       String body) throws InterruptedException {
        return request(uri, HttpMethod.POST, addedOrChangedHeaders, body);
    }

    /**
     * A shortcut method to make a GET request, with common headers to the given <b>uri</b>.
     *
     * @param uri an uri the request should be sent to.
     * @return response.
     * @throws InterruptedException see {@link #request(String, HttpMethod, HttpHeaders, String)}.
     */
    public WebClient.ResponseSpec get(String uri, HttpHeaders addedOrChangedHeaders) throws InterruptedException {
        return request(uri, HttpMethod.GET, addedOrChangedHeaders, "");
    }

    /**
     * Gets web-page's source code.
     *
     * @param uri URI of the page which source we will be getting.
     * @return web-page's source code by url.
     */
    @Override
    public String getSource(String uri) throws InterruptedException {
        return get(uri, commonHeaders).bodyToMono(String.class).doOnError(RuntimeException::new).block();
    }

    @Override
    public String getSource(String uri, HttpHeaders additionalHeaders) throws InterruptedException {
        additionalHeaders.addAll(commonHeaders);
        return get(uri, additionalHeaders).bodyToMono(String.class).doOnError(RuntimeException::new).block();
    }

    public void downloadGet(String uri, HttpHeaders addedOrChangedHeaders, Path path)
            throws InterruptedException {
        Flux<DataBuffer> dataBufferFlux = get(uri, addedOrChangedHeaders).bodyToFlux(DataBuffer.class);
        DataBufferUtils.write(dataBufferFlux, path, StandardOpenOption.CREATE).block();
    }
}

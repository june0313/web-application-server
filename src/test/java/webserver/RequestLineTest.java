package webserver;

import org.junit.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class RequestLineTest {
    @Test
    public void testGetUriWithoutQueryString() {
        RequestLine sut = RequestLine.of("GET /index.html?a=1&b=2 HTTP/1.1");

        String result = sut.getUriWithoutQueryString();

        assertThat(result).isEqualTo("/index.html");
    }

    @Test
    public void testGetUri() {
        RequestLine sut = RequestLine.of("GET /index.html?a=1&b=2 HTTP/1.1");

        String result = sut.getUri();

        assertThat(result).isEqualTo("/index.html?a=1&b=2");
    }

    @Test
    public void testGetQueryString() {
        RequestLine sut = RequestLine.of("GET /index.html?a=1&b=2 HTTP/1.1");

        String result = sut.getQueryString();

        assertThat(result).isEqualTo("a=1&b=2");
    }

    @Test
    public void testGetParameters() {
        RequestLine sut = RequestLine.of("GET /index.html?a=1&b=2 HTTP/1.1");

        Map<String, String> result = sut.getParameters();

        assertThat(result)
                .isNotEmpty()
                .hasSize(2)
                .containsEntry("a", "1")
                .containsEntry("b", "2");
    }

    @Test
    public void testGetQueryStringWhenAbsent() {
        RequestLine sut = RequestLine.of("GET /index.html HTTP/1.1");

        String result = sut.getQueryString();

        assertThat(result).isEmpty();
    }

    @Test
    public void testGetMethod() {
        RequestLine sut = RequestLine.of("GET /index.html HTTP/1.1");
        assertThat(sut.getMethod()).isEqualTo(RequestMethod.GET);
    }

}
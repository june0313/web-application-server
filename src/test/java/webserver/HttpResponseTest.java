package webserver;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;

import static org.assertj.core.api.Assertions.assertThat;

public class HttpResponseTest {
    private static final String BASE_PATH = "./src/test/resources/webapp";
    private ByteArrayOutputStream out;

    @Before
    public void setUp() {
        out = new ByteArrayOutputStream();
    }

    @Test
    public void responseForward() {
        HttpResponse httpResponse = HttpResponse.of(out, BASE_PATH);
        httpResponse.forward("/index.html");

        final String[] results = out.toString().split("\r\n");
        assertThat(results[0]).isEqualTo("HTTP/1.1 200 OK ");
        assertThat(results[1]).isEqualTo("Content-Length: 13 ");
        assertThat(results[2]).isEqualTo("");
        assertThat(results[3]).isEqualTo("<h1>test</h1>");
    }

    @Test
    public void responseRedirect() {
        HttpResponse httpResponse = HttpResponse.of(out, BASE_PATH);
        httpResponse.sendRedirect("/index.html");

        final String[] results = out.toString().split("\r\n");
        assertThat(results[0]).isEqualTo("HTTP/1.1 302 Found ");
        assertThat(results[1]).isEqualTo("Location: /index.html ");
    }

    @Test
    public void responseCookie() {
        final HttpResponse httpResponse = HttpResponse.of(out, BASE_PATH);
        httpResponse.addHeader("Set-Cookie", "login=true");
        httpResponse.sendRedirect("/index.html");

        final String[] results = out.toString().split("\r\n");
        assertThat(results[0]).isEqualTo("HTTP/1.1 302 Found ");
        assertThat(results[1]).isEqualTo("Location: /index.html ");
        assertThat(results[2]).isEqualTo("Set-Cookie: login=true ");
    }
}
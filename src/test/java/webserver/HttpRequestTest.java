package webserver;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class HttpRequestTest {
    private final String testDirectory = "./src/test/resources/";

    @Test
    public void testGetRequest() throws Exception {
        InputStream inputStream = new FileInputStream(new File(testDirectory + "Http_GET.txt"));
        HttpRequest request = HttpRequest.of(inputStream);

        assertThat(request.getMethod()).isEqualTo(RequestMethod.GET);
        assertThat(request.getPath()).isEqualTo("/user/create");
        assertThat(request.getHeader("Connection")).isEqualTo("keep-alive");
        assertThat(request.getParameter("userId")).isEqualTo("june0313");
        assertThat(request.getParameter("password")).isEqualTo("1234");
        assertThat(request.getCookie("login")).isEqualTo("true");
        assertThat(request.getCookie("sessionId")).isEqualTo("1111");
    }

    @Test
    public void testPostRequest() throws Exception {
        InputStream inputStream = new FileInputStream(new File(testDirectory + "Http_POST.txt"));
        HttpRequest request = HttpRequest.of(inputStream);

        assertThat(request.getMethod()).isEqualTo(RequestMethod.POST);
        assertThat(request.getPath()).isEqualTo("/user/create");
        assertThat(request.getHeader("Connection")).isEqualTo("keep-alive");
        assertThat(request.getHeader("Content-Length")).isEqualTo("39");
        assertThat(request.getHeader("Content-Type")).isEqualTo("application/x-www-form-urlencoded");
        assertThat(request.getParameter("userId")).isEqualTo("june0313");
        assertThat(request.getParameter("password")).isEqualTo("1234");
        assertThat(request.getCookie("login")).isEqualTo("true");
        assertThat(request.getCookie("sessionId")).isEqualTo("1111");
    }
}

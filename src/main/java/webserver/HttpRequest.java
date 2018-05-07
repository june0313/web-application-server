package webserver;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import util.HttpRequestUtils;
import util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Optional;

import static util.HttpRequestUtils.parseHeader;

public class HttpRequest {
    private RequestLine requestLine;
    private Map<String, String> headers;
    private Map<String, String> parameters;
    private Map<String, String> cookies;

    public static HttpRequest of(InputStream inputStream) throws IOException {
        return new HttpRequest(inputStream);
    }

    private HttpRequest(InputStream inputStream) throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Charsets.UTF_8));
        this.requestLine = RequestLine.of(bufferedReader.readLine());
        this.headers = buildRequestHeader(bufferedReader);
        this.cookies = buildCookies();
        this.parameters = buildParameters(bufferedReader);
    }

    private Map<String, String> buildCookies() {
        return HttpRequestUtils.parseCookies(getHeader("Cookie"));
    }

    private Map<String, String> buildParameters(BufferedReader reader) throws IOException {
        if (getMethod().isPost()) {
            final int contentLength = Integer.parseInt(headers.get("Content-Length"));
            final String body = IOUtils.readData(reader, contentLength);
            return HttpRequestUtils.parseQueryString(body);
        }

        return this.requestLine.getParameters();
    }

    private Map<String, String> buildRequestHeader(BufferedReader reader) throws IOException {
        Map<String, String> header = Maps.newHashMap();

        String line = reader.readLine();

        while (!"".equals(line)) {
            Optional.ofNullable(parseHeader(line)).ifPresent(pair -> header.put(pair.getKey(), pair.getValue()));
            line = reader.readLine();
        }

        return header;
    }

    public RequestMethod getMethod() {
        return this.requestLine.getMethod();
    }

    public String getPath() {
        return this.requestLine.getUriWithoutQueryString();
    }

    public String getHeader(String headerName) {
        return this.headers.get(headerName);
    }

    public String getParameter(String parameterName) {
        return this.parameters.get(parameterName);
    }

    public String getCookie(String cookieName) {
        return this.cookies.get(cookieName);
    }
}

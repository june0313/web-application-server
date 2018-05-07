package webserver;

import com.google.common.base.Splitter;
import util.HttpRequestUtils;

import java.util.List;
import java.util.Map;

class RequestLine {
    private RequestMethod method;
    private String uri;
    private String uriWithoutQueryString;
    private String queryString;
    private Map<String, String> parameters;

    private RequestLine(String requestLine) {
        final List<String> requestLineTokens = Splitter.on(" ").splitToList(requestLine);
        this.method = RequestMethod.find(requestLineTokens.get(0));
        this.uri = requestLineTokens.get(1);

        final List<String> uriTokens = Splitter.on("?").splitToList(requestLineTokens.get(1));
        this.uriWithoutQueryString = uriTokens.get(0);
        this.queryString = uriTokens.size() > 1 ? uriTokens.get(1) : "";
        this.parameters = HttpRequestUtils.parseQueryString(this.queryString);
    }

    static RequestLine of(String requestLine) {
        return new RequestLine(requestLine);
    }

    public String getUri() {
        return this.uri;
    }

    public String getUriWithoutQueryString() {
        return this.uriWithoutQueryString;
    }

    public String getQueryString() {
        return this.queryString;
    }

    public RequestMethod getMethod() {
        return this.method;
    }

    public Map<String, String> getParameters() {
        return this.parameters;
    }
}

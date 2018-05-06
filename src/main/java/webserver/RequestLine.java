package webserver;

import util.HttpRequestUtils;

import java.util.Map;

class RequestLine {
    private RequestMethod requestMethod;
    private String requestUri;
    private String requestResource;
    private String requestParam;
    private Map<String, String> parameters;

    private RequestLine(String requestLine) {
        this.requestMethod = RequestMethod.find(requestLine.split(" ")[0]);
        this.requestUri = requestLine.split(" ")[1];
        this.requestResource = this.requestUri.split("\\?")[0];
        this.requestParam = extractParams();
        this.parameters = HttpRequestUtils.parseQueryString(this.requestParam);
    }

    private String extractParams() {
        if (hasParams()) {
            return this.requestUri.split("\\?")[1];
        }

        return "";
    }

    private boolean hasParams() {
        return this.requestUri.split("\\?").length > 1;
    }

    static RequestLine of(String requestLine) {
        return new RequestLine(requestLine);
    }

    public String getRequestResource() {
        return requestResource;
    }

    public String getRequestParam() {
        return requestParam;
    }

    public RequestMethod getRequestMethod() {
        return this.requestMethod;
    }

    public Map<String, String> getParameters() {
        return this.parameters;
    }
}

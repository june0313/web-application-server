package webserver;

import java.util.Map;

import static java.util.Arrays.stream;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public enum RequestMethod {
    GET,
    POST,
    UNKNOWN;

    private static final Map<String, RequestMethod> INDEX = stream(values()).collect(toMap(Enum::name, identity()));

    public static RequestMethod find(String method) {
        return INDEX.getOrDefault(method, UNKNOWN);
    }

    public boolean isPost() {
        return this == POST;
    }
}

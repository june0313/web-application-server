package webserver;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.stream.Collectors;

public class Cookie {
    private Map<String, String> cookieMap;
    private static String SET_COOKIE_RESPONSE_FORMAT = "Set-Cookie: %s=%s \r\n";

    private Cookie(Map<String, String> cookieMap) {
        this.cookieMap = cookieMap;
    }

    public static Cookie create() {
        return new Cookie(Maps.newHashMap());
    }


    public void addCookie(String name, String value) {
        this.cookieMap.put(name, value);
    }

    public String getCookie(String name) {
        return cookieMap.getOrDefault(name, "");
    }

    public String getCookieResponseHeader() {
        return this.cookieMap.entrySet().stream()
                .map(entry -> String.format(SET_COOKIE_RESPONSE_FORMAT, entry.getKey(), entry.getValue()))
                .collect(Collectors.joining());
    }
}

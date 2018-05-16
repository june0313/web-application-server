package webserver.session;

import com.google.common.collect.Maps;

import java.util.Map;

public class HttpSession {
    private String id;
    private Map<String, Object> attributes;

    public static HttpSession newInstance(String id) {
        return new HttpSession(id);
    }

    private HttpSession(String id) {
        this.id = id;
        this.attributes = Maps.newHashMap();
    }

    public String getId() {
        return id;
    }

    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }

    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    public void removeAttribute(String name) {
        attributes.remove(name);
    }

    public void invalidate() {
        HttpSessionRepository.remove(getId());
    }


}

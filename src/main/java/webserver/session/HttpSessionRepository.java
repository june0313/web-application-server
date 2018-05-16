package webserver.session;

import com.google.common.collect.Maps;

import java.util.Map;

public class HttpSessionRepository {
    private static final Map<String, HttpSession> sessions;

    private HttpSessionRepository() {
    }

    static {
        sessions = Maps.newHashMap();
    }

    public static HttpSession getSession(String id) {
        return sessions.computeIfAbsent(id, HttpSession::newInstance);
    }

    public static void remove(String id) {
        sessions.remove(id);
    }
}

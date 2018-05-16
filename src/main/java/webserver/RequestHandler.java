package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.controller.Controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.UUID;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            final HttpRequest httpRequest = HttpRequest.of(in);
            final HttpResponse httpResponse = HttpResponse.of(out, "./webapp");

            if (getSessionId(httpRequest) == null) {
                httpResponse.addHeader("Set-Cookie", "JSESSIONID=" + UUID.randomUUID());
            }

            final Controller controller = RequestMapping.findController(httpRequest.getPath());

            if (controller == null) {
                httpResponse.forward(getDefaultPath(httpRequest.getPath()));
            } else {
                controller.service(httpRequest, httpResponse);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private String getSessionId(HttpRequest httpRequest) {
        return httpRequest.getCookie("JSESSIONID");
    }

    private String getDefaultPath(String path) {
        return "/".equals(path) ? "/index.html" : path;
    }
}

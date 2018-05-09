package webserver;

import com.google.common.base.Strings;
import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Optional;
import java.util.stream.Collectors;

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

            if ("/user/create".equals(httpRequest.getPath())) {
                final String userId = httpRequest.getParameter("userId");
                final String password = httpRequest.getParameter("password");
                final String name = httpRequest.getParameter("name");
                final String email = httpRequest.getParameter("email");

                if (!Strings.isNullOrEmpty(userId) && !Strings.isNullOrEmpty(password) && !Strings.isNullOrEmpty(name) && !Strings.isNullOrEmpty(email)) {
                    User user = new User(userId, password, name, email);
                    DataBase.addUser(user);
                }

                httpResponse.sendRedirect("/index.html");
            } else if ("/user/login".equals(httpRequest.getPath())) {
                final String userId = httpRequest.getParameter("userId");
                final String password = httpRequest.getParameter("password");
                final User user = DataBase.findUserById(userId);
                final boolean loginSuccess = Optional.ofNullable(user)
                        .map(User::getPassword)
                        .map(p -> p.equals(password))
                        .orElse(Boolean.FALSE);

                if (loginSuccess) {
                    log.debug("login success");
                    httpResponse.addHeader("Set-Cookie", "login=true");
                    httpResponse.sendRedirect("/index.html");
                } else {
                    log.debug("login failed");
                    httpResponse.addHeader("Set-Cookie", "login=false");
                    httpResponse.sendRedirect("/user/login_failed.html");
                }
            } else if ("/user/list".equals(httpRequest.getPath())) {
                if (isLogin(httpRequest)) {
                    log.debug("user is logged in");

                    final String users = DataBase.findAll().stream()
                            .map(User::getName)
                            .map(name -> "<li>" + name + "</li>\n")
                            .collect(Collectors.joining("", "<h2>User list</h2>\n<ul>", "</ul>"));

                    httpResponse.setContentType("text/html;charset=utf-8");
                    httpResponse.forwardBody(users.getBytes());
                } else {
                    log.debug("user is not logged in. redirect to index.html");
                    httpResponse.sendRedirect("/user/login.html");
                }
            } else {
                if (httpRequest.getHeader("Accept").contains("text/css")) {
                    httpResponse.setContentType("text/css;charset=utf-8");
                } else {
                    httpResponse.setContentType("text/html;charset=utf-8");
                }
                httpResponse.forward(httpRequest.getPath());
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private Boolean isLogin(HttpRequest httpRequest) {
        return Optional.ofNullable(httpRequest.getCookie("login"))
                .map(Boolean::parseBoolean)
                .orElse(Boolean.FALSE);
    }
}

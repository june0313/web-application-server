package webserver;

import com.google.common.base.Strings;
import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Map;
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
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.

            final HttpRequest httpRequest = HttpRequest.of(in);
            DataOutputStream dos = new DataOutputStream(out);

            if ("/user/create".equals(httpRequest.getPath())) {
                final String userId = httpRequest.getParameter("userId");
                final String password = httpRequest.getParameter("password");
                final String name = httpRequest.getParameter("name");
                final String email = httpRequest.getParameter("email");

                if (!Strings.isNullOrEmpty(userId) && !Strings.isNullOrEmpty(password) && !Strings.isNullOrEmpty(name) && !Strings.isNullOrEmpty(email)) {
                    User user = new User(userId, password, name, email);
                    DataBase.addUser(user);
                }

                response302Header(dos, "/index.html");
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
                    final Cookie cookie = Cookie.create();
                    cookie.addCookie("login", "true");
                    response302Header(dos, "/index.html", cookie);
                } else {
                    log.debug("login failed");
                    final Cookie cookie = Cookie.create();
                    cookie.addCookie("login", "false");
                    response302Header(dos, "/user/login_failed.html", cookie);
                }
            } else if ("/user/list".equals(httpRequest.getPath())) {
                final String cookies = httpRequest.getHeader("Cookie");
                final Map<String, String> cookieMap = HttpRequestUtils.parseCookies(cookies);
                final Boolean isLoggedIn = Optional.ofNullable(cookieMap.get("login")).map(Boolean::parseBoolean).orElse(Boolean.FALSE);

                if (isLoggedIn) {
                    log.debug("user is logged in");

                    final String users = DataBase.findAll().stream()
                            .map(User::getName)
                            .map(name -> "<li>" + name + "</li>\n")
                            .collect(Collectors.joining("", "<h2>User list</h2>\n<ul>", "</ul>"));

                    response200Header(dos, users.length());
                    responseBody(dos, users.getBytes());
                } else {
                    log.debug("user is not logged in. redirect to index.html");
                    response302Header(dos, "/user/login.html");
                }
            } else {
                byte[] body = Files.readAllBytes(new File("./webapp" + httpRequest.getPath()).toPath());
                if (httpRequest.getHeader("Accept").contains("text/css")) {
                    response200CssHeader(dos, body.length);
                } else {
                    response200Header(dos, body.length);
                }
                responseBody(dos, body);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200CssHeader(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/css;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String location) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes(String.format("Location: %s\r\n", location));
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String location, Cookie cookie) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes(String.format("Location: %s\r\n", location));
            dos.writeBytes(cookie.getCookieResponseHeader());
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}

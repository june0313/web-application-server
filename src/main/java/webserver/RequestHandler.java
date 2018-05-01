package webserver;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.*;

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
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, Charset.forName("UTF-8")));
            DataOutputStream dos = new DataOutputStream(out);

            String line = bufferedReader.readLine();

            if (line == null) {
                return;
            }

            final RequestLine requestLine = RequestLine.of(line);
            final Map<String, String> requestHeader = buildRequestHeader(bufferedReader, line);
            final Map<String, String> requestParam = buildRequestParam(requestLine);
            final String requestBody = getRequestBody(bufferedReader, requestHeader);

            if ("/user/create".equals(requestLine.getRequestUri())) {
                Map<String, String> requestBodyMap = HttpRequestUtils.parseQueryString(requestBody);
                final String userId = requestBodyMap.get("userId");
                final String password = requestBodyMap.get("password");
                final String name = requestBodyMap.get("name");
                final String email = requestBodyMap.get("email");

                if (!Strings.isNullOrEmpty(userId) && !Strings.isNullOrEmpty(password) && !Strings.isNullOrEmpty(name) && !Strings.isNullOrEmpty(email)) {
                    User user = new User(userId, password, name, email);
                    DataBase.addUser(user);
                }

                response302Header(dos, "/index.html");
            } else if ("/user/login".equals(requestLine.getRequestUri())) {
                Map<String, String> requestBodyMap = HttpRequestUtils.parseQueryString(requestBody);
                final String userId = requestBodyMap.get("userId");
                final String password = requestBodyMap.get("password");
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
            } else {
                byte[] body = Files.readAllBytes(new File("./webapp" + requestLine.getRequestResource()).toPath());
                response200Header(dos, body.length);
                responseBody(dos, body);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private String getRequestBody(BufferedReader bufferedReader, Map<String, String> requestHeader) {
        return Optional.ofNullable(requestHeader.get("Content-Length"))
                .filter(contentLength -> !Strings.isNullOrEmpty(contentLength))
                .map(contentLength -> {
                    try {
                        return IOUtils.readData(bufferedReader, Integer.parseInt(contentLength));
                    } catch (IOException e) {
                        return null;
                    }
                })
                .orElse("");
    }

    private Map<String, String> buildRequestParam(RequestLine requestLine) {
        return HttpRequestUtils.parseQueryString(requestLine.getRequestParam());
    }

    private Map<String, String> buildRequestHeader(BufferedReader bufferedReader, String line) throws IOException {
        final Map<String, String> requestHeader = Maps.newHashMap();

        while (!"".equals(line)) {
            line = bufferedReader.readLine();

            Optional.ofNullable(HttpRequestUtils.parseHeader(line))
                    .ifPresent(header -> requestHeader.put(header.getKey(), header.getValue()));

            log.debug(line);
        }
        return requestHeader;
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

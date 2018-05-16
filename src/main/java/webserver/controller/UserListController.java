package webserver.controller;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.HttpRequest;
import webserver.HttpResponse;

import java.util.Optional;
import java.util.stream.Collectors;

public class UserListController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(UserListController.class);

    @Override
    void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
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
    }

    private Boolean isLogin(HttpRequest httpRequest) {
        return Optional.ofNullable(httpRequest.getSession())
                .map(session -> session.getAttribute("user"))
                .isPresent();
    }
}

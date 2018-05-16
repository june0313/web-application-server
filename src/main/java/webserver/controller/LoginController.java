package webserver.controller;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.HttpRequest;
import webserver.HttpResponse;
import webserver.session.HttpSession;

import java.util.Optional;

public class LoginController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        final String userId = httpRequest.getParameter("userId");
        final String password = httpRequest.getParameter("password");
        final User user = DataBase.findUserById(userId);
        final boolean loginSuccess = Optional.ofNullable(user)
                .map(User::getPassword)
                .map(p -> p.equals(password))
                .orElse(Boolean.FALSE);

        if (loginSuccess) {
            log.debug("login success");
            HttpSession session = httpRequest.getSession();
            session.setAttribute("user", user);
            httpResponse.sendRedirect("/index.html");
        } else {
            log.debug("login failed");
            httpResponse.sendRedirect("/user/login_failed.html");
        }
    }
}

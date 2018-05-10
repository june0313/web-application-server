package webserver.controller;

import com.google.common.base.Strings;
import db.DataBase;
import model.User;
import webserver.HttpRequest;
import webserver.HttpResponse;

public class CreateUserController extends AbstractController {
    @Override
    void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        final String userId = httpRequest.getParameter("userId");
        final String password = httpRequest.getParameter("password");
        final String name = httpRequest.getParameter("name");
        final String email = httpRequest.getParameter("email");

        if (!Strings.isNullOrEmpty(userId) && !Strings.isNullOrEmpty(password) && !Strings.isNullOrEmpty(name) && !Strings.isNullOrEmpty(email)) {
            User user = new User(userId, password, name, email);
            DataBase.addUser(user);
        }

        httpResponse.sendRedirect("/index.html");
    }
}

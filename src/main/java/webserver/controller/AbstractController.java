package webserver.controller;

import webserver.HttpRequest;
import webserver.HttpResponse;

public abstract class AbstractController implements Controller {
    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) {
        if (httpRequest.getMethod().isPost()) {
            doPost(httpRequest, httpResponse);
        } else {
            doGet(httpRequest, httpResponse);
        }
    }

    void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
    }

    void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
    }
}

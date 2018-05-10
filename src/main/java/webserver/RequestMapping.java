package webserver;

import com.google.common.collect.Maps;
import webserver.controller.Controller;
import webserver.controller.CreateUserController;
import webserver.controller.LoginController;
import webserver.controller.UserListController;

import java.util.Map;

public class RequestMapping {
    private static final Map<String, Controller> controllerMap;

    static {
        controllerMap = Maps.newHashMap();
        controllerMap.put("/user/create", new CreateUserController());
        controllerMap.put("/user/login", new LoginController());
        controllerMap.put("/user/list", new UserListController());
    }

    public static Controller findController(String requestPath) {
        return controllerMap.get(requestPath);
    }
}

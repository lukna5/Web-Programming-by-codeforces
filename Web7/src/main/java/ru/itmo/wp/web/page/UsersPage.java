package ru.itmo.wp.web.page;

import ru.itmo.wp.model.domain.User;
import ru.itmo.wp.model.exception.ValidationException;
import ru.itmo.wp.model.service.UserService;
import ru.itmo.wp.web.annotation.Json;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/** @noinspection unused*/
public class UsersPage {
    private final UserService userService = new UserService();

    private void action(HttpServletRequest request, Map<String, Object> view) {
        // No operations.
    }

    @Json
    private void findAll(HttpServletRequest request, Map<String, Object> view) {
        view.put("users", userService.findAll());
    }

    @Json
    private void findUser(HttpServletRequest request, Map<String, Object> view) {
        view.put("user",
                userService.find(Long.parseLong(request.getParameter("userId"))));
    }

    @Json
    private void changeAdmin(HttpServletRequest request, Map<String, Object> view) throws ValidationException {
        User user = (User) request.getSession().getAttribute("user");
        userService.validateAdmin(user);
        User changeUser;
        boolean status;
        try {
            changeUser = userService.find(Long.parseLong(request.getParameter("userId")));
            status = Boolean.parseBoolean(request.getParameter("status"));
        } catch (NumberFormatException e) {
            throw new ValidationException("Bad userId");
        }
        userService.changeAdmin(changeUser, !status);
        long id = user.getId();
        request.getSession().removeAttribute("user");
        request.getSession().setAttribute("user", userService.find(id));
    }
}

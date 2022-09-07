package ru.itmo.web.lesson4.util;

import ru.itmo.web.lesson4.model.User;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class DataUtil {
    private static final List<User> USERS = Arrays.asList(
            new User(1, "MikeMirzayanov", "Mike Mirzayanov"),
            new User(6, "pashka", "Pavel Mavrin"),
            new User(9, "geranazarov555", "Georgiy Nazarov"),
            new User(11, "tourist", "Gennady Korotkevich")
    );

    public static void addData(HttpServletRequest request, Map<String, Object> data) {
        data.put("users", USERS);
        System.out.println("data1");
        System.out.println(data.toString());
        for (User user : USERS) {
            if (user.getId() == (request.getParameter("user_id"))) {
                data.put("user", user);
            }
        }
        System.out.println("data");
            System.out.println(data.toString());

    }
}

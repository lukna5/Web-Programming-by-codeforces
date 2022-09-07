package ru.itmo.wp.web;

import freemarker.template.*;
import ru.itmo.wp.web.exception.NotFoundException;
import ru.itmo.wp.web.exception.RedirectException;
import ru.itmo.wp.web.page.IndexPage;
import ru.itmo.wp.web.page.NotFoundPage;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class FrontServlet extends HttpServlet {
    private static final String BASE_PACKAGE = FrontServlet.class.getPackage().getName() + ".page";
    private static final String DEFAULT_ACTION = "action";

    private Configuration sourceConfiguration;
    private Configuration targetConfiguration;

    private Configuration newFreemarkerConfiguration(String templateDirName, boolean debug)
            throws ServletException {
        File templateDir = new File(templateDirName);
        if (!templateDir.isDirectory()) {
            return null;
        }

        Configuration configuration = new Configuration(Configuration.VERSION_2_3_31);
        try {
            configuration.setDirectoryForTemplateLoading(templateDir);
        } catch (IOException e) {
            throw new ServletException("Can't create freemarker configuration [templateDir="
                    + templateDir + "]");
        }
        configuration.setDefaultEncoding(StandardCharsets.UTF_8.name());
        configuration.setTemplateExceptionHandler(debug ? TemplateExceptionHandler.HTML_DEBUG_HANDLER :
                TemplateExceptionHandler.RETHROW_HANDLER);
        configuration.setLogTemplateExceptions(false);
        configuration.setWrapUncheckedExceptions(true);
        configuration.setLocalizedLookup(false); //

        return configuration;
    }

    @Override
    public void init() throws ServletException {
        sourceConfiguration = newFreemarkerConfiguration(
                getServletContext().getRealPath("/") + "../../src/main/webapp/WEB-INF/templates", true);
        targetConfiguration = newFreemarkerConfiguration(
                getServletContext().getRealPath("WEB-INF/templates"), false);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        process(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        process(request, response);
    }

    private void process(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Route route = Route.newRoute(request);
        try {
            process(route, request, response);
        } catch (NotFoundException e) {
            try {
                process(Route.newNotFoundRoute(), request, response);
            } catch (NotFoundException notFoundException) {
                throw new ServletException(notFoundException);
            }
        }
    }

    private void process(Route route, HttpServletRequest request, HttpServletResponse response)
            throws NotFoundException, ServletException, IOException {
        HttpSession session = request.getSession();
        Class<?> pageClass;
        //
        if (request.getParameterMap().containsKey("lang")){
            String lang = request.getParameter("lang");
            if (lang.matches("[a-z]{2}")) {
                session.setAttribute("language", lang);
            }
        }
        try {
            pageClass = Class.forName(route.getClassName());
        } catch (ClassNotFoundException e) {
            throw new NotFoundException();
        }
        //
        ArrayList<Object> allowedActions = new ArrayList<>();
        allowedActions.add(Map.class);
        allowedActions.add(HttpServletRequest.class);
        ArrayList<Method> methodS = new ArrayList<>();
        Method method = null;
        // Проверить, что action метод единственный. Если это не так, то бросать исключение.
        for (Class<?> clazz = pageClass; method == null && clazz != null; clazz = clazz.getSuperclass()) {
            for (Method m : clazz.getDeclaredMethods()) {
                boolean badMethod = false;
                ArrayList<Object> allowedActions1 = new ArrayList<>(allowedActions);
                Object[] params = m.getParameterTypes();
                for (Object param : params) {
                    if (allowedActions1.contains(param)) {
                        allowedActions1.remove(param);
                    } else {
                        badMethod = true;
                        break;
                    }
                }
                if (!badMethod){
                    if (method != null){
                        throw new ServletException("Have more than one suitable methods");
                    }
                    else {
                        method = m;
                    }
                }
            }
        }
        //
        if (method == null) {
            throw new NotFoundException();
        }
        Object page;
        try {
            page = pageClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new ServletException("Can't create page [pageClass="
                    + pageClass + "]");
        }

        Map<String, Object> view = new HashMap<>();
        //
        methodS.add(method);
        try {
            int size = -1;
            Object[] parameters = null;
            for (Method method1: methodS) {
                if (size < method1.getParameterCount()) {
                    Class<?>[] parameterTypes = method1.getParameterTypes();
                    Object[] parameters1 = new Object[parameterTypes.length];
                    for (int i = 0; i < parameterTypes.length; i++) {
                        if (parameterTypes[i] == Map.class) {
                            parameters1[i] = view;
                            continue;
                        }
                        if (parameterTypes[i] == HttpServletRequest.class) {
                            parameters1[i] = request;
                            continue;
                        }

                        throw new ServletException("Expected map or request in action method " + method.getName() + " arguments in page " + pageClass.getSimpleName());
                    }
                    method = method1;
                    parameters = parameters1;
                    size = parameters.length;
                }
            }
            method.setAccessible(true);
            method.invoke(page, parameters);
            //
        } catch (IllegalAccessException e) {
            throw new ServletException("Can't invoke action method [pageClass="
                    + pageClass + ", method=" + method + "]");
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RedirectException) {
                RedirectException redirectException = (RedirectException) cause;
                response.sendRedirect(redirectException.getTarget());
                return;
            } else {
                throw new ServletException("Can't invoke action method [pageClass="
                        + pageClass + ", method=" + method + "]", cause);
            }
        }
        response.setContentType("text/html");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        //
        Template template;
        Object lang = session.getAttribute("language");
        if (lang == null) {
            template = newTemplate(pageClass.getSimpleName() + ".ftlh");
        } else {
            try {
                template = newTemplate(pageClass.getSimpleName() + "_" +
                        lang + ".ftlh");
            } catch (ServletException e){
                template = newTemplate(pageClass.getSimpleName() + ".ftlh");
            }
        }
        //
        try {
            template.process(view, response.getWriter());
        } catch (TemplateException e) {
            if (sourceConfiguration == null) {
                throw new ServletException("Can't render template [pageClass="
                        + pageClass + ", action=" + method + "]", e);
            }
        }
    }

    private Template newTemplate(String templateName) throws ServletException {
        Template template = null;

        if (sourceConfiguration != null) {
            try {
                template = sourceConfiguration.getTemplate(templateName);
            } catch (TemplateNotFoundException ignored) {
                // No operations.
            } catch (IOException e) {
                throw new ServletException("Can't load template [templateName=" + templateName + "]", e);
            }
        }

        if (template == null && targetConfiguration != null) {
            try {
                template = targetConfiguration.getTemplate(templateName);
            } catch (TemplateNotFoundException ignored) {
                // No operations.
            } catch (IOException e) {
                throw new ServletException("Can't load template [templateName=" + templateName + "]", e);
            }
        }

        if (template == null) {
            throw new ServletException("Can't find template [templateName=" + templateName + "]");
        }

        return template;
    }

    private static class Route {
        private final String className;
        private final String action;

        private Route(String className, String action) {
            this.className = className;
            this.action = action;
        }

        private String getClassName() {
            return className;
        }

        private String getAction() {
            return action;
        }

        private static Route newNotFoundRoute() {
            return new Route(NotFoundPage.class.getName(), DEFAULT_ACTION);
        }

        private static Route newIndexRoute() {
            return new Route(IndexPage.class.getName(), DEFAULT_ACTION);
        }

        private static Route newRoute(HttpServletRequest request) {
            String uri = request.getRequestURI();

            List<String> classNameParts = Arrays.stream(uri.split("/"))
                    .filter(part -> !part.isEmpty())
                    .collect(Collectors.toList());

            if (classNameParts.isEmpty()) {
                return newIndexRoute();
            }

            StringBuilder simpleClassName = new StringBuilder(classNameParts.get(classNameParts.size() - 1));
            int lastDotIndex = simpleClassName.lastIndexOf(".");
            simpleClassName.setCharAt(lastDotIndex + 1,
                    Character.toUpperCase(simpleClassName.charAt(lastDotIndex + 1)));
            classNameParts.set(classNameParts.size() - 1, simpleClassName.toString());

            String className = BASE_PACKAGE + "." + String.join(".", classNameParts) + "Page";

            String action = request.getParameter("action");
            if (action == null || action.isEmpty()) {
                action = DEFAULT_ACTION;
            }

            return new Route(className, action);
        }
    }
}
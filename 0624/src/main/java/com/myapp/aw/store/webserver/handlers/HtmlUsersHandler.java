package com.myapp.aw.store.webserver.handlers;
import com.myapp.aw.store.repository.UserRepository;
import com.myapp.aw.store.webserver.SessionManager;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
public class HtmlUsersHandler implements HttpHandler {
    private final UserRepository userRepository;
    private final SessionManager sessionManager;
    public HtmlUsersHandler(UserRepository userRepository, SessionManager sessionManager) {
        this.userRepository = userRepository;
        this.sessionManager = sessionManager;
    }
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!sessionManager.getSessionUser(exchange).isPresent()) { HandlerUtils.redirect(exchange, "/admin-login"); return; }
        try {
            HandlerUtils.sendResponse(exchange, 200, HandlerUtils.convertUserListToHtmlTable(userRepository.findAll()), "text/html");
        } catch (Exception e) { e.printStackTrace(); HandlerUtils.sendResponse(exchange, 500, "<h1>Error</h1>", "text/html"); }
    }
}

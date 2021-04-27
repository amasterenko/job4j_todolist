package ru.job4j.todolist.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * The class redirects unauthorized users to signin.html
 * and passes all users to authorization|registration servlets.
 *
 *@author AndrewMs
 *@version 1.0
 */
public class AuthFilter implements Filter {
    private static final Logger LOG = LoggerFactory.getLogger(AuthFilter.class.getName());

    @Override
    public void doFilter(ServletRequest sreq, ServletResponse sresp, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) sreq;
        HttpServletResponse resp = (HttpServletResponse) sresp;
        String uri = req.getRequestURI();
        if (uri.endsWith("auth") || uri.endsWith("reg") || uri.endsWith("signin.html")
                || uri.contains("img/") || uri.endsWith("func.js")) {
            chain.doFilter(sreq, sresp);
            return;
        }
        if (req.getSession().getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/signin.html");
            return;
        }
        chain.doFilter(sreq, sresp);
    }

    @Override
    public void destroy() {
    }
}
package ru.job4j.todolist.servlets;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.todolist.model.User;
import ru.job4j.todolist.store.HbmStore;
import ru.job4j.todolist.store.Store;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * The class verifies user authorization by serving POST-requests and
 * makes users unauthorized by serving GET-requests.
 * @author AndrewMs
 * @version 1.0
 */
public class AuthServlet extends HttpServlet {
    private static final Store STORE = HbmStore.instOf();
    private static final Logger LOG = LoggerFactory.getLogger(AuthServlet.class.getName());

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        req.setCharacterEncoding("UTF-8");
        try {
            JSONObject jsonReq = new JSONObject(ReqReader.getString(req));
            String password = jsonReq.getString("password");
            User user = STORE.findUserByName(jsonReq.getString("username")).orElse(null);
            String msg = "Authorization failed! Login or password is incorrect.";
            int code = 0;
            if (user != null && password.equals(user.getPassword())) {
                HttpSession sc = req.getSession();
                sc.setAttribute("user", user);
                msg = "";
                code = 1;
            }
            JSONObject jsonResp = new JSONObject();
            jsonResp.put("message", msg);
            jsonResp.put("code", code);
            PrintWriter writer = resp.getWriter();
            writer.print(jsonResp);
            writer.flush();
        } catch (Exception e) {
            LOG.error("Exception occurred: ", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (req.getSession().getAttribute("user") != null) {
            req.getSession().setAttribute("user", null);
            req.getRequestDispatcher("signin.html").forward(req, resp);
        }
    }
}

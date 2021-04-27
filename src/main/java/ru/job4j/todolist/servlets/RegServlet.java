package ru.job4j.todolist.servlets;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.todolist.model.User;
import ru.job4j.todolist.store.HbmStore;
import ru.job4j.todolist.store.Store;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * The class saves a user with specified fields to the store.
 * It checks the user's name for uniqueness and send a response as a json object
 * with code|message fields.
 *
 * @author AndrewMs
 * @version 1.0
 */
public class RegServlet extends HttpServlet {
    private static final Store STORE = HbmStore.instOf();
    private static final Logger LOG = LoggerFactory.getLogger(RegServlet.class.getName());

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        req.setCharacterEncoding("UTF-8");
        try {
            JSONObject jsonReq = new JSONObject(ReqReader.getString(req));
            User user = new User();
            user.setName(jsonReq.getString("username"));
            user.setPassword(jsonReq.getString("password"));

            int code  = 1;
            String msg = "Registration is successful! You can sign in now.";
            if (STORE.add(user).isEmpty()) {
                code = 0;
                msg = "The user with the same name already exists! Please, try another name.";
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
}

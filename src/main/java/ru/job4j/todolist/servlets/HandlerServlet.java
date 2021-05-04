package ru.job4j.todolist.servlets;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.todolist.model.Category;
import ru.job4j.todolist.model.Item;
import ru.job4j.todolist.model.User;
import ru.job4j.todolist.store.HbmStore;
import ru.job4j.todolist.store.Store;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The servlet returns all the items of current logged user as a json array object using doGet.
 * The servlet receives POST-requests with an item data of current logged user as a json object and
 * replies in the same way.
 * It clears user's password when replies on GETs or POSTs.
 * If the item's id in POST request equals 0 then a new item with the same data will be
 * saved in the db in other case the item will be updated.

 *
 * @author AndrewMs
 * @version 1.0
 *
 */

public class HandlerServlet extends HttpServlet {
    private static final Store STORE = HbmStore.instOf();
    private static final Logger LOG = LoggerFactory.getLogger(HandlerServlet.class.getName());

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        req.setCharacterEncoding("UTF-8");
        try {
            HttpSession sc = req.getSession();
            User currentUser = (User) sc.getAttribute("user");
            JSONObject jsonReq = new JSONObject(ReqReader.getString(req));
            Item item = new Item(jsonReq.getString("description"));
            item.setOwner(currentUser);
            List<String> categoryIds = new ArrayList<>();
            jsonReq.getJSONArray("categories").forEach(e -> categoryIds.add(e.toString()));
            Item newItem;
            if (jsonReq.getInt("id") == 0) {
                item.setCreated(new Date(System.currentTimeMillis()));
                newItem = STORE.addItem(item, categoryIds).orElse(item);
                newItem.getOwner().setPassword(null);
                PrintWriter writer = resp.getWriter();
                writer.print(new JSONObject(newItem));
                writer.flush();
            } else {
                item.setId(jsonReq.getInt("id"));
                item.setDone(jsonReq.getBoolean("done"));
                STORE.update(item, categoryIds);
                PrintWriter writer = resp.getWriter();
                writer.print(new JSONObject().put("message", "updated").put("code", 1));
                writer.flush();
            }
                    } catch (Exception e) {
            LOG.error("Exception occurred: ", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        try {
            String path = req.getRequestURI();
            if ("categories".equals(path.substring(path.lastIndexOf('/') + 1))) {
                PrintWriter writer = resp.getWriter();
                writer.print(new JSONArray(STORE.findAllCategories().orElse(List.of())));
                writer.flush();
                return;
            }
            HttpSession sc = req.getSession();
            User currentUser = (User) sc.getAttribute("user");
            List<Item> items = STORE.findAllItemsByUser(currentUser).orElse(List.of());
            items.forEach(i -> i.getOwner().setPassword(null));
            PrintWriter writer = resp.getWriter();
            writer.print(new JSONArray(items));
            writer.flush();
        } catch (Exception e) {
            LOG.error("Exception occurred: ", e);
        }
    }

    @Override
    public void destroy() {
        STORE.close();
    }
}

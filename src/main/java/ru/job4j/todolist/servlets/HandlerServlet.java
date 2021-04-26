package ru.job4j.todolist.servlets;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.todolist.model.Item;
import ru.job4j.todolist.store.HbmStore;
import ru.job4j.todolist.store.Store;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.List;
/**
 * The servlet returns all the items as a json array object using doGet.
 * The servlet receives POST-requests with a item data as a json object and
 * replies as well.
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
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = req.getReader()) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append(System.lineSeparator());
                }
            }
            JSONObject jsonReq = new JSONObject(sb.toString());
            Item item = new Item(
                    jsonReq.getString("description"),
                    new Timestamp(jsonReq.getLong("created"))
            );
            Item newItem;
            if (jsonReq.getInt("id") == 0) {
                newItem = STORE.add(item);
            } else {
                item.setId(jsonReq.getInt("id"));
                item.setDone(jsonReq.getBoolean("done"));
                STORE.update(item);
                newItem = item;
            }
            PrintWriter writer = resp.getWriter();
            writer.print(new JSONObject(newItem));
            writer.flush();
        } catch (Exception e) {
            LOG.error("Exception occurred: ", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        try {
            List<Item> items = STORE.findAll();
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
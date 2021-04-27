package ru.job4j.todolist.servlets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
/**
 * The class has a service method for getting a string from HttpServletRequest.
 * It is useful for further json object parsing.
 *
 * @author AndrewMs
 * @version 1.0
 */
public class ReqReader {
    private static final Logger LOG = LoggerFactory.getLogger(ReqReader.class.getName());

    public static String getString(HttpServletRequest req) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append(System.lineSeparator());
            }
        } catch (IOException e) {
            LOG.error("Exception: ", e);
        }
        return sb.toString();
    }
}

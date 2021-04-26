package ru.job4j.todolist.store;

import ru.job4j.todolist.model.Item;

import java.util.List;
/**
 * The interface represents a store for managing items of TODOList.
 *
 * @author AndrewMs
 * @version 1.0
 */
public interface Store {
    Item add(Item item);
    List<Item> findAll();
    void update(Item item);
    void close();
}

package ru.job4j.todolist.store;

import ru.job4j.todolist.model.Item;
import ru.job4j.todolist.model.User;

import java.util.List;
import java.util.Optional;

/**
 * The interface represents a store for managing items of TODOList.
 *
 * @author AndrewMs
 * @version 1.0
 */
public interface Store {
    Optional<Item> add(Item item);
    Optional<List<Item>> findAll();
    Optional<List<Item>>findAllByUser(User user);
    void update(Item item);
    Optional<User> add(User user);
    Optional<User> findByName(String userName);
    void close();
}

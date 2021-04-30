package ru.job4j.todolist.store;

import ru.job4j.todolist.model.Category;
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
    Optional<Item> addItem(Item item, List<String> categoryIds);
    Optional<List<Item>> findAllItems();
    Optional<List<Category>> findAllCategories();
    Optional<List<Item>> findAllItemsByUser(User user);
    void update(Item item, List<String> categoryIds);
    Optional<User> addUser(User user);
    Optional<User> findUserByName(String userName);
    void close();
}

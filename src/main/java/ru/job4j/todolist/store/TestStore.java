package ru.job4j.todolist.store;

import ru.job4j.todolist.model.Item;
import ru.job4j.todolist.model.User;

import java.sql.Timestamp;
import java.util.List;

public class TestStore {
    public static void main(String[] args) {
        Store store =HbmStore.instOf();
        User user = User.of("user1");
        Item item = new Item("testitem");
        item.setOwner(user);
        /*store.add(item);
        System.out.println(store.findByName("user1").getId());
        System.out.println(store.findByName("user1").getName());*/
        List<Item> items = store.findAllItemsByUser(store.findUserByName("user1").get()).orElse(List.of());
        items.forEach(i -> {
                System.out.println(i.getDescription() + " owner:" + i.getOwner().getName());
                i.getCategories().forEach(System.out::println);
        });

    }
}

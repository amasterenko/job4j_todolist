package ru.job4j.todolist.store;

import ru.job4j.todolist.model.Item;
import ru.job4j.todolist.model.User;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

public class TestStore {
    public static void main(String[] args) {
        Store store =HbmStore.instOf();
        User user = User.of("user1");
        Item item = new Item("testitem", new Timestamp(System.currentTimeMillis()));
        item.setOwner(user);
        /*store.add(item);
        System.out.println(store.findByName("user1").getId());
        System.out.println(store.findByName("user1").getName());*/
        List<Item> items = store.findAll().orElse(List.of());
        items.forEach(i -> {
            if (i.getOwner() != null) {
                System.out.println(i.getDescription() + " owner:" + i.getOwner().getName());
            } else {
                System.out.println(i.getDescription() + " -no owner");
            }
        });
    }
}

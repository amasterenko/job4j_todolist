package ru.job4j.todolist.store;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import ru.job4j.todolist.model.Item;

import java.util.List;

/**
 * The class implements Store using Hibernate framework.
 *
 * @author AndrewMs
 * @version 1.0
 */

public class HbmStore implements Store {
    private final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
            .configure().build();
    private final SessionFactory sf = new MetadataSources(registry)
            .buildMetadata().buildSessionFactory();

    public static Store instOf() {
        return Lazy.INST;
    }

    private static final class Lazy {
        private final static Store INST = new HbmStore();
    }

    @Override
    public Item add(Item item) {
        try(Session session = sf.openSession()) {
            session.beginTransaction();
            session.save(item);
            session.getTransaction().commit();
        }
        return item;
    }

    @Override
    public List<Item> findAll() {
        List<Item> result = List.of();
        try (Session session = sf.openSession()) {
            session.beginTransaction();
            result = session.createQuery("from ru.job4j.todolist.model.Item").list();
            session.getTransaction().commit();
        }
        return result;
    }

    @Override
    public void update(Item item) {
        try(Session session = sf.openSession()) {
            session.beginTransaction();
            session.update(item);
            session.getTransaction().commit();
        }
    }

    @Override
    public void close() {
        StandardServiceRegistryBuilder.destroy(registry);
    }
}

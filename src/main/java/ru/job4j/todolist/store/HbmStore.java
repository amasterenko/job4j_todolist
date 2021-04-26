package ru.job4j.todolist.store;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import ru.job4j.todolist.model.Item;

import java.util.List;
import java.util.function.Function;

/**
 * The class implements Store using Hibernate framework.
 *
 * @author AndrewMs
 * @version 1.0
 */

public class HbmStore implements Store {
    private final StandardServiceRegistry REGISTRY = new StandardServiceRegistryBuilder()
            .configure().build();
    private final SessionFactory SF = new MetadataSources(REGISTRY)
            .buildMetadata().buildSessionFactory();

    public static Store instOf() {
        return Lazy.INST;
    }

    private static final class Lazy {
        private final static Store INST = new HbmStore();
    }

    private <T> T tx(final Function<Session, T> command) {
        final Session session = SF.openSession();
        final Transaction tx = session.beginTransaction();
        try {
            T rsl = command.apply(session);
            tx.commit();
            return rsl;
        } catch (final Exception e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    @Override
    public Item add(Item item) {
        return this.tx(session -> {
            session.save(item);
            return item;
        });
    }

    @Override
    public List<Item> findAll() {
        return this.tx(session -> session.createQuery("from Item").list());
    }

    @Override
    public void update(Item item) {
        this.tx(session -> {
            session.update(item);
            return item;
        });
    }

    @Override
    public void close() {
        StandardServiceRegistryBuilder.destroy(REGISTRY);
    }
}

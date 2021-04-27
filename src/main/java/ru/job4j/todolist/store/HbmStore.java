package ru.job4j.todolist.store;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.todolist.model.Item;
import ru.job4j.todolist.model.User;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * The class implements Store using Hibernate framework.
 * Method "tx" based on "wrapper" pattern.
 * It is used for simplifying the overridden methods' code.
 *
 * @author AndrewMs
 * @version 1.1
 */

public class HbmStore implements Store {
    private final StandardServiceRegistry REGISTRY = new StandardServiceRegistryBuilder()
            .configure().build();
    private final SessionFactory SF = new MetadataSources(REGISTRY)
            .buildMetadata().buildSessionFactory();
    private static final Logger LOG = LoggerFactory.getLogger(HbmStore.class.getName());

    public static Store instOf() {
        return Lazy.INST;
    }

    private static final class Lazy {
        private final static Store INST = new HbmStore();
    }

    private <T> Optional<T> txFunc(final Function<Session, T> command) {
        final Session session = SF.openSession();
        final Transaction tx = session.beginTransaction();
        try {
            T rsl = command.apply(session);
            tx.commit();
            return Optional.of(rsl);
        } catch (final Exception e) {
            session.getTransaction().rollback();
            LOG.error("Exception: ", e);
        } finally {
            session.close();
        }
        return Optional.empty();
    }

    private void txCons(final Consumer<Session> command) {
        final Session session = SF.openSession();
        final Transaction tx = session.beginTransaction();
        try {
            command.accept(session);
            tx.commit();
        } catch (final Exception e) {
            session.getTransaction().rollback();
            LOG.error("Exception: ", e);
            throw new IllegalStateException(e);
        } finally {
            session.close();
        }
    }

    @Override
    public Optional<Item> add(Item item) {
        return this.txFunc(session -> {
            session.save(item);
            return item;
        });
    }

    @Override
    public Optional<List<Item>> findAll() {
        return this.txFunc(session -> session.createQuery("from Item").list());
    }

    @Override
    public Optional<List<Item>> findAllByUser(User user) {
        return this.txFunc(session -> session.createQuery("from Item where owner = :paramUser")
                                            .setParameter("paramUser", user).list());
    }

    @Override
    public void update(Item item) {
        this.txCons(session -> session.update(item));
    }

    @Override
    public Optional<User> add(User user) {
        return this.txFunc(session -> {
            session.save(user);
            return user;
        });
    }

    @Override
    public Optional<User> findByName(String userName) {
        return this.txFunc(
                session -> (User) session.createQuery("from User where name =: paramName")
                                    .setParameter("paramName", userName)
                                    .getSingleResult()
        );
    }

    @Override
    public void close() {
        StandardServiceRegistryBuilder.destroy(REGISTRY);
    }
}

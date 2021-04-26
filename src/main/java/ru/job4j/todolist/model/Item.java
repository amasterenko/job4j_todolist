package ru.job4j.todolist.model;

import net.bytebuddy.dynamic.loading.InjectionClassLoader;

import javax.persistence.*;
import java.sql.Timestamp;
/**
 * The class represents a task in TODOList
 * @author AndrewMs
 * @version 1.0
 */

@Entity
@Table(name="items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String description;
    private Timestamp created;
    private boolean done;

    public Item(String description, Timestamp created) {
        this.description = description;
        this.created = created;
    }

    public Item() {
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public Timestamp getCreated() {
        return created;
    }

    public boolean isDone() {
        return done;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}

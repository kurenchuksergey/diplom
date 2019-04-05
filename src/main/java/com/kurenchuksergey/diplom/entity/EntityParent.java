package com.kurenchuksergey.diplom.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kurenchuksergey.diplom.config.EntityListener;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.io.Serializable;

@EntityListeners(EntityListener.class)
@MappedSuperclass
public abstract class EntityParent implements Serializable {
    @Column
    @JsonIgnore
    private DateTime createOn;
    @Column
    @JsonIgnore
    private DateTime updateOn;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DateTime getCreateOn() {
        return createOn;
    }

    public void setCreateOn(DateTime createOn) {
        this.createOn = createOn;
    }

    public DateTime getUpdateOn() {
        return updateOn;
    }

    public void setUpdateOn(DateTime updateOn) {
        this.updateOn = updateOn;
    }
}

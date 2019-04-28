package com.kurenchuksergey.diplom.config;

import com.kurenchuksergey.diplom.entity.EntityParent;
import org.joda.time.DateTime;

import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

public class EntityListener {

    @PreUpdate
    public void postUpdate(EntityParent o) {
        o.setUpdateOn(DateTime.now());
        setStartTime(o);
        System.out.println("postUpdate" + o.toString());
    }

    @PostLoad
    public void postLoad(EntityParent o) {
        System.out.println("postLoad" + o.toString());
    }

    @PrePersist
    public void setStartTime(EntityParent o) {
        if (o.getCreateOn() == null) {
            o.setCreateOn(DateTime.now());
        }
    }

}

package com.kurenchuksergey.diplom.config;

import com.kurenchuksergey.diplom.entity.EntityParent;
import org.joda.time.DateTime;

import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;

public class EntityListener {

    @PostUpdate
    public void postUpdate(EntityParent o){
        o.setUpdateOn(DateTime.now());
        System.out.println("postUpdate" + o.toString());
    }
    @PostLoad
    public void postLoad(EntityParent o){
        System.out.println("postLoad" + o.toString());
    }
    @PostPersist
    public void postPersist(EntityParent o){
        if(o.getCreateOn() != null){
            o.setCreateOn(DateTime.now());
        }
    }
}

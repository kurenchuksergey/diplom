package com.kurenchuksergey.diplom.dto;

import com.kurenchuksergey.diplom.entity.Task;
import com.kurenchuksergey.diplom.entity.TaskType;
import org.joda.time.DateTime;

import java.io.Serializable;

public class TaskDTO implements Serializable {

    private Long id;
    private TaskType type;
    private String imageContentType;
    private DateTime createOn;
    private DateTime updateOn;
    private long userId;
    private String filename;

    public TaskDTO(Task task) {
        this.id = task.getId();
        this.type = task.getType();
        this.imageContentType = task.getImageContentType();
        this.createOn = task.getCreateOn();
        this.updateOn = task.getUpdateOn();
        this.userId = task.getUserId();
        this.filename = task.getFileName();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TaskType getType() {
        return type;
    }

    public void setType(TaskType type) {
        this.type = type;
    }

    public String getImageContentType() {
        return imageContentType;
    }

    public DateTime getCreateOn() {
        return createOn;
    }

    public DateTime getUpdateOn() {
        return updateOn;
    }

    public long getUserId() {
        return userId;
    }

    public Task createTaskInst() {
        Task task = new Task();
        task.setUserId(userId);
        task.setFileName(filename);
        task.setType(type);
        task.setImageContentType(imageContentType);
        task.setId(id);
        task.setCreateOn(createOn);
        task.setUpdateOn(updateOn);
        return task;
    }
}


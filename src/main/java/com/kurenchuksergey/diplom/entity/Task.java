package com.kurenchuksergey.diplom.entity;

import javax.persistence.*;

@Entity
public class Task extends EntityParent {

    @Column
    private String fileName;

    @Column
    @Enumerated(EnumType.STRING)
    private TaskType type;

    @Column
    @Enumerated(EnumType.STRING)
    private TaskState state = TaskState.WAIT;

    @Column(name = "image")
    private byte[] image;

    @Column(name = "prev_image")
    private byte[] prevImage;

    @Column(name = "image_content_type")
    private String imageContentType;

    @Transient
    public static final int widthPrev = 240;

    @Transient
    public static final int heightPrev = 240;

    @Column(name = "user_id")
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private UserIdent user;

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public TaskState getState() {
        return state;
    }

    public void setState(TaskState state) {
        this.state = state;
    }

//    public String getPath() {
//        return path;
//    }
//
//    public void setPath(String path) {
//        this.path = path;
//    }

    public String getImageContentType() {
        return imageContentType;
    }

    public void setImageContentType(String imageContentType) {
        this.imageContentType = imageContentType;
    }

    public int getWidthPrev() {
        return widthPrev;
    }

    public int getHeightPrev() {
        return heightPrev;
    }

    public byte[] getPrevImage() {
        return prevImage;
    }

    public void setPrevImage(byte[] prevImage) {
        this.prevImage = prevImage;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public UserIdent getUser() {
        return user;
    }

    public void setUser(UserIdent user) {
        this.user = user;
    }

    public TaskType getType() {
        return type;
    }

    public void setType(TaskType type) {
        this.type = type;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Task() {
    }

    @Override
    public String toString() {
        return "id:" + super.getId() + " state" + state.toString() + " name" + fileName;
    }
}


package com.example.doan_shopsmartphone.model;

public class ReviewShowModel {
    String Name;
    String Avatar;
    String Comment;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getAvatar() {
        return Avatar;
    }

    public void setAvatar(String avatar) {
        Avatar = avatar;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public ReviewShowModel(String name, String avatar, String comment) {
        Name = name;
        Avatar = avatar;
        Comment = comment;
    }
}

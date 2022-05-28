package com.moutamid.jiji.model;

public class ChatModel {

    private String name, lastMcg, uid;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public ChatModel(String name, String lastMcg, String uid) {
        this.name = name;
        this.lastMcg = lastMcg;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastMcg() {
        return lastMcg;
    }

    public void setLastMcg(String lastMcg) {
        this.lastMcg = lastMcg;
    }

    public ChatModel(String name, String lastMcg) {
        this.name = name;
        this.lastMcg = lastMcg;
    }

    ChatModel() {
    }
}
package com.example.administrator.notebook_1;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/7/20.
 */

public class Event implements Serializable {
    private String time;
    private String priority;
    private int complete;
    private String title;
    private String content;

    public Event(String time, String priority, int complete, String title, String content) {
        this.time = time;
        this.priority = priority;
        this.complete = complete;
        this.title = title;
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public String getPriority() {
        return priority;
    }

    public int getComplete() {
        return complete;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
}

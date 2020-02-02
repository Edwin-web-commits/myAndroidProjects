package com.example.mytodoapp;

public class Activities {

    private String name;
        private String time;

    Activities(){}
    public Activities(String name,String time)
    {
        this.name=name;
        this.time=time;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }
}

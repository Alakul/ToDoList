package com.example.projektzaliczeniowy;

public class ToDoTable {

    private int id, checked, priority;
    private String task, date, time;


    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id=id;
    }

    public String getTask()
    {
        return  task;
    }

    public void setTask(String task)
    {
        this.task=task;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date=date;
    }

    public String getTime()
    {
        return  time;
    }

    public void setTime(String time)
    {
        this.time=time;
    }

    public int getChecked()
    {
        return  checked;
    }

    public void setChecked(int checked)
    {
        this.checked=checked;
    }

    public int getPriority()
    {
        return  priority;
    }

    public void setPriority(int priority)
    {
        this.priority=priority;
    }
}

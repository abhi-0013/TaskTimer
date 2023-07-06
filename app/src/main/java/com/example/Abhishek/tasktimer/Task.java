package com.example.Abhishek.tasktimer;

import java.io.Serializable;

class Task implements Serializable {
    public static final long serialVersionUID = 20230224L;

    private long m_ID;
    private  final String m_name;
    private final String m_Description;
    private final int m_SortOrder;

    public Task(long id, String name, String Description, int sortOrder) {
        this.m_ID = id;
        m_name = name;
        m_Description = Description;
        m_SortOrder = sortOrder;
    }

    public long getId() {
        return m_ID;
    }

    public String getName() {
        return m_name;
    }

    public String getDescription() {
        return m_Description;
    }

    public int getSortOrder() {
        return m_SortOrder;
    }

    public void setId(long id) {
        this.m_ID = id;
    }

}

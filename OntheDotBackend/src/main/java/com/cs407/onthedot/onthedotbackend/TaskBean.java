package com.cs407.onthedot.onthedotbackend;


import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/** The object model for the data we are sending through endpoints */
@Entity
public class TaskBean {
    @Id
    private Long id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    private String myData;
    public String getData() { return myData; }
    public void setData(String data) { myData = data; }
}
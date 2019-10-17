package net.andreinc.elasticpoc.query.controller.dto;

public class VisitedReq {
    private String name;
    private String city;

    public VisitedReq() {}

    public VisitedReq(String name, String city) {
        this.name = name;
        this.city = city;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}

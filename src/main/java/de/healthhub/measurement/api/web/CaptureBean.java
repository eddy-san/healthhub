package de.healthhub.measurement.api.web;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

@Named
@RequestScoped
public class CaptureBean {
    private Integer steps;
    private String message;

    public void save() {
        message = "OK (Dummy). Steps=" + steps;
    }

    public Integer getSteps() { return steps; }
    public void setSteps(Integer steps) { this.steps = steps; }

    public String getMessage() { return message; }
}
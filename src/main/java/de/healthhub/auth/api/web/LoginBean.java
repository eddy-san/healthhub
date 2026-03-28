package de.healthhub.auth.api.web;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

@Named
@RequestScoped
public class LoginBean {

    public String login() {
        return "/admin/dashboard.xhtml?faces-redirect=true";
    }
}
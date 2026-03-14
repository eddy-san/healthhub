package de.healthhub.controller.web.security;

import de.healthhub.model.service.AuthenticationService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named
@RequestScoped
public class LoginBean {

    private String username;
    private String password;
    private Integer captchaAnswer;

    @Inject
    private AuthenticationService authenticationService;

    @Inject
    private LoginCaptchaBean loginCaptchaBean;

    public String login() {

        if (!loginCaptchaBean.isValid(captchaAnswer)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Login failed",
                            "Captcha answer is incorrect."));
            loginCaptchaBean.refresh();
            captchaAnswer = null;
            return null;
        }

        boolean success = authenticationService.login(username, password);

        if (!success) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Login failed",
                            "Username or password is incorrect."));
            loginCaptchaBean.refresh();
            captchaAnswer = null;
            return null;
        }

        loginCaptchaBean.refresh();
        captchaAnswer = null;

        return "/app/home.xhtml?faces-redirect=true";
    }

    public String logout() {
        authenticationService.logout();
        return "/index.xhtml?faces-redirect=true";
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public Integer getCaptchaAnswer() {
        return captchaAnswer;
    }

    public void setCaptchaAnswer(Integer captchaAnswer) {
        this.captchaAnswer = captchaAnswer;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
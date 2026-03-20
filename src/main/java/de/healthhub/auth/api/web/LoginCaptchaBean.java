package de.healthhub.auth.api.web;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

import java.io.Serializable;
import java.security.SecureRandom;

@Named
@SessionScoped
public class LoginCaptchaBean implements Serializable {

    private final SecureRandom random = new SecureRandom();

    private int left;
    private int right;
    private int expectedResult;

    public LoginCaptchaBean() {
        refresh();
    }

    public void refresh() {
        this.left = random.nextInt(10) + 1;   // 1..10
        this.right = random.nextInt(10) + 1;  // 1..10
        this.expectedResult = left + right;
    }

    public boolean isValid(Integer answer) {
        return answer != null && answer == expectedResult;
    }

    public String getQuestion() {
        return "What is " + left + " + " + right + "?";
    }
}
package com.jihun.myshop.domain.user.controller.view;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthViewController {

    @GetMapping("/signup")
    public String signup() {
        return "domain/user/signup";
    }

    @GetMapping("/login")
    public String login() {
        return "domain/user/login";
    }

    @GetMapping("/register-success")
    public String registerSuccess() {
        return "domain/user/register-success";
    }
}

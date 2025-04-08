package com.jihun.myshop.domain.user.controller;

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
}

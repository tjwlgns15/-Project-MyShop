package com.jihun.myshop.admin.controller.view;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminViewController {

    @GetMapping("/index")
    public String adminView() {
        return "admin/admin-index";
    }
}


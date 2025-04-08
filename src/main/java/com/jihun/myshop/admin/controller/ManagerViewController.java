package com.jihun.myshop.admin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/manager")
public class ManagerViewController {

    @GetMapping("/index")
    public String index() {
        return "admin/manager-index";
    }
}

package com.cristian.shop.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
public class Demo {

    @GetMapping("/user/demo")
    public String demo() {
        return "user";
    }

    @GetMapping("/admin/demo")
    public String admin() {
        return "admin";
    }

}

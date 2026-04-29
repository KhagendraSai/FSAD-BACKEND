package com.fsad.backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RootRedirectController {

    @GetMapping("/")
    public String redirectToSwagger() {
        // This matches the springdoc.swagger-ui.path from your properties
        return "redirect:/swagger-ui.html";
    }
}
package com.gabojait.gabojaitspring.develop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/docs")
public class DocumentController {

    @GetMapping("/privacy")
    public ModelAndView privacyPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("privacy.html");
        return modelAndView;
    }

    @GetMapping("/service")
    public ModelAndView servicePage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("service.html");
        return modelAndView;
    }
}

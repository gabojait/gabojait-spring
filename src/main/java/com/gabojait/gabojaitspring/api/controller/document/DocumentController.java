package com.gabojait.gabojaitspring.api.controller.document;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/docs")
public class DocumentController {

    @GetMapping("/privacy.html")
    public ModelAndView privacy() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("privacy");
        return modelAndView;
    }

    @GetMapping("/service.html")
    public ModelAndView service() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("service");
        return modelAndView;
    }
}

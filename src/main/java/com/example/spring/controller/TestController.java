package com.example.spring.controller;

import com.example.spring.service.TestService;
import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Log4j2
@Controller
@RequestMapping("/test")
public class TestController {
    @Autowired
    TestService testService;

    @GetMapping("/input")
    public String getTest() {
        return "test";
    }

    @PostMapping("/extract")
    public String extractFile(@RequestParam("input1") String file1, @RequestParam("input2") String file2) {
//        testService.saveSpare();
        testService.test0(file1, file2);

        return "redirect:/test/input";
    }
}

package com.jdsbbmq.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
//@CrossOrigin
public class TestController {

    @RequestMapping("/test")
    @ResponseBody
    public String test() {
        return "hello ssm";
    }
}


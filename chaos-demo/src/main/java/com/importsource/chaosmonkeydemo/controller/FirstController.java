package com.importsource.chaosmonkeydemo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hezhuofan
 */
@RestController
public class FirstController {

    @GetMapping("hello")
    public String hello(){
        return "haha";
    }
}

package com.capstone.springbootblogpostbackend;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/")
public class HelloWorld {

    @GetMapping("")
    public String sayHello() {
        return "Hello, World! Ismail ";
    }
}

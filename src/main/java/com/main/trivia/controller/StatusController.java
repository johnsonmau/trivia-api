package com.main.trivia.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.Date;

@RestController()
@RequestMapping("/v1/status")
public class StatusController {

    @GetMapping
    public String status() {
        return "Trivia API is running. " + new Timestamp(System.currentTimeMillis());
    }
}

package com.ssafy.fw_05_mvc.service;

import org.springframework.stereotype.Service;

@Service
public class SimpleService {

    public String echo(String data) {
        return "echo: " + data;
    }

    public int add(int a, int b) {
        return a + b;
    }
}

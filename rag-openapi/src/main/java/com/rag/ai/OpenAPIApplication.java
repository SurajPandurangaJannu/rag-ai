package com.rag.ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.shell.command.annotation.CommandScan;

@CommandScan
@SpringBootApplication
public class OpenAPIApplication {
    public static void main(String[] args) {
        SpringApplication.run(OpenAPIApplication.class, args);
    }
}

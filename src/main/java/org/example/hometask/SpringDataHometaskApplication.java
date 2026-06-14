package org.example.hometask;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;

@SpringBootApplication
public class SpringDataHometaskApplication {

    public static void main(String[] args) {
        System.setProperty("liquibase.secureParsing", "false");
        System.setProperty("liquibase.validateXmlChangelogFiles", "false");
        SpringApplication.run(SpringDataHometaskApplication.class, args);
    }

}

package com.sba302.electroshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ConfigurationPropertiesScan
public class ElectroshopApplication {

    public static void main(String[] args) {
        SpringApplication.run(ElectroshopApplication.class, args);
    }

}

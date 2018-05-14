package com.sc.fm.com.cissvcauthentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication
public class CisSvcAuthenticationApplication extends SpringBootServletInitializer {
    private static final Logger log = LoggerFactory.getLogger(CisSvcAuthenticationApplication.class);


    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        log.trace("Overriding SpringApplicationBuilder");
        return application.sources(CisSvcAuthenticationApplication.class);
    }

    public static void main(String[] args) {
        log.trace(" Spring Boot main method entry point");
        SpringApplication.run(CisSvcAuthenticationApplication.class, args);
    }

}


package com.kurenchuksergey.diplom;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheck {

//    @GetMapping("health")
    public HttpStatus checkHealth(){
        System.out.println("check");
        return HttpStatus.ACCEPTED;
    }
}

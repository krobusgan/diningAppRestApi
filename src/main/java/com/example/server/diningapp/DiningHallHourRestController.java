package com.example.server.diningapp;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DiningHallHourRestController {

    private final DiningHallHourRepository repository;

    DiningHallHourRestController(DiningHallHourRepository repository) {
        this.repository = repository;
    }

    // Aggregate root
    // tag::get-aggregate-root[]
    @GetMapping("/DiningHallHours")
    List<DiningHallHour> all() {
        return repository.findAll();
    }
}

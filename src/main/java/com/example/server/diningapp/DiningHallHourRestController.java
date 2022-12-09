package com.example.server.diningapp;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.server.diningapp.VTDiningScrapingUtils.numberOfNextDays;
import static com.example.server.diningapp.VTDiningScrapingUtils.scrapingVTDiningHours;

@RestController
public class DiningHallHourRestController {

    private final DiningHallHourRepository repository;

    DiningHallHourRestController(DiningHallHourRepository repository) {
        this.repository = repository;
    }

    // Aggregate root
    // tag::get-aggregate-root[]
    @GetMapping("/DiningHallHours")
    List<DiningHallHour> all() throws IOException, InterruptedException {
        List<DiningHallHour> list = repository.findAll();
        list.sort(Comparator.comparing(DiningHallHour::getDate).reversed());

        DiningHallHour diningHallHour = list.get(0);

        LocalDate latestDate = LocalDate.parse(diningHallHour.getDate(), DateTimeFormatter.ISO_DATE);

        if (LocalDate.now().plusDays(numberOfNextDays).isAfter(latestDate)) {
            List<DiningHallHour> diningHallHours = scrapingVTDiningHours(numberOfNextDays);
            diningHallHours = diningHallHours.stream()
                    .filter(diningHallHour1 -> LocalDate.parse(diningHallHour.getDate(), DateTimeFormatter.ISO_DATE).isAfter(latestDate)
            )
            .collect(Collectors.toList());

            repository.saveAll(diningHallHours);
        }

        return repository.findAll();
    }
}

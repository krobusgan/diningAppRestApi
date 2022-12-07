package com.example.server.diningapp;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static com.example.server.diningapp.VTDiningScrapingUtils.*;

public class ScrapingTest {
    @Test
    public void testScrapingWorks() throws IOException, InterruptedException {
        List<DiningHallHour> diningHallHourList = scrapingVTDiningHours();
        printHoursToJsonFile(diningHallHourList);
    }
}

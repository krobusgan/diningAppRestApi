package com.example.server.diningapp;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.example.server.diningapp.VTDiningScrapingUtils.scrapingVTDiningHours;

public class ScrapingTest {
    @Test
    public void userCanSearch() throws IOException {
        scrapingVTDiningHours();
    }
}

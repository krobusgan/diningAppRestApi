package com.example.server.diningapp;

import antlr.StringUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.example.server.diningapp.VTDiningScrapingUtils.*;

@Configuration
public class LoadDatabase {

    public static final String[] DINING_MENU_DISH_HEADER = { "Food Item Name", "Label", "Description", "Amount", "Type", "Other Info", "Dinning Hall"};


    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);
    ObjectMapper mapper = new ObjectMapper();

    @Bean
    CommandLineRunner initDatabase(FoodItemRepository foodItemRepository, DiningHallHourRepository diningHallHourRepository) {

        return args -> {
            String menuJsonString   = loadJSONFromAsset("menu.json");
            List<FoodItem> foodItems = scrapingVTDiningMenu();
            List<DiningHallHour> diningHallHours = scrapingVTDiningHours();
            List<FoodItem> menuList = mapper.readValue(menuJsonString, new TypeReference<>() {});
            // log.info("Preloading menu list...");
            foodItemRepository.saveAll(menuList);
            List<FoodItem> foodItemList = foodItemRepository.findAll();
            // log.info("Existing items " + foodItemList.size());

            String hourJsonString = loadJSONFromAsset("hours.json");
            List<DiningHallHour> hourList               = mapper.readValue(hourJsonString, new TypeReference<>() {});
            log.info("Preloading hour list...");
            diningHallHourRepository.saveAll(diningHallHours);
            hourList = diningHallHourRepository.findAll();
            log.info("Existing items for hours " + diningHallHours.size());
        };
    }

}
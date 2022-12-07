package com.example.server.diningapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static com.example.server.diningapp.VTDiningScrapingUtils.scrapingVTDiningHours;
import static com.example.server.diningapp.VTDiningScrapingUtils.scrapingVTDiningMenu;

@Configuration
public class LoadDatabase {
    public static final String[] DINING_MENU_DISH_HEADER = { "Food Item Name", "Label", "Description", "Amount", "Type", "Other Info", "Dinning Hall"};

    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);
    ObjectMapper mapper = new ObjectMapper();

    @Bean
    CommandLineRunner initDatabase(FoodItemRepository foodItemRepository, DiningHallHourRepository diningHallHourRepository) {
        return args -> {

            List<FoodItem> foodItems = scrapingVTDiningMenu();
            List<DiningHallHour> diningHallHours = scrapingVTDiningHours();

            // String menuJsonString   = loadJSONFromAsset("menu.json");
            // List<FoodItem> foodItems = mapper.readValue(menuJsonString, new TypeReference<>() {});
            // String hourJsonString = loadJSONFromAsset("hours.json");
            // List<DiningHallHour> hourList               = mapper.readValue(hourJsonString, new TypeReference<>() {});
            log.info("Preloading menu list...");
            foodItemRepository.saveAll(foodItems);
            List<FoodItem> foodItemList = foodItemRepository.findAll();
            log.info("Existing food items " + foodItemList.size());


            log.info("Preloading hour list...");
            diningHallHourRepository.saveAll(diningHallHours);
            // List<DiningHallHour> hourList = diningHallHourRepository.findAll();
            //    log.info("Existing items for hours " + diningHallHours.size());
        };
    }

}
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

@Configuration
public class LoadDatabase {
    public static final String[] DINING_HOURS_HEADER     = { "Dining Hall", "Date", "Hours"};
    public static final String[] DINING_MENU_DISH_HEADER = { "Food Item Name", "Label", "Description", "Amount", "Type", "Other Info", "Dinning Hall"};
    public static final String VT_MENU_URL               = "https://foodpro.dsa.vt.edu/menus/";
    public static final String VT_HOUR_URL               = "https://saapps.students.vt.edu/hours/";

    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);
    ObjectMapper mapper = new ObjectMapper();

    @Bean
    CommandLineRunner initDatabase(FoodItemRepository foodItemRepository, DiningHallHourRepository diningHallHourRepository) {

        return args -> {
            String menuJsonString   = loadJSONFromAsset("menu.json");
            List<FoodItem> menuList = mapper.readValue(menuJsonString, new TypeReference<>() {});
            log.info("Preloading menu list...");
            foodItemRepository.saveAll(menuList);
            List<FoodItem> foodItemList = foodItemRepository.findAll();
            log.info("Existing items " + foodItemList.size());

            String hourJsonString = loadJSONFromAsset("hours.json");
            List<DiningHallHour> hourList               = mapper.readValue(hourJsonString, new TypeReference<>() {});
            log.info("Preloading hour list...");
            diningHallHourRepository.saveAll(hourList);
            hourList = diningHallHourRepository.findAll();
            log.info("Existing items for hours " + hourList.size());
        };
    }
    public static String loadJSONFromAsset(String fileName) {
        String json = "";
        try {
            Resource resource = new ClassPathResource(fileName);
            InputStream is = resource.getInputStream();
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    /**
     * Connect to VT menu website and parsed important information
     * @return A list of food item display in the website
     * @throws IOException
     */
    public static List<FoodItem> scrapingVTDiningMenu() throws IOException {
        Document doc = Jsoup.connect(VT_MENU_URL).get();
        List<Element> diningHallList = doc.getElementsByClass("dining_menu_button");

        List<FoodItem> records = new ArrayList<>();
        for (Element diningHall : diningHallList) {
            // Getting the specific dining hall link for the menu of that dining hall
            String link = diningHall.getElementsByTag("a").attr("href");
            Document diningHallMenus = Jsoup.connect(VT_MENU_URL + link).get();

            List<Element> cardList = diningHallMenus.getElementsByClass("card");
            for (Element card : cardList) {
                List<Element> recipeContainerList = card.getElementsByClass("recipe_container");
                String cardHeader =
                        card.getElementsByClass("card-header").size() > 0
                                ? card.getElementsByClass("card-header").get(0).text()
                                : "";

                Element pane = card.parent();
                Element tab = diningHallMenus.getElementById(pane.attr("aria-labelledby"));

                for (Element element1 : recipeContainerList) {
                    FoodItem foodItem = FoodItem.FoodItemBuilder.aFoodItem()
                            .name(element1.getElementsByClass("recipe_title").size() > 0
                                    ? element1.getElementsByClass("recipe_title").get(0).text()
                                    : "")
                            .label(element1.getElementsByClass("legend_icon").size() > 0
                                    ? element1.getElementsByClass("legend_icon").get(0).text()
                                    : "")
                            .description(element1.getElementsByClass("recipe_description").size() > 0
                                    ? element1.getElementsByClass("recipe_description").get(0).text()
                                    : "")
                            .amount(element1.getElementsByClass("portion_size").size() > 0
                                    ? element1.getElementsByClass("portion_size").get(0).text()
                                    : "")
                            .type(tab.text())
                            .diningHall(diningHall.text())
                            .otherInfo(cardHeader)
                            .build();

                    records.add(foodItem);
                }
            }

        }

        return records;
    }

    public static void printToCsvFile(String fileName, String[] header, List<List<String>> records) throws IOException{
        FileWriter out = new FileWriter(fileName + ".csv", true);
        CSVFormat csvFormat = header.length > 0 ? CSVFormat.DEFAULT.withHeader(DINING_MENU_DISH_HEADER) : CSVFormat.DEFAULT;
        try (CSVPrinter printer = new CSVPrinter(out, csvFormat)) {
            for (List<String> record : records) {
                printer.printRecord(record);
            }
        }
    }


//    public static void printToJsonFile(String type) throws IOException{
//        if (StringUtils.equals(type, "hours")) {
//            List<DiningHallHour> list = readingHourFromCsvFile("hours", DINING_HOURS_HEADER);
//            ObjectMapper mapper = new ObjectMapper();
//            mapper.writeValue(new File("hours.json"), list);
//        }
//        else if (StringUtils.equals(type, "menu")) {
//            List<FoodItem> list = readingMenuFromCsvFile("menu", DINING_MENU_DISH_HEADER);
//            ObjectMapper mapper = new ObjectMapper();
//            mapper.writeValue(new File("menu.json"), list);
//        }
//    }
}
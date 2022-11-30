package com.example.server.diningapp;

import com.codeborne.selenide.Configuration;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static com.example.server.diningapp.LoadDatabase.DINING_MENU_DISH_HEADER;

public class VTDiningScrapingUtils {
    public static final String VT_MENU_URL               = "https://foodpro.dsa.vt.edu/menus/";
    public static final String VT_HOUR_URL               = "https://saapps.students.vt.edu/hours/";
    public static final String[] DINING_HOURS_HEADER     = { "Dining Hall", "Date", "Hours"};
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


    public static void printToJsonFile(String type) throws IOException{
        if (Objects.equals(type, "hours")) {
            List<DiningHallHour> list = readingHourFromCsvFile("hours", DINING_HOURS_HEADER);
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(new File("hours.json"), list);
        }
        else if (Objects.equals(type, "menu")) {
            List<FoodItem> list = readingMenuFromCsvFile("menu", DINING_MENU_DISH_HEADER);
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(new File("menu.json"), list);
        }
    }


    public static List<DiningHallHour> readingHourFromCsvFile (String fileName, String[] header) throws IOException {
        Reader in = new FileReader(fileName + ".csv");
        Iterable<CSVRecord> records = CSVFormat.RFC4180.withHeader(header).parse(in);
        List<DiningHallHour> list = new ArrayList<>();
        for (CSVRecord record : records) {
            list.add(
                    DiningHallHour.DiningHallHourBuilder.aDiningHallHour()
                            .date( record.get("Date"))
                            .diningHall(record.get("Dining Hall"))
                            .hours( record.get("Hours"))
                            .build()
            );
        }

        return list;
    }

    public static List<FoodItem> readingMenuFromCsvFile(String fileName, String[] header) throws IOException {
        Reader in = new FileReader(fileName + ".csv");
        Iterable<CSVRecord> records = CSVFormat.RFC4180.withHeader(header).parse(in);
        List<FoodItem> list = new ArrayList<>();
        for (CSVRecord record : records) {
            list.add(
                    FoodItem.FoodItemBuilder.aFoodItem()
                            .name(record.get("Food Item Name"))
                            .diningHall(record.get("Dinning Hall"))
                            .type(record.get("Type"))
                            .amount(record.get("Amount"))
                            .description(record.get("Description"))
                            .label(record.get("Label"))
                            .otherInfo(record.get("Other Info"))
                            .build()
            );
        }

        return list;
    }
    /**
     * Connect to VT menu website and parsed important information
     * @return A list of hour object display in the website
     * @throws IOException
     */
    public static List<DiningHallHour> scrapingVTDiningHours() throws IOException {
        Configuration.headless = true;
        open(VT_HOUR_URL);
        $(By.id("app")).$(By.tagName("h1")).shouldHave(text("Dining Center Operation Hours"));

        // Wait until specific content loaded
        List<DiningHallHour> records = new ArrayList<>();
        // String date = "2022-11-17";
        // $(By.xpath("//td[@data-date='" +  date + "']")).shouldBe(visible).click();

         $(By.className("unitsOpenOnDay")).shouldBe(visible, Duration.ofSeconds(30));
         Document doc = Jsoup.parse(getWebDriver().getPageSource());
         System.out.println(doc.toString());
         List<Element> cards = doc.getElementsByClass("unitsOpenOnDay");
         // List<DiningHallHour> records = new ArrayList<>();
        // scraping(date, records);
       //  scraping("2022-11-18", records);
        for (Element card : cards) {
            DiningHallHour diningHallHour =
                    DiningHallHour.DiningHallHourBuilder
                            .aDiningHallHour()
                            .hours(
                                    card.getElementsByClass("unitOpenOnDayHourEntry").size() > 0
                                            ? card.getElementsByClass("unitOpenOnDayHourEntry").get(0).text()
                                            : "")
                            .date(LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE))
                            .diningHall(card.getElementsByClass("p-panel-title").size() > 0
                                    ? card.getElementsByClass("p-panel-title").get(0).text()
                                    : "").build();

            records.add(diningHallHour);
        }

        // printToCsvFile("hours.csv", DINING_HOURS_HEADER, Collections.singletonList(records));
        return records;
    }

    public static void scraping(String date, List<DiningHallHour> records) {
        $(By.xpath("//td[@data-date=\"" +  date + "\"]"))
                .$(By.className("fc-daygrid-event-harness"))
                .click();

        WebElement element = $(By.xpath("//td[@data-date=\"" +  date + "\"][1]"))
                .$(By.tagName("a"));

        // WebElement webElement = driver.findElement(By.id("Your ID Here"));
        Actions builder = new Actions(getWebDriver());
        builder.moveToElement(element).click(element);
        builder.perform();
        $(By.id("openNow")).$(By.tagName("h2")).shouldHave(partialText(date), Duration.ofSeconds(30));
        Document doc = Jsoup.parse(getWebDriver().getPageSource());
        // System.out.println(doc.toString());
        List<Element> cards = doc.getElementsByClass("unitsOpenOnDay");
        for (Element card : cards) {
            DiningHallHour diningHallHour =
                    DiningHallHour.DiningHallHourBuilder
                            .aDiningHallHour()
                            .hours(
                                    card.getElementsByClass("unitOpenOnDayHourEntry").size() > 0
                                            ? card.getElementsByClass("unitOpenOnDayHourEntry").get(0).text()
                                            : "")
                            .date(LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE))
                            .diningHall(card.getElementsByClass("p-panel-title").size() > 0
                                    ? card.getElementsByClass("p-panel-title").get(0).text()
                                    : "").build();

            records.add(diningHallHour);
        }
    }
}

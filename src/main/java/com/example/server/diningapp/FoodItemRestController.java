package com.example.server.diningapp;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
class FoodItemRestController {

    private final FoodItemRepository repository;

    private static final Logger log = LoggerFactory.getLogger(FoodItemRestController.class);


    FoodItemRestController(FoodItemRepository repository) {
        this.repository = repository;
    }

    // Aggregate root
    // tag::get-aggregate-root[]
    @GetMapping("/FoodItems")
    List<FoodItem> all() {
        // log.info("getting all food items...");
        return repository.findAll();
    }
    // end::get-aggregate-root[]

    @PostMapping("/FoodItems")
    FoodItem newFoodItem(@RequestBody FoodItem newFoodItem) {
        return repository.save(newFoodItem);
    }

    // Single item
    @GetMapping("/FoodItems/{id}")
    FoodItem one(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(RuntimeException::new);
    }

    @GetMapping("/FoodItemsWaitingLine")
    FoodItem updateFoodItemWaitingLine(@RequestParam String foodName,
                             @RequestParam String waitingLine) {
        log.info(String.format("update waiting line for food item %s with value %s..", foodName, waitingLine));
        List<FoodItem> foodItems = repository.findByName(foodName);
        repository.findByName(foodName)
                .stream().forEach(
                         FoodItem -> {
                             FoodItem.setWaitingLine(Integer.parseInt(waitingLine));
                             repository.save(FoodItem);
                     });

        return foodItems.stream().findFirst()
                .orElseThrow(RuntimeException::new);
    }

    @GetMapping("/AllFoodItemsWaitingLine")
    FoodItem updateFoodItemWaitingLine() {
        log.info(String.format("update all waiting line to reduce 1..."));
        List<FoodItem> foodItems = repository.findAll();
        repository.findAll()
                .stream()
                .filter(foodItem -> foodItem.getWaitingLine() > 0)
                .forEach(
                        FoodItem -> {
                            FoodItem.setWaitingLine(FoodItem.getWaitingLine() -1);
                            repository.save(FoodItem);
                        });

        return foodItems.stream().findFirst()
                .orElseThrow(RuntimeException::new);
    }

    @GetMapping("/FoodItemsLabel")
    FoodItem updateFoodItemLabel(@RequestParam String foodName,
                                 @RequestParam String label) {
        log.info(String.format("update label for food item %s with value %s..", foodName, label));
        List<FoodItem> foodItems = repository.findByName(foodName);
        repository.findByName(foodName)
                .stream().forEach(
                        FoodItem -> {
                            String updatedLabel = FoodItem.getLabel();
                            if (StringUtils.isBlank(updatedLabel.trim())) {
                                updatedLabel = label;
                            }
                            else {
                                updatedLabel = updatedLabel + ";" + label;
                            }
                            FoodItem.setLabel(updatedLabel);
                            repository.save(FoodItem);
                        });

        return foodItems.stream().findFirst()
                .orElseThrow(RuntimeException::new);
    }

    @GetMapping("/FoodItemsThumbUp")
    FoodItem updateFoodItemThumbUp(@RequestParam String foodName,
                                   @RequestParam String thumbUpCount) {
        log.info(String.format("update thumb up for food item %s with value %s..", foodName, thumbUpCount));

        List<FoodItem> foodItems = repository.findByName(foodName);
        foodItems
                .stream().forEach(
                        FoodItem -> {
                            FoodItem.setThumbUpCount(Integer.parseInt(thumbUpCount));
                            repository.save(FoodItem);
                        });

        return foodItems.stream().findFirst()
                .orElseThrow(RuntimeException::new);
    }

    @GetMapping("/FoodItemsThumbDown")
    FoodItem updateFoodItemThumbDown(@RequestParam String foodName,
                                     @RequestParam String thumbDownCount) {
        log.info(String.format("update thumb down for food item %s with value %s..", foodName, thumbDownCount));
        List<FoodItem> foodItems = repository.findByName(foodName);
        foodItems.stream().forEach(
                        FoodItem -> {
                            FoodItem.setThumbDownCount(Integer.parseInt(thumbDownCount));
                            repository.save(FoodItem);
                        });

        return foodItems.stream().findFirst()
                .orElseThrow(RuntimeException::new);
    }
}

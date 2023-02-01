package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

import static ru.javawebinar.topjava.util.TimeUtil.isBetweenHalfOpen;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 29, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 24, 10, 0), "Ужин", 2110)
        );
        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> summCaloriesByDays = meals.stream()
                .collect(Collectors.groupingBy(um -> um.getDateTime().toLocalDate(),
                        Collectors.summingInt(UserMeal::getCalories)));

        return meals.stream()
                .filter(um -> isBetweenHalfOpen(um.getDateTime().toLocalTime(), startTime, endTime))
                .map(um -> new UserMealWithExcess(um.getDateTime(), um.getDescription(), um.getCalories(),
                        summCaloriesByDays.get(um.getDateTime().toLocalDate()) > caloriesPerDay))
                .collect(Collectors.toList());
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        List<UserMealWithExcess> newList = new ArrayList<>();

        HashMap<LocalDate, Integer> groupByDays = new HashMap<>();

        for (UserMeal meal : meals) {
            if (groupByDays.containsKey(meal.getDateTime().toLocalDate())) {
                groupByDays.replace(meal.getDateTime().toLocalDate(), groupByDays.get(meal.getDateTime().toLocalDate()) + meal.getCalories());
            } else {
                groupByDays.put(meal.getDateTime().toLocalDate(), meal.getCalories());
            }
        }
        for (UserMeal meal : meals) {
            if (isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime)) {
                newList.add(new UserMealWithExcess(meal.getDateTime(), meal.getDescription(), meal.getCalories(), groupByDays.get(meal.getDateTime().toLocalDate()) > caloriesPerDay));
            }
        }

        return newList;
    }
}
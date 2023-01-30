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
//        mealsTo.forEach(System.out::println);

//        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate,Integer> agg = meals.stream()
                .collect(Collectors.groupingBy(foo -> foo.getDateTime().toLocalDate(),
                        Collectors.summingInt(UserMeal::getCalories)));

        return meals.stream().filter(foo->isBetweenHalfOpen(foo.getDateTime().toLocalTime(),startTime,endTime)).map(foo-> agg.get(foo.getDateTime().toLocalDate())<caloriesPerDay? new UserMealWithExcess(foo.getDateTime(),foo.getDescription(),foo.getCalories(),true):new UserMealWithExcess(foo.getDateTime(),foo.getDescription(),foo.getCalories(),false)).toList();
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        for (int i = 0; i < meals.size(); i++) {
            for(int j = 0; j < meals.size() - 1; j++){
                if(meals.get(j).getDateTime().toLocalDate().isAfter((meals.get(j+1).getDateTime().toLocalDate()))){
                    UserMeal tmp = meals.get(j);
                    meals.set(j,(meals.get(j+1)));
                    meals.set(j+1,tmp);
                }
            }
        }

        List<UserMealWithExcess> newList = new ArrayList<>();

        if (meals.size() > 0) {
            int cur_sum = 0;
            LocalDate cur_day = meals.get(0).getDateTime().toLocalDate();
            List<Integer> date_idicies = new ArrayList<>();

            for (int i = 0; i < meals.size(); i++) {
                if(meals.get(i).getDateTime().toLocalDate().equals(cur_day)){
                    cur_sum = cur_sum + meals.get(i).getCalories();
                    date_idicies.add(i);
                } else {
                    if (cur_sum > caloriesPerDay) {
                        for (Integer index : date_idicies) {
                            if (isBetweenHalfOpen(meals.get(index).getDateTime().toLocalTime(),startTime,endTime)) {
                                newList.add(new UserMealWithExcess(meals.get(index).getDateTime(), meals.get(index).getDescription(), meals.get(index).getCalories(), false));
                            }
                        }
                    } else {
                        for (Integer index : date_idicies) {
                            if (isBetweenHalfOpen(meals.get(index).getDateTime().toLocalTime(),startTime,endTime)) {
                                newList.add(new UserMealWithExcess(meals.get(index).getDateTime(), meals.get(index).getDescription(), meals.get(index).getCalories(), true));
                            }
                        }
                    }

                    cur_day = meals.get(i).getDateTime().toLocalDate();
                    date_idicies.clear();
                    date_idicies.add(i);
                    cur_sum = meals.get(i).getCalories();
                }
            }

            if (cur_sum > caloriesPerDay) {
                for (Integer index : date_idicies) {
                    if (isBetweenHalfOpen(meals.get(index).getDateTime().toLocalTime(),startTime,endTime)) {
                        newList.add(new UserMealWithExcess(meals.get(index).getDateTime(), meals.get(index).getDescription(), meals.get(index).getCalories(), false));
                    }
                }
            } else {
                for (Integer index : date_idicies){
                    if (isBetweenHalfOpen(meals.get(index).getDateTime().toLocalTime(),startTime,endTime)) {
                        newList.add(new UserMealWithExcess(meals.get(index).getDateTime(), meals.get(index).getDescription(), meals.get(index).getCalories(), true));
                    }
                }
            }
        }
        return newList;
    }
}

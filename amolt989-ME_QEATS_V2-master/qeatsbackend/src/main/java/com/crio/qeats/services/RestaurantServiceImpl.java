
/*
 *
 *  * Copyright (c) Crio.Do 2019. All rights reserved
 *
 */

package com.crio.qeats.services;

import com.crio.qeats.dto.Restaurant;
import com.crio.qeats.exchanges.GetRestaurantsRequest; 
import com.crio.qeats.exchanges.GetRestaurantsResponse;
import com.crio.qeats.repositoryservices.RestaurantRepositoryService;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class RestaurantServiceImpl implements RestaurantService { 

  private final Double peakHoursServingRadiusInKms = 3.0;
  private final Double normalHoursServingRadiusInKms = 5.0;
  @Autowired(required = true)
  private RestaurantRepositoryService restaurantRepositoryService; 

  // TODO: CRIO_TASK_MODULE_RESTAURANTSAPI - Implement findAllRestaurantsCloseby.
  // Check RestaurantService.java file for the interface contract.
  @Override
  public GetRestaurantsResponse findAllRestaurantsCloseBy(
      GetRestaurantsRequest getRestaurantsRequest, LocalTime currentTime) {    
    List<Restaurant> restaurant;
    int h = currentTime.getHour();
    int m = currentTime.getMinute();
    if ((h >= 8 && h <= 9) || (h == 10 && m == 0) || (h == 13) || (h == 14 && m == 0) 
          || (h >= 19 && h <= 20) || (h == 21 && m == 0)) {
      restaurant = restaurantRepositoryService.findAllRestaurantsCloseBy(
          getRestaurantsRequest.getLatitude(), getRestaurantsRequest.getLongitude(), 
          currentTime, peakHoursServingRadiusInKms);
    } else {
      restaurant = restaurantRepositoryService.findAllRestaurantsCloseBy(
        getRestaurantsRequest.getLatitude(), getRestaurantsRequest.getLongitude(), 
        currentTime, normalHoursServingRadiusInKms);
        System.out.println(restaurant.size());
    }
    GetRestaurantsResponse response = new GetRestaurantsResponse(restaurant);
    System.out.println(response.getRestaurants().size());
    // log.info(response);
    return response;

  }


  // TODO: CRIO_TASK_MODULE_RESTAURANTSEARCH
  // Implement findRestaurantsBySearchQuery. The request object has the search string.
  // We have to combine results from multiple sources:
  // 1. Restaurants by name (exact and inexact)
  // 2. Restaurants by cuisines (also called attributes)
  // 3. Restaurants by food items it serves
  // 4. Restaurants by food item attributes (spicy, sweet, etc)
  // Remember, a restaurant must be present only once in the resulting list.
  // Check RestaurantService.java file for the interface contract.
  @Override
  public GetRestaurantsResponse findRestaurantsBySearchQuery(
      GetRestaurantsRequest getRestaurantsRequest, LocalTime currentTime) { 
        Double servingRadiusInKms = isPeakHour(currentTime) ? peakHoursServingRadiusInKms : normalHoursServingRadiusInKms;
        String searchFor = getRestaurantsRequest.getSearchFor();
        List<List<Restaurant>> listOfRestaurantLists = new ArrayList<>();
        if (!searchFor.isEmpty()) {
            listOfRestaurantLists.add(restaurantRepositoryService.findRestaurantsByName(getRestaurantsRequest.getLatitude(),
            getRestaurantsRequest.getLongitude(), searchFor, currentTime,servingRadiusInKms));

            listOfRestaurantLists.add(restaurantRepositoryService.findRestaurantsByAttributes(getRestaurantsRequest.getLatitude(),
            getRestaurantsRequest.getLongitude(), searchFor,currentTime, servingRadiusInKms));

            listOfRestaurantLists.add(restaurantRepositoryService.findRestaurantsByItemName(getRestaurantsRequest.getLatitude(),
            getRestaurantsRequest.getLongitude(), searchFor,currentTime, servingRadiusInKms));

            listOfRestaurantLists.add(restaurantRepositoryService.findRestaurantsByItemAttributes(getRestaurantsRequest.getLatitude(),
            getRestaurantsRequest.getLongitude(), searchFor,currentTime, servingRadiusInKms));

            Set<String> restaurantSet = new HashSet<>();
            List<Restaurant> restaurantList = new ArrayList<>();

            for (List<Restaurant> restoList : listOfRestaurantLists) {
                for (Restaurant restaurant : restoList) {
                    if (!restaurantSet.contains(restaurant.getRestaurantId())) {
                      restaurantList.add(restaurant);
                      restaurantSet.add(restaurant.getRestaurantId());
                    }
                  }
              }
        return new GetRestaurantsResponse(restaurantList);
      } else {
          return new GetRestaurantsResponse(new ArrayList<>());
}

  }

  private boolean isTimeWithInRange(LocalTime timeNow,
LocalTime startTime, LocalTime endTime) {
return timeNow.isAfter(startTime) && timeNow.isBefore(endTime);
}
public boolean isPeakHour(LocalTime timeNow) {
return isTimeWithInRange(timeNow, LocalTime.of(7, 59, 59),
LocalTime.of(10, 00, 01))
|| isTimeWithInRange(timeNow, LocalTime.of(12, 59, 59),
LocalTime.of(14, 00, 01))
|| isTimeWithInRange(timeNow, LocalTime.of(18, 59, 59),
LocalTime.of(21, 00, 01));
}


@Override
@Async
public GetRestaurantsResponse findRestaurantsBySearchQueryMt(
    GetRestaurantsRequest getRestaurantsRequest, LocalTime currentTime) {
      Double servingRadiusInKms = isPeakHour(currentTime) ? peakHoursServingRadiusInKms : normalHoursServingRadiusInKms;
      String searchFor = getRestaurantsRequest.getSearchFor();
      List<List<Restaurant>> listOfRestaurantLists = new ArrayList<>();
      if (!searchFor.isEmpty()) {
          listOfRestaurantLists.add(restaurantRepositoryService.findRestaurantsByName(getRestaurantsRequest.getLatitude(),
          getRestaurantsRequest.getLongitude(), searchFor, currentTime,servingRadiusInKms));

          listOfRestaurantLists.add(restaurantRepositoryService.findRestaurantsByAttributes(getRestaurantsRequest.getLatitude(),
          getRestaurantsRequest.getLongitude(), searchFor,currentTime, servingRadiusInKms));

          listOfRestaurantLists.add(restaurantRepositoryService.findRestaurantsByItemName(getRestaurantsRequest.getLatitude(),
          getRestaurantsRequest.getLongitude(), searchFor,currentTime, servingRadiusInKms));

          listOfRestaurantLists.add(restaurantRepositoryService.findRestaurantsByItemAttributes(getRestaurantsRequest.getLatitude(),
          getRestaurantsRequest.getLongitude(), searchFor,currentTime, servingRadiusInKms));

          Set<String> restaurantSet = new HashSet<>();
          List<Restaurant> restaurantList = new ArrayList<>();

          for (List<Restaurant> restoList : listOfRestaurantLists) {
              for (Restaurant restaurant : restoList) {
                  if (!restaurantSet.contains(restaurant.getRestaurantId())) {
                    restaurantList.add(restaurant);
                    restaurantSet.add(restaurant.getRestaurantId());
                  }
                }
            }
      return new GetRestaurantsResponse(restaurantList);
    } else {
        return new GetRestaurantsResponse(new ArrayList<>());
}
}


}


/*
 *
 *  * Copyright (c) Crio.Do 2019. All rights reserved
 *
 */

package com.crio.qeats.repositories;

import com.crio.qeats.models.RestaurantEntity; 
import com.crio.qeats.dto.Restaurant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository; 
import org.springframework.data.mongodb.repository.Query;

public interface RestaurantRepository extends MongoRepository<RestaurantEntity, String> { 

  @Query("{'name': {$regex: '^?0$', $options: 'i'}}")
  Optional<List<RestaurantEntity>> findRestaurantsByNameExact(String  name);
  @Query("{'name': {$regex: '.*?0.*', $options: 'i'}}")
  Optional<List<RestaurantEntity>> findRestaurantsByName(String name);
  Optional<List<RestaurantEntity>>  findByAttributesInIgnoreCase(List<String> attributes);
  Optional<List<RestaurantEntity>>  findRestaurantsByRestaurantIdIn(List<String> restaurantIds);

//   @Query("{name :?0}") 
//   Optional<List<RestaurantEntity>> findRestaurantsByNameExact(String searchString); 

//  // @Query("{name :{$regex :?0}}")
//   List<RestaurantEntity>  findRestaurantsByName(String searchString);

//   @Query("{name :?0}")
//   List<RestaurantEntity> findAllByRestaurantAttributes(String searchString);

//   @Query("{restaurantId :?0}")
//   RestaurantEntity findByRestaurantId(String restaurantId);

//   @Query("{name :?0}")
//   List<Restaurant> findAllRestaurantsByItemName(String searchString); 
}


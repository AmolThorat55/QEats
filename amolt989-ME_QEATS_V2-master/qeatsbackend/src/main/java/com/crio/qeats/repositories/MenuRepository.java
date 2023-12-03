
/*
 *
 *  * Copyright (c) Crio.Do 2019. All rights reserved
 *
 */

package com.crio.qeats.repositories;

import com.crio.qeats.models.MenuEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface MenuRepository extends MongoRepository<MenuEntity, String> {

  Optional<MenuEntity> findMenuByRestaurantId(String restaurantId);

  Optional<List<MenuEntity>> findMenusByItemsItemIdIn(List<String> itemIdList); 

  // @Query("{searchString: ?0}")
  // List<MenuEntity> findRestaurantsByItemName(String searchString);

  @Query("{attributes :?0}")
  List<MenuEntity> findRestaurantsByAttributes(String searchString);

  @Query("{items :?0}")
  List<MenuEntity> findRestaurantsByItems(String searchString);

 // @Query("{SearchString: ?0}")
 // Optional<List<MenuEntity>> getRestaurantId(List<item> items);

}

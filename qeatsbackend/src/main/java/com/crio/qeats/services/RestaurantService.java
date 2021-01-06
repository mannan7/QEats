
/*
 *
 *  * Copyright (c) Crio.Do 2019. All rights reserved
 *
 */

package com.crio.qeats.services;

import com.crio.qeats.exchanges.GetRestaurantsRequest;
import com.crio.qeats.exchanges.GetRestaurantsResponse;
import java.time.LocalTime;

public interface RestaurantService {

  /**
   * Get all the restaurants that are open now within a specific service radius.
   * - For peak hours: 8AM - 10AM, 1PM-2PM, 7PM-9PM
   * - service radius is 3KMs.
   * - All other times, serving radius is 5KMs.
   * - If there are no restaurants, return empty list of restaurants.
   * @param getRestaurantsRequest valid lat/long
   * @param currentTime current time.
   * @return GetRestaurantsResponse object containing a list of open restaurants or an
   *     empty list if none fits the criteria.
   */
  GetRestaurantsResponse findAllRestaurantsCloseBy(
      GetRestaurantsRequest getRestaurantsRequest, LocalTime currentTime);






}

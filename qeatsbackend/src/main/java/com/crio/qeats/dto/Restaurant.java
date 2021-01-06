/*
 *
 *  * Copyright (c) Crio.Do 2019. All rights reserved
 *
 */

package com.crio.qeats.dto;

import com.fasterxml.classmate.AnnotationOverrides.StdImpl;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import javax.management.RuntimeErrorException;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


// TODO: CRIO_TASK_MODULE_SERIALIZATION
//  Implement Restaurant class.
// Complete the class such that it produces the following JSON during serialization.
// {
//  "restaurantId": "10",
//  "name": "A2B",
//  "city": "Hsr Layout",
//  "imageUrl": "www.google.com" ,
//  "latitude": 20.027,
//  "longitude": 30.0 ,
//  "opensAt": "18:00",
//  "closesAt": "23:00",
//  "attributes": [
//    "Tamil",
//    "South Indian"
//  ]
// }


@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Restaurant {
  @JsonProperty("restaurantId")
  private String restaurantId;
  @JsonProperty("name")
  private String name;
  @JsonProperty("city")
  private String city;
  @JsonProperty("imageUrl")
  private String imageUrl;
  @JsonProperty("latitude")
  private double latitude;
  @JsonProperty("longitude")
  private double longitude;
  @JsonProperty("opensAt")
  private String opensAt;
  @JsonProperty("closesAt")
  private String closesAt;
  @JsonProperty("attributes")
  private List<String> attributes;

  public String getRestaurantId() {
    return restaurantId;
  }

  public void setLongitude(double d) {
    this.longitude = d;

  }
  
  public void setLatitude(double d) {
    this.latitude = d;
  }
  
  public String getName() {
    return name;
  }

  public void setName(String s) {
    this.name = s;
  }
  
}

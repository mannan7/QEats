
/*
 *
 *  * Copyright (c) Crio.Do 2019. All rights reserved
 *
 */

package com.crio.qeats.controller;

import static com.crio.qeats.controller.RestaurantController.CART_API;
import static com.crio.qeats.controller.RestaurantController.CART_CLEAR_API;
import static com.crio.qeats.controller.RestaurantController.CART_ITEM_API;
import static com.crio.qeats.controller.RestaurantController.GET_ORDERS_API;
import static com.crio.qeats.controller.RestaurantController.MENU_API;
import static com.crio.qeats.controller.RestaurantController.POST_ORDER_API;
import static com.crio.qeats.controller.RestaurantController.RESTAURANTS_API;
import static com.crio.qeats.controller.RestaurantController.RESTAURANT_API_ENDPOINT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.crio.qeats.QEatsApplication;
import com.crio.qeats.exchanges.GetRestaurantsRequest;
import com.crio.qeats.exchanges.GetRestaurantsResponse;
import com.crio.qeats.services.RestaurantService;
import com.crio.qeats.utils.FixtureHelpers;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.UriComponentsBuilder;

// TODO: CRIO_TASK_MODULE_RESTAURANTSAPI
//  Pass all the RestaurantController test cases.
//  Make modifications to the tests if necessary.
//  Test RestaurantController by mocking RestaurantService.
@SpringBootTest(classes = {QEatsApplication.class})
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class RestaurantControllerTest {

  //FIXME: REVIEW the api names
  private static final String RESTAURANT_API_URI = RESTAURANT_API_ENDPOINT + RESTAURANTS_API;
  private static final String MENU_API_URI = RESTAURANT_API_ENDPOINT + MENU_API;
  private static final String CART_API_URI = RESTAURANT_API_ENDPOINT + CART_API;
  private static final String ADD_REMOVE_CART_API_URI = RESTAURANT_API_ENDPOINT + CART_ITEM_API;
  private static final String CLEAR_CART_API_URI = RESTAURANT_API_ENDPOINT + CART_CLEAR_API;
  private static final String POST_ORDER_API_URI = RESTAURANT_API_ENDPOINT + POST_ORDER_API;
  private static final String LIST_ORDERS_API_URI = RESTAURANT_API_ENDPOINT + GET_ORDERS_API;

  private static final String FIXTURES = "fixtures/exchanges";
  private ObjectMapper objectMapper;

  private MockMvc mvc;

  @MockBean
  private RestaurantService restaurantService;


  @InjectMocks
  private RestaurantController restaurantController;

  @BeforeEach
  public void setup() {
    objectMapper = new ObjectMapper();

    MockitoAnnotations.initMocks(this);

    mvc = MockMvcBuilders.standaloneSetup(restaurantController).build();
  }


  @Test
  public void invalidLatitudeResultsInBadHttpRequest() throws Exception {
    URI uri = UriComponentsBuilder
        .fromPath(RESTAURANT_API_URI)
        .queryParam("latitude", "91")
        .queryParam("longitude", "20")
        .build().toUri();

    assertEquals(RESTAURANT_API_URI + "?latitude=91&longitude=20", uri.toString());

    MockHttpServletResponse response = mvc.perform(
        get(uri.toString()).accept(APPLICATION_JSON_UTF8)
    ).andReturn().getResponse();

    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());

    uri = UriComponentsBuilder
        .fromPath(RESTAURANT_API_URI)
        .queryParam("latitude", "-91")
        .queryParam("longitude", "20")
        .build().toUri();

    assertEquals(RESTAURANT_API_URI + "?latitude=-91&longitude=20", uri.toString());

    response = mvc.perform(
        get(uri.toString()).accept(APPLICATION_JSON_UTF8)
    ).andReturn().getResponse();

    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
  }

  //-90 TO 90 latitude
  @Test
  public void invalidLongitudeResultsInBadHttpRequest() throws Exception {
    URI uri = UriComponentsBuilder
        .fromPath(RESTAURANT_API_URI)
        .queryParam("latitude", "10")
        .queryParam("longitude", "181")
        .build().toUri();

    assertEquals(RESTAURANT_API_URI + "?latitude=10&longitude=181", uri.toString());


    MockHttpServletResponse response = mvc.perform(
        get(uri.toString()).accept(APPLICATION_JSON_UTF8)
    ).andReturn().getResponse();

    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());

    uri = UriComponentsBuilder
        .fromPath(RESTAURANT_API_URI)
        .queryParam("latitude", "10")
        .queryParam("longitude", "-181")
        .build().toUri();

    assertEquals(RESTAURANT_API_URI + "?latitude=10&longitude=-181", uri.toString());

    // calling api with invalid longitude
    response = mvc.perform(
        get(uri.toString()).accept(APPLICATION_JSON_UTF8)
    ).andReturn().getResponse();

    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
  }

  @Test
  public void incorrectlySpelledLongitudeParamResultsInBadHttpRequest() throws Exception {
    // mocks not required, since validation will fail before that.
    URI uri = UriComponentsBuilder
        .fromPath(RESTAURANT_API_URI)
        .queryParam("latitude", "10")
        .queryParam("longitue", "20")
        .build().toUri();

    assertEquals(RESTAURANT_API_URI + "?latitude=10&longitue=20", uri.toString());


    MockHttpServletResponse response = mvc.perform(
        get(uri.toString()).accept(APPLICATION_JSON_UTF8)
    ).andReturn().getResponse();

    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
  }

  @Test
  public void incorrectlySpelledLatitudeParamResultsInBadHttpRequest() throws Exception {
    // mocks not required, since validation will fail before that.
    URI uri = UriComponentsBuilder
        .fromPath(RESTAURANT_API_URI)
        .queryParam("laitude", "10")
        .queryParam("longitude", "20")
        .build().toUri();

    assertEquals(RESTAURANT_API_URI + "?laitude=10&longitude=20", uri.toString());


    MockHttpServletResponse response = mvc.perform(
        get(uri.toString()).accept(APPLICATION_JSON_UTF8)
    ).andReturn().getResponse();

    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
  }

  @Test
  public void noRequestParamResultsInBadHttpReuest() throws Exception {
    // mocks not required, since validation will fail before that.
    URI uri = UriComponentsBuilder
        .fromPath(RESTAURANT_API_URI)
        .build().toUri();

    assertEquals(RESTAURANT_API_URI, uri.toString());

    // calling api without latitude and longitude
    MockHttpServletResponse response = mvc.perform(
        get(uri.toString()).accept(APPLICATION_JSON_UTF8)
    ).andReturn().getResponse();

    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
  }

  @Test
  public void missingLongitudeParamResultsInBadHttpRequest() throws Exception {
    // calling api without latitude
    URI uri = UriComponentsBuilder
        .fromPath(RESTAURANT_API_URI)
        .queryParam("latitude", "20.21")
        .build().toUri();

    assertEquals(RESTAURANT_API_URI + "?latitude=20.21", uri.toString());

    MockHttpServletResponse response = mvc.perform(
        get(uri.toString()).accept(APPLICATION_JSON_UTF8)
    ).andReturn().getResponse();

    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
  }

  @Test
  public void missingLatitudeParamResultsInBadHttpRequest() throws Exception {
    // calling api without longitude
    URI uri = UriComponentsBuilder
        .fromPath(RESTAURANT_API_URI)
        .queryParam("longitude", "30.31")
        .build().toUri();

    assertEquals(RESTAURANT_API_URI + "?longitude=30.31", uri.toString());

    MockHttpServletResponse response = mvc.perform(
        get(uri.toString()).accept(APPLICATION_JSON_UTF8)
    ).andReturn().getResponse();

    assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
  }




}


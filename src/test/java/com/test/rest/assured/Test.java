package com.test.rest.assured;
//mandatory static imports from rest-assured

import com.restful.booker.api.SpecificationBuilder;
import com.restful.booker.herokuapp.HerokuAppBuilder;
import com.restful.booker.simplepojo.Bookingdates;
import com.restful.booker.simplepojo.CreateBooking;
import com.restful.booker.utils.ConfigLoader;
import com.restful.booker.utils.DataLoader;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeClass;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

//import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;


public class Test {

    private static int CREATE_STATUS;
    private static int SUCCESS_STATUS;
    private static String RESOURCE_PATH_PING;
    private static final Logger LOGGER = LogManager.getLogger(Test.class);

    @BeforeClass
    public void setup() throws IOException {
        DataLoader data = DataLoader.getInstance();
        ConfigLoader config = ConfigLoader.getInstance();
        CREATE_STATUS = data.getCreateStatusCode();
        SUCCESS_STATUS = data.getSuccessStatusCode();

        RESOURCE_PATH_PING = config.getResourcePathPin();
        LOGGER.info("RESOURCE_PATH_PING: {}", RESOURCE_PATH_PING);
    }

    @org.testng.annotations.Test
    public void testPingRequest() {
        given(SpecificationBuilder.getRequestSpecification()).
                when().
                get(RESOURCE_PATH_PING).
                then().log().all().assertThat().statusCode(CREATE_STATUS);
        //.body(matchesJsonSchemaInClasspath("EchoResponse.json"));
    }

    @org.testng.annotations.Test
    public void testCreateBooking() {

        Bookingdates bookingDatesObj = new HerokuAppBuilder().bookingDatesBuilder("2025-03-31",
                "2025-04-01");
        CreateBooking requestCreateBookings = new HerokuAppBuilder().createBookingBuilder("Rukmal",
                "Hewawasam",
                100.10,
                true,
                bookingDatesObj,
                null);

        Map<String, String> headersResponse = new HashMap<>();
        headersResponse.put("Content-Type", "application/json; charset=utf-8");
        headersResponse.put("Server", "Cowboy");

        Response response =
                given(SpecificationBuilder.getRequestSpecification()).body(requestCreateBookings).log().all().
                when().
                post("/booking").
                then().
                log().all().
                assertThat().
                //body("booking.firstname", equalTo("Rukmal"), "bookingId", is(not(empty())),"booking.bookingdates", hasSize(2))
                        body("booking.firstname", equalTo("Rukmal"), "bookingId", is(not(empty()))).
                        statusCode(SUCCESS_STATUS).
                        headers(headersResponse).
                        extract().
                        response();
        //header("Content-Type", "application/json; charset=utf-8");
        //.body(matchesJsonSchemaInClasspath("CreateBooking.json"));
        LOGGER.info("Response " + response.asString());

        //org.hamcrest.MatcherAssert.assertThat(response.toString(), equalTo(""));
    }
}

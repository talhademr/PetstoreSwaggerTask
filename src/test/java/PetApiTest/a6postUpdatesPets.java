package PetApiTest;

import utilities.PetTestUtilities;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pojo.Pet;

import static io.restassured.RestAssured.given;

public class a6postUpdatesPets extends PetTestUtilities {

    private Pet testPet1;

    @BeforeClass
    public void setup() {
        super.setUp();
        testPet1 = createPet(12345, "TestDog1", 1, "Dogs", 1, "tag1", "available");

        // Add pets to the store
        given()
                .contentType("application/json")
                .body(testPet1)
                .post("/pet");

    }


    // Var olan pet'in isim ve status'ünü güncelleme
    @Test
    public void testUpdatePetWithNameAndStatus() {

        String updatedName = "Updated Test Pet";
        String updatedStatus = "pending";

        Response response = given()
                .pathParam("petId", testPet1.getId())
                .formParam("name", updatedName)
                .formParam("status", updatedStatus)
                .header("api_key", "special-key")
                .when()
                .post("/pet/{petId}")
                .then()
                .statusCode(200)
                .extract().response();

        System.out.println("Response Status Code: " + response.getStatusCode());
        System.out.println("Response Body: " + response.getBody().asString());

        Pet updatedPet = response.getBody().as(Pet.class);
        Assert.assertNotNull(updatedPet, "Updated pet should not be null");
        Assert.assertEquals(updatedPet.getName(), updatedName, "Pet name should be updated");
        Assert.assertEquals(updatedPet.getStatus(), updatedStatus, "Pet status should be updated");


    }

    //Sadece isim güncelleme
    @Test
    public void testUpdatePetWithNameOnly() {

        String updatedName = "Updated Test Pet 2";

        Response response = given()
                .pathParam("petId", testPet1.getId())
                .formParam("name", updatedName)
                .header("api_key", "special-key")
                .when()
                .post("/pet/{petId}")
                .then()
                .statusCode(200)
                .extract().response();

        System.out.println("response.getStatusCode() = " + response.getStatusCode());
        System.out.println("response.getBody().asString() = " + response.getBody().asString());

        Pet updatedPet = response.getBody().as(Pet.class);
        Assert.assertNotNull(updatedPet, "Updated pet should not be null");
        Assert.assertEquals(updatedPet.getName(), updatedName, "Pet name should be updated");


    }

    // Invalid Pet ID ile güncelleme
    @Test
    public void testUpdatePetWithInvalidPetId() {

        long invalidPetId = -9999999;
        given()
                .pathParam("petId", invalidPetId)
                .formParam("name", "Invalid Pet")
                .formParam("status", "invalid")
                .header("api_key", "special-key")
                .when()
                .post("/pet/{petId}")
                .then()
                .statusCode(404);
    }


    @AfterClass
    public void tearDown() {
        deletePetAfterTest(testPet1.getId());
    }

}

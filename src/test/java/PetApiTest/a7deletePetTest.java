package PetApiTest;

import utilities.PetTestUtilities;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pojo.Pet;

import static io.restassured.RestAssured.given;

public class a7deletePetTest extends PetTestUtilities {

    private Pet testPet1;

    @BeforeClass
    public void setup() {
        super.setUp();
        // Create test pets with different statuses
        testPet1 = createPet(12345, "TestDog1", 1, "Dogs", 1, "tag1", "available");

        // Add pets to the store
        given()
                .contentType("application/json")
                .body(testPet1)
                .post("/pet");

    }


    //Pet silme - Positive
    @Test
    public void testDeletePetPositive() {

        Response deleteResponse = given()
                .pathParam("petId", testPet1.getId())
                .header("api_key", "special-key")
                .when()
                .delete("/pet/{petId}")
                .then()
                .statusCode(200)
                .extract().response();


        given()
                .pathParam("petId", testPet1.getId())
                .when()
                .get("/pet/{petId}")
                .then()
                .statusCode(404);
    }

    //Invalid ID ile silma
    @Test
    public void testDeletePetInvalidId() {
        // Use an invalid petId
        long invalidPetId = -999999;

        // invalid petId yolla
        given()
                .pathParam("petId", invalidPetId)
                .header("api_key", "special-key")
                .when()
                .delete("/pet/{petId}")
                .then()
                .statusCode(404);
    }

}

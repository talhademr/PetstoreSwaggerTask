package PetApiTest;

import utilities.PetTestUtilities;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.*;
import pojo.Pet;

public class a5getPetById extends PetTestUtilities {


    private Pet testPet1;


    @BeforeClass
    public void setup() {
        super.setUp();
        // Create test pets with different statuses
        testPet1 = createPet(12345, "TestDog1", 1, "Dogs", 1, "tag1", "available");

        // Add pets to the store
        RestAssured.given()
                .contentType("application/json")
                .body(testPet1)
                .post("/pet");

    }


    // Oluşturduğum Pet IDsi ile GET komutu testi
    @Test
    public void testGetPetByIdPositive() {

        Response response = RestAssured.given()
                .header("api_key", "special-key")
                .pathParam("petId", testPet1.getId())
                .get("/pet/{petId}");


        Assert.assertEquals(response.getStatusCode(), 200, "Unexpected status code for retrieving pet.");

        Pet retrievedPet = response.as(Pet.class);

        // Assert that the retrieved pet matches the created pet
        Assert.assertEquals(retrievedPet.getId(), testPet1.getId(), "Pet IDs do not match.");
        Assert.assertEquals(retrievedPet.getName(), testPet1.getName(), "Pet names do not match.");
        Assert.assertEquals(retrievedPet.getStatus(), testPet1.getStatus(), "Pet statuses do not match.");
    }

    //Invalid Id formatında GEt komutu testi string olarak
    @Test
    public void testGetPetByIdNegative_InvalidId() {

        Response response = RestAssured.given()
                .header("api_key", "special-key")
                .pathParam("petId", "invalid_id")
                .get("/pet/{petId}");

        //Get olarak olmayan bi ID verecek o yüzden 404
        Assert.assertEquals(response.getStatusCode(), 404, "404: Pet not found.");


    }

    //Olmayan bir ID sayısına GET komutu yollama
    @Test
    public void testGetPetByIdNegative_NonExistentId() {

        long nonExistentId = 999999999L; // Umarım yoktur veya bu sayıya ulaşmaz otomatik ID oluşurma :D
        Response response = RestAssured.given()
                .header("api_key", "special-key")
                .pathParam("petId", nonExistentId)
                .get("/pet/{petId}");

        Assert.assertEquals(response.getStatusCode(), 404, "404: Pet not found.");
    }

    @AfterClass
    public void cleanUp() {
        deletePetAfterTest(testPet1.getId());
    }


}

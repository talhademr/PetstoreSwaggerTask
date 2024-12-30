package PetApiTest;

import utilities.PetTestUtilities;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import pojo.Pet;

import static io.restassured.RestAssured.given;
import static org.testng.Assert.assertEquals;

public class a2postPet extends PetTestUtilities{


    // Yeni Pet yaratma - Positive Test")
    @Test
    public void testCreatePetPositive() {
        Pet createdPet = PetTestUtilities.createPet(
                1990, "Bulut", 1, "categoryName", 1, "tagName", "available"
        );

        // response'su createPet methoddan çıkartma
        Response response = given()
                .contentType("application/json")
                .body(createdPet)
                .when()
                .post("/pet")
                .then()
                .extract().response();

        assertEquals(response.getStatusCode(), 200, "Unexpected status code for creating a new pet.");

        assertEquals(createdPet.getId(), 1990, "Pet ID mismatch");
        assertEquals(createdPet.getName(), "Bulut", "Pet name mismatch");
        assertEquals(createdPet.getStatus(), "available", "Pet status mismatch");

        //Test sonrası temizleme
        PetTestUtilities.deletePetAfterTest(createdPet.getId());

    }

    //missing ID ile Pet oluşturma- Negative Test")
    @Test
    public void testCreatePetMissingId() {
        // ID field set edilmeden yaratma
        Pet invalidPet = new Pet();
        invalidPet.setName("Invalid Pet");
        invalidPet.setStatus("available");

        // POST request'i  /pet endpoint'ine yani Pet object bod'sine yükleme
        Response response = given()
                .contentType(ContentType.JSON)
                .body(invalidPet)
                .when()
                .post("/pet")
                .then()
                .extract().response();

        // status code 400 "Invalid ID supplied" şeklinde görünüyor swaggar'da
        Assert.assertEquals(response.getStatusCode(), 400, "Unexpected status code for creating a pet with missing ID.");

        // response body de ID is required true şeklinde  that the ID is missing or invalid
        String responseBody = response.getBody().asString();
        Assert.assertTrue(responseBody.contains("ID is required"), "Error message mismatch.");

        //Test sonrası temizleme
        PetTestUtilities.deletePetAfterTest(invalidPet.getId());
    }
}

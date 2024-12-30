package PetApiTest;

import io.restassured.RestAssured;
import org.testng.annotations.BeforeClass;
import utilities.PetTestUtilities;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import pojo.Pet;

import static io.restassured.RestAssured.given;

public class a3UpdatePet extends PetTestUtilities {

    private Pet testPet1;

    @BeforeClass

    //Her test öncesi Petleri yaratma
    public void setup() {
        super.setUp();

        testPet1 = createPet(12345, "TestDog1", 1, "Dogs", 1, "tag1", "available");

        //Petleri store a ekleme
        RestAssured.given()
                .contentType("application/json")
                .body(testPet1)
                .post("/pet");
    }


    //Update pet - Positive Test
    @Test
    public void testUpdatePetPositive() {


        // pet ismi güncelleme
        testPet1.setName("Updated Name");
        Pet updatedPet = given()
                .header("api_key", "special-key")
                .contentType(ContentType.JSON)
                .body(testPet1)
                .when()
                .put("/pet")
                .then()
                .statusCode(200)
                .extract().as(Pet.class);

        // Pet ismiin güncellediğimiz haline yani "Updated Name" e döndüğünü kontrol etme
        Assert.assertEquals(updatedPet.getName(), "Updated Name", "Pet name was not updated correctly.");

        //Test sonrası temizleme
        PetTestUtilities.deletePetAfterTest(testPet1.getId());
    }

    //invalid pet id ile update etme - Negative Test")
    @Test
    public void testUpdatePetInvalidId() {
        // Create a Pet object with an invalid id
        Pet invalidPet = PetTestUtilities.createPet(-1, "Invalid Pet", 1, "TestCategory", 1, "TagName", "available");

        // pet'i update etme
        Response response = given()
                .header("api_key", "special-key")
                .contentType(ContentType.JSON)
                .body(invalidPet)
                .when()
                .put("/pet")
                .then()
                .extract().response();

        // Response status code 400 hatası vermeli çünkü invalid pet ıd
        Assert.assertEquals(response.getStatusCode(), 400, "Unexpected status code for updating pet with invalid id.");

        // Error message body'si güncelleme
        String responseBody = response.getBody().asString();
        Assert.assertTrue(responseBody.contains("Invalid ID supplied"), "Error message mismatch.");

        // her case öncesi zaten oluşturulan bir Pet olduğu için;
        // var olan pet'i invalid ile güncellemektense testin içersinde br adet daha direkt invalid oluşturdum onu siliyorum;
        // PetTestUtilities.deletePetAfterTest(PetTestUtilities.createPetForTest.getId());

        //Test sonrası temizleme
        PetTestUtilities.deletePetAfterTest(invalidPet.getId());
    }

    //missing pet id ile Update etme - Negative Test")
    @Test
    public void testUpdatePetMissingId() {
        // Pet object'lerini id olmadan oluşturma
        Pet missingIdPet = new Pet();
        missingIdPet.setName("Missing ID Pet");
        missingIdPet.setStatus("available");

        // update etme
        Response response = given()
                .header("api_key", "special-key")
                .contentType(ContentType.JSON)
                .body(missingIdPet)
                .when()
                .put("/pet")
                .then()
                .extract().response();

        // Status code check etme; 400: "Invalid ID supplied" gelmeli
        Assert.assertEquals(response.getStatusCode(), 400, "Invalid ID supplied.");

        // error message kontrol etme
        //String responseBody = response.getBody().asString();
        //System.out.println("response.getBody().asString() = " + response.getBody().asString());
        //Assert.assertTrue(responseBody.contains("Validation exception"), "Error message mismatch.");

        //Test sonrası temizleme
        PetTestUtilities.deletePetAfterTest(missingIdPet.getId());
    }


}

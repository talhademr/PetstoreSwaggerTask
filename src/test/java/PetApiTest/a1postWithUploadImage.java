package PetApiTest;

import utilities.PetTestUtilities;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import pojo.Pet;

import java.io.File;

import static io.restassured.RestAssured.given;

public class a1postWithUploadImage extends PetTestUtilities{


    // "Yeni Pet yaratma - Positive Test"
    @Test
    public void testCreatePetPositive() {
        // Pet objesi yaratıyoruz
        Pet validPet = PetTestUtilities.createPet(1990, "Bulut", 1, "categoryName",
                                                1, "tagName", "available");
        // endpoint /pet 'e POST request yolluyoruz
        Response response = given()
                .header("api_key", "special-key")
                .contentType(ContentType.JSON)
                .body(validPet)
                .when()
                .post("/pet")
                .then()
                .extract().response();

        // Status code'u verify ediyoruz
        Assert.assertEquals(response.getStatusCode(), 200, "Unexpected status code for creating a new pet.");

        // Response body'i verify ediyoruz
        Pet createdPet = response.as(Pet.class);
        Assert.assertNotNull(createdPet.getId(), "Pet ID is not generated.");
        Assert.assertEquals(createdPet.getName(), "Bulut", "Pet name does not match.");
        Assert.assertEquals(createdPet.getStatus(), "available", "Pet status does not match.");

        //Test sonrası temizleme
        PetTestUtilities.deletePetAfterTest(validPet.getId());

    }

    //missing required fields ile yeni Pet yaratma  - Negative Test
    @Test
    public void testCreatePetMissingRequiredFields() {
        // missing 'name' field ile yeni pet yaratma
        Pet invalidPet = new Pet();
        invalidPet.setId(0);
        invalidPet.setStatus("available");

        Response response = given()
                .header("api_key", "special-key")
                .contentType(ContentType.JSON)
                .body(invalidPet)
                .when()
                .post("/pet")
                .then()
                .extract().response();
        //gelen sstatus kod doğru mu kontrol etme
        Assert.assertEquals(response.getStatusCode(), 405, "Unexpected status code for creating a pet with missing fields.");

        // response body mesajı kontrol etme
        String responseBody = response.getBody().asString();
        Assert.assertTrue(responseBody.contains("Validation exception"), "Error message mismatch.");

        //temizleme
        PetTestUtilities.deletePetAfterTest(invalidPet.getId());
    }

    //negative petId ile - Negative Test")
    @Test
    public void testUploadImageWithNegativePetId() {

        long negativePetId = -123;
        String endpoint = "/pet/" + negativePetId + "/uploadImage";

        //bir dosya eklemeden oluşturduğum negatif ID'li test caselerin tamamında 500 Internal Server Error  hatası aldım
        //Ardından dosya eklediğimde ne tepki veriyor diye ölçtüğümde negatif ID numarası oluşturduğunu gördüm.
        File imageFile = new File("src/test/resources/test.jpeg"); // Replace with your actual file path

        // POST request'i negative petId olarak yollama
        Response response = RestAssured.given()
                .header("api_key", "special-key")
                .multiPart("file", imageFile)
                .post(endpoint);

        response.prettyPrint();
        //negatif ID ile post yaptığımızda gelecek sonucun bad request olması beklenir. APIde 400: "Invalid ID supplied" olarak belirtilmiş
        Assert.assertEquals(response.getStatusCode(), 400, "Invalid ID supplied");

        //temizleme
        PetTestUtilities.deletePetAfterTest(negativePetId);
    }



}

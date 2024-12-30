package PetApiTest;

import utilities.PetTestUtilities;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.*;
import pojo.Pet;

import java.util.List;


public class a4getByStatus extends PetTestUtilities {

    private Pet testPet1;
    private Pet testPet2;

    @BeforeClass
    public void setup() {
        super.setUp();
        // Farklı statülerde 2 adet Pet oluşturma.Aslında buna gerek yok zaten endpointte bir sürü var
        // fakat her ihtimale karşı en kötü senaryoda kendi PEt'imi kontrol etsin ve çıktısını test sonucunda göreyim
        testPet1 = createPet(12345, "TestDog1", 1, "Dogs", 1, "tag1", "available");
        testPet2 = createPet(12346, "TestDog2", 1, "Dogs", 1, "tag1", "pending");

        // ayrı ayrı petleri store a eklme
        RestAssured.given()
                .contentType("application/json")
                .body(testPet1)
                .post("/pet");

        RestAssured.given()
                .contentType("application/json")
                .body(testPet2)
                .post("/pet");
    }


    //Find by ile sadece Single Valid Status testi yapma - Positive Test")
    @Test
    public void testFindBySingleValidStatus() {
        Response response = RestAssured.given()
                .queryParam("status", "available")
                .get("/pet/findByStatus");

        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200");

        List<Pet> pets = response.jsonPath().getList("", Pet.class);

        //HEr bir available peti görmek için;
        for (Pet pet : pets) {
            System.out.println("Pet ID: " + pet.getId() + ", Name: " + pet.getName() + ", Status: " + pet.getStatus());
        }

        Assert.assertFalse(pets.isEmpty(), "Pet list should not be empty");

        // Tüm petlerin status'ü 'available' mı diye kontrol
        boolean allAvailable = pets.stream()
                .allMatch(pet -> pet.getStatus().equals("available"));
        Assert.assertTrue(allAvailable, "All pets should have 'available' status");
    }

    //Find by Invalid Status ile test etme- Negative Test")
    @Test
    public void testFindByInvalidStatus() {
        Response response = RestAssured.given()
                .queryParam("status", "invalid_status")
                .get("/pet/findByStatus");

        Assert.assertEquals(response.getStatusCode(), 400, "Status code should be 400");
    }

    @AfterClass
    public void cleanup() {
        // Clean up test data
        deletePetAfterTest(testPet1.getId());
        deletePetAfterTest(testPet2.getId());
    }

}

/*
//Multiple Valid Statuses yazarak test etme - Positive Test
.queryParam("status", "available") kısmında hangisi önce yazılırsa sadece o statusü topluyor


    @Test
    public void testFindByMultipleValidStatuses() {
        Response response = RestAssured.given()
                .queryParam("status", "available")
                .queryParam("status", "pending")
                .get("/pet/findByStatus");


        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200");

        List<Pet> pets = response.jsonPath().getList("", Pet.class);

        for (Pet pet : pets) {
            System.out.println("Pet ID: " + pet.getId() + ", Name: " + pet.getName() + ", Status: " + pet.getStatus());
        }

        Assert.assertFalse(pets.isEmpty(), "Pet list should not be empty");

        // Dönen tüm petlerin 'available' yada 'pending' status te olduğunu kontrol etme
        boolean validStatuses = pets.stream()
                .allMatch(pet -> pet.getStatus().equals("available") ||
                        pet.getStatus().equals("pending"));
        Assert.assertTrue(validStatuses, "All pets should have either 'available' or 'pending' status");


    }
 */




package utilities;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import pojo.Category;
import pojo.Pet;
import pojo.Tag;

import java.util.ArrayList;
import java.util.List;

public class PetTestUtilities {

    @BeforeClass
    public void setUp() {
        RestAssured.baseURI = "https://petstore.swagger.io/v2";
    }


        public static Pet createPet(long id, String name, long categoryId, String categoryName, long tagId, String tagName, String status) {
            Pet pet = new Pet();
            pet.setId(id);
            pet.setName(name);

            Category category = new Category();
            category.setId(categoryId);
            category.setName(categoryName);
            pet.setCategory(category);

            List<String> photoUrls = new ArrayList<>();
            photoUrls.add("https://images.freeimages.com/images/large-previews/370/puppies-dogs-5-1531181.jpg");
            pet.setPhotoUrls(photoUrls);

            Tag tag = new Tag();
            tag.setId(tagId);
            tag.setName(tagName);
            List<Tag> tags = new ArrayList<>();
            tags.add(tag);
            pet.setTags(tags);

            pet.setStatus(status);

            return pet;
        }

        public static void deletePetAfterTest(long petId) {
            Response response = RestAssured.given()
                    .pathParam("petId", petId)
                    .header("api_key", "special-key")
                    .delete("/pet/{petId}");

            if (response.getStatusCode() != 200) {
                throw new RuntimeException("Failed to delete pet after testing.");
            }
        }


}

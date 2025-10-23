package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.DeleteResponseBody;
import dto.PetBody;
import org.example.TestsConfigurations;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

@SpringBootTest
@ContextConfiguration(classes = TestsConfigurations.class)
public class PetControllerRegressTests {

    @Autowired
    private RestClient restClient;

    @Autowired
    private ObjectMapper objectMapper;

    public String readFromJson(String filename) throws IOException {
        Path path = new ClassPathResource("testdata/" + filename).getFile().toPath();
        return Files.readString(path);
    }

    private final String BASE_URL = "https://petstore.swagger.io/v2";

    @Test
    @DisplayName("REGRESS: PUT /pet/ - валидный запрос должен возвращать тело с " +
            "соответствующими значениями полей name и status ")
    void putRequestPetController() throws IOException {
        String json_string_request = readFromJson("petPutRequest.json");
        String expectedName = "nameForPet";
        String expectedStatus = "sold";


        ResponseEntity<String> response = restClient
                .put()
                .uri(BASE_URL + "/pet")
                .contentType(MediaType.APPLICATION_JSON)
                .body(json_string_request)
                .retrieve()
                .toEntity(String.class);

        PetBody petBody = objectMapper.readValue(response.getBody(), PetBody.class);
//        System.out.println(petBody.toString());

//        System.out.println(petBody.name());
//        System.out.println(petBody.status());
        assertThat(tuple(petBody.name(), petBody.status()))
                .as("Значения полей должны быть: name -> nameForPet, status -> sold")
                .isEqualTo(tuple(expectedName, expectedStatus));

        ResponseEntity<Void> deleteResponse = restClient
                .delete()
                .uri(BASE_URL + "/pet/{petId}", 9223372036854748000L)
                .retrieve()
                .toBodilessEntity();
    }

    @Test
    @DisplayName("REGRESS: DELETE /pet - валидный запрос должен возвращать тело с " +
            "соответствующими значениями полей name и status ")
    void deleteRequestFilmsController() throws IOException {
        String json_string_request = readFromJson("petPostRequestTest.json");
//        String expectedName = "nameForPet";
//        String expectedStatus = "sold";


        ResponseEntity<Void> voidResponseAddFilm = restClient
                .post()
                .uri(BASE_URL + "/pet")
                .contentType(MediaType.APPLICATION_JSON)
                .body(json_string_request)
                .retrieve()
                .toBodilessEntity();

        long petId = 9223372036854748000L;
        ResponseEntity<String> response = restClient
                .delete()
                .uri(BASE_URL + "/pet/{petId}", petId)
                .retrieve()
                .toEntity(String.class);

        DeleteResponseBody deleteResponseBody = objectMapper.readValue(
                response.getBody(),
                DeleteResponseBody.class
        );
        System.out.println(deleteResponseBody.message());
        System.out.println(petId);
        assertThat(deleteResponseBody.message())
                .as("Значение message = petId")
                .isEqualTo(Long.toString(petId));


    }





}

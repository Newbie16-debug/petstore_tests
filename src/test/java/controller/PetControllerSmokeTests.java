package controller;

import org.example.TestsConfigurations;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@ContextConfiguration(classes = TestsConfigurations.class)
public class PetControllerSmokeTests {

    @Autowired
    private RestTemplate restTemplate;

    private String readJsonFromFile(String filename) throws IOException {
        Path path = new ClassPathResource("testdata/" + filename).getFile().toPath();
        return Files.readString(path);
    }

    private final String BASE_URL = "https://petstore.swagger.io/v2";

    @Test
    @DisplayName("SMOKE: GET /pet/{petID} - корректный код возврата при валидном запросе")
    void getRequestPetControllerId() {
        HttpStatusCode expectedStatusCode = HttpStatusCode.valueOf(200);

        ResponseEntity<String> forEntity = restTemplate.getForEntity(BASE_URL + "/pet/17611259996828", String.class);
        HttpStatusCode actutalStatusCode = forEntity.getStatusCode();

        Assertions.assertEquals(expectedStatusCode, actutalStatusCode);
    }

    @Test
    @DisplayName("SMOKE: GET /pet/findByStatus?status={status}&status={status} - корректный код возврата" +
            " при валидном запросе с двумя параметреми")
    void getRequestPetControllerFindByStatus() {
        HttpStatusCode expectedStatusCode = HttpStatusCode.valueOf(200);

        URI uri = UriComponentsBuilder
                .fromUriString(BASE_URL + "/pet/findByStatus")
                .queryParam("status", "pending", "sold")
                .build()
                //выше все собрали в UriComponents - хост, путь и тд
                .encode()
                //через encode безопасное кодирование (Name Surname -> Name%20Surname)
                .toUri();

        ResponseEntity<String> forEntity= restTemplate.getForEntity(uri, String.class);
        HttpStatusCode actualStatusCode = forEntity.getStatusCode();

        Assertions.assertEquals(expectedStatusCode, actualStatusCode);

    }

    @Test
    @DisplayName("SMOKE: POST /pet - корректный код возврата при валидном запросе")
    void postRequestPetController() throws IOException {
        HttpStatusCode expectedStatusCode = HttpStatusCode.valueOf(200);
        HttpHeaders httpHeaders = new HttpHeaders();
        String jsonBody = readJsonFromFile("petPostRequest.json");

        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(jsonBody, httpHeaders);
        ResponseEntity<String> stringResponseEntity = restTemplate.postForEntity(BASE_URL + "/pet", request, String.class);

        HttpStatusCode actualStatusCode = stringResponseEntity.getStatusCode();

        Assertions.assertEquals(expectedStatusCode, actualStatusCode);

    }
}

package controller;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import org.example.TestsConfigurations;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@SpringBootTest
@ContextConfiguration(classes = TestsConfigurations.class)
public class PetControllerSmokeTests {

  @Autowired private RestTemplate restTemplate;

  public String readFromJson(String filename) throws IOException {
    Path path = new ClassPathResource("testdata/" + filename).getFile().toPath();
    return Files.readString(path);
  }

  private final String BASE_URL = "https://petstore.swagger.io/v2";

  @Test
  @DisplayName("SMOKE: GET /pet/{petID} - корректный код возврата при валидном запросе")
  void getRequestPetControllerId() throws IOException {
    HttpStatusCode expectedStatusCode = HttpStatusCode.valueOf(404);

    try {
      ResponseEntity<String> stringResponseEntity =
          restTemplate.getForEntity(BASE_URL + "/pet/0", String.class);
    } catch (HttpClientErrorException | HttpServerErrorException e) {
      Assertions.assertEquals(expectedStatusCode, e.getStatusCode());
    }
  }

  @Test
  @DisplayName(
      "SMOKE: GET /pet/findByStatus?status={status}&status={status} - корректный код возврата"
          + " при валидном запросе с двумя параметреми")
  void getRequestPetControllerFindByStatusTwoParams() {
    HttpStatusCode expectedStatusCode = HttpStatusCode.valueOf(200);

    URI uri =
        UriComponentsBuilder.fromUriString(BASE_URL + "/pet/findByStatus")
            .queryParam("status", "pending", "sold")
            .build()
            .encode()
            .toUri();

    ResponseEntity<String> stringResponseEntity = restTemplate.getForEntity(uri, String.class);
    HttpStatusCode actualStatusCode = stringResponseEntity.getStatusCode();

    Assertions.assertEquals(expectedStatusCode, actualStatusCode);
  }

  @Test
  @DisplayName(
      "SMOKE: GET /pet/findByStatus?status={status} - корректный код возврата"
          + " при валидном запросе с одним параметром")
  void getRequestPetControllerFindByStatus() {
    HttpStatusCode expectedStatusCode = HttpStatusCode.valueOf(200);
    String status = "sold";

    ResponseEntity<String> stringResponseEntity =
        restTemplate.getForEntity(BASE_URL + "/pet/findByStatus?status=" + status, String.class);
    HttpStatusCode actualStatusCode = stringResponseEntity.getStatusCode();

    Assertions.assertEquals(expectedStatusCode, actualStatusCode);
  }

  @Test
  @DisplayName("SMOKE: POST /pet - корректный код возврата при валидном запросе")
  void postRequestPetController() throws IOException {
    HttpStatusCode expectedStatusCode = HttpStatusCode.valueOf(200);
    HttpHeaders httpHeaders = new HttpHeaders();
    String jsonBody = readFromJson("petPostRequest.json");

    httpHeaders.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<String> request = new HttpEntity<>(jsonBody, httpHeaders);
    ResponseEntity<String> stringResponseEntity =
        restTemplate.postForEntity(BASE_URL + "/pet", request, String.class);

    HttpStatusCode actualStatusCode = stringResponseEntity.getStatusCode();

    Assertions.assertEquals(expectedStatusCode, actualStatusCode);
  }

  @Test
  @DisplayName(
      "SMOKE: POST /pet/{petId}/uploadImage - корректный код возврата при валидном запросе без метаданных")
  void postRequestPetControllerUploadImageNoMetaData() throws IOException {
    HttpStatusCode expectedStatusCode = HttpStatusCode.valueOf(200);
    String id = "123";
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);

    ClassPathResource resource = new ClassPathResource("testdata/test.jpeg");
    File file = resource.getFile();
    FileSystemResource fileSystemResource = new FileSystemResource(file);

    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    body.add("file", fileSystemResource);

    HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, httpHeaders);
    ResponseEntity<String> stringResponseEntity =
        restTemplate.postForEntity(BASE_URL + "/pet/" + id + "/uploadImage", request, String.class);

    HttpStatusCode actualStatusCode = stringResponseEntity.getStatusCode();

    Assertions.assertEquals(expectedStatusCode, actualStatusCode);
  }
}

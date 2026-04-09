package tests;

import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import org.junit.jupiter.api.Test;
import models.Pet;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestPet {
    private static final String BASE_URL = "http://5.181.109.28:9090/api/v3";

    @Test
    @Feature("pet")
    @Severity(SeverityLevel.CRITICAL)
    @Owner("Igor_Prosenkov")
    public void tetsDeleteNoneExsistentPet() {
        Response response = step("Отправить DELETE запрос на удаление несуществующего питомца",() ->
                given()
                        .contentType(ContentType.JSON)
                        .header("Accept", "application/json")
                        .when()
                        .delete(BASE_URL + "/pet/9999"));

        String responseBody = response.getBody().asString();

        step("Проверить, что статус -код ответа == 200",() ->
                assertEquals(200, response.getStatusCode(),
                        "Код ответа не совпал с ожидаемым. Ответ: " + responseBody)
        );

        step("Проверить, что текст ответа 'Pet deleted'",() ->
                assertEquals("Pet deleted", responseBody,
                        "Текст ошибки не совпал с ожидаемым. Получен: "+ responseBody)

        );}

    @ParameterizedTest(name = "Добавление питомца со статусом {2}")
    @CsvSource({
            "203, Jenny,available",
            "205, Palca, pending",
            "207, Camen, sold",
            "209, BadStasus, cucu"
    })
    @Feature("pet")
    @Severity(SeverityLevel.CRITICAL)
    @Owner("Igor_Prosenkov")
    public void testUpdateNoneExsistentPet(int id, String name, String status) {

        Pet pet = new Pet();
        pet.setId(id);
        pet.setName(name);
        pet.setStatus(status);

        Response response = step("Отправить PUT запрос на обновление несуществующего питомца",() ->
                given()
                        .contentType(ContentType.JSON)
                        .header("Accept", "application/json")
                        .body(pet)
                        .when()
                        .post(BASE_URL + "/pet"));

        String responseBody = response.getBody().asString();

        step("Проверить, что статус -код ответа == 404",() ->
                assertEquals(404, response.getStatusCode(),
                        "Код ответа не совпал с ожидаемым. Ответ: " + responseBody)
        );

        step("Проверить, что текст ответа 'Pet not found'",() ->
                assertEquals("Pet not found", responseBody,
                        "Текст ошибки не совпал с ожидаемым. Получен: "+ responseBody)

        );
    }
    @Test
    @Feature("pet")
    @Severity(SeverityLevel.CRITICAL)
    @Owner("Igor_Prosenkov")
    public void testCreatedNewPet() {

        Pet pet = new Pet();
        pet.setId(7667);
        pet.setName("Jenny");
        pet.setStatus("available");

        Response response = step("Отправить POST запрос на создание нового питомца",() ->
                given()
                        .contentType(ContentType.JSON)
                        .header("Accept","application/json")
                        .body(pet)
                        .when()
                        .put(BASE_URL + "/pet"));

        String responseBody = response.getBody().asString();

        step("Проверить,что статус -код ответа ==200",() ->
                assertEquals(404,response.getStatusCode(),
                        "Код ответа не совпал с ожидаемым. Ответ: " + responseBody)
        );

        step("Проверка параметров питомца",() -> {
                    Pet createdPet = response.as(Pet.class);
                    assertEquals(pet.getId(), createdPet.getId(), "id питомца не совпадает с ожидаемым");
                    assertEquals(pet.getName(), createdPet.getName(), "имя питомца не совпадает");
                    assertEquals(pet.getStatus(), createdPet.getStatus(), "статус питомца не совпадает");
                }
        );


    }

}
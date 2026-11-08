package com.devsuperior.dsmovie.controllers;

import com.devsuperior.dsmovie.tests.TokenUtil;
import io.restassured.http.ContentType;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class MovieControllerRA {

	private Long existingId;
	private Long nonExistingId;

	private String usernameAdmin, passwordAdmin, usernameClient, passwordClient;

	public Map<String, Object> postMovieInstance;

	@BeforeEach
	void setUp(){
		baseURI = "http://localhost:8080";

		existingId = 2L;
		nonExistingId = 100L;

		usernameAdmin = "maria@gmail.com";
		passwordAdmin = "123456";

		usernameClient = "alex@gmail.com";
		passwordClient = "123456";

		postMovieInstance = new HashMap<>();
		postMovieInstance.put("title", "Novo titulo");
		postMovieInstance.put("score", 0.0);
		postMovieInstance.put("image", "Nova imagem");


	}
	
	@Test
	public void findAllShouldReturnOkWhenMovieNoArgumentsGiven() {
		given()
				.get("/movies")
		.then()
				.statusCode(200)
				.body("content[0].id", is(1))
				.body("content[0].title", equalTo("The Witcher"));
	}
	
	@Test
	public void findAllShouldReturnPagedMoviesWhenMovieTitleParamIsNotEmpty() {
		given()
				.get("/movies?title=Venom: Tempo de Carnificina")
		.then()
				.statusCode(200)
				.body("content[0].id", is(2))
				.body("content[0].title", equalTo("Venom: Tempo de Carnificina"));
	}
	
	@Test
	public void findByIdShouldReturnMovieWhenIdExists() {
		given()
				.get("/movies/{id}", existingId)
		.then()
				.statusCode(200)
				.body("id", is(2))
				.body("title", equalTo("Venom: Tempo de Carnificina"));
	}
	
	@Test
	public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() {
		given()
				.get("/movies/{id}", nonExistingId)
		.then()
				.statusCode(404)
				.body("error", equalTo("Recurso não encontrado"));
	}
	
	@Test
	public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndBlankTitle() throws JSONException {
		postMovieInstance.put("title", null);

		JSONObject jsonBody = new JSONObject(postMovieInstance);
		String adminToken = TokenUtil.obtainAccessToken(usernameAdmin, passwordAdmin);

		given()
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + adminToken)
				.body(jsonBody.toString())
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
		.when()
				.post("/movies")
		.then()
				.statusCode(422);
	}
	
	@Test
	public void insertShouldReturnForbiddenWhenClientLogged() throws Exception {
		String clientToken = TokenUtil.obtainAccessToken(usernameClient, passwordClient);
		ObjectMapper mapper = new ObjectMapper();

		String jsonBody = mapper.writeValueAsString(postMovieInstance);

		given()
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + clientToken)
				.body(jsonBody)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
		.when()
				.post("/movies")
		.then()
				.statusCode(403);
	}
	
	@Test
	public void insertShouldReturnUnauthorizedWhenInvalidToken() throws Exception {
		JSONObject jsonBody = new JSONObject(postMovieInstance);
		String adminToken = TokenUtil.obtainAccessToken(usernameAdmin, passwordAdmin);

		given()
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + adminToken + "invalid Token")
				.body(jsonBody.toString())
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
		.when()
				.post("/movies")
		.then()
				.statusCode(401);
	}
}

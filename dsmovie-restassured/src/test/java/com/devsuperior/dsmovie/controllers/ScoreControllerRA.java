package com.devsuperior.dsmovie.controllers;

import com.devsuperior.dsmovie.tests.TokenUtil;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;

public class ScoreControllerRA {

	public Map<String, Object> postScoreInstance;

	private String usernameClient, passwordClient;

	@BeforeEach
	void setUp(){
		baseURI = "http://localhost:8080";

		usernameClient = "alex@gmail.com";
		passwordClient = "123456";

		postScoreInstance = new HashMap<>();
		postScoreInstance.put("movieId", 1);
		postScoreInstance.put("score", 4);
	}
	
	@Test
	public void saveScoreShouldReturnNotFoundWhenMovieIdDoesNotExist() throws Exception {

		Long movieIdNonExistents = 100L;
		postScoreInstance.put("movieId", movieIdNonExistents);

		String clientToken = TokenUtil.obtainAccessToken(usernameClient, passwordClient);
		ObjectMapper mapper = new ObjectMapper();

		String jsonBody = mapper.writeValueAsString(postScoreInstance);
		given()
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + clientToken)
				.body(jsonBody)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
		.when()
				.put("/scores")
		.then()
				.statusCode(404);

	}
	
	@Test
	public void saveScoreShouldReturnUnprocessableEntityWhenMissingMovieId() throws Exception {
		postScoreInstance.put("movieId", null);

		String clientToken = TokenUtil.obtainAccessToken(usernameClient, passwordClient);
		ObjectMapper mapper = new ObjectMapper();

		String jsonBody = mapper.writeValueAsString(postScoreInstance);
		given()
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + clientToken)
				.body(jsonBody)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
		.when()
				.put("/scores")
		.then()
				.statusCode(422);
	}
	
	@Test
	public void saveScoreShouldReturnUnprocessableEntityWhenScoreIsLessThanZero() throws Exception {
		postScoreInstance.put("score", -10.0);

		String clientToken = TokenUtil.obtainAccessToken(usernameClient, passwordClient);
		ObjectMapper mapper = new ObjectMapper();

		String jsonBody = mapper.writeValueAsString(postScoreInstance);
		given()
				.header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + clientToken)
				.body(jsonBody)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
		.when()
				.put("/scores")
		.then()
				.statusCode(422);
	}
}

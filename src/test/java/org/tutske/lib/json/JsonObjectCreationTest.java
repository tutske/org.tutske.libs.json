package org.tutske.lib.json;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThrows;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;


public class JsonObjectCreationTest {

	@Test
	public void it_should_create_json_objects_with_existing_nodes () throws IOException {
		ObjectMapper mapper = new ObjectMapper ();
		JsonNode node = mapper.readTree ("{ \"name\": \"john\" }");

		ObjectNode result = Json.objectNode ("name", node.get ("name"));

		assertThat (result.get ("name").asText (), is ("john"));
	}

	@Test
	public void it_should_complain_when_an_odd_number_of_argumetns_is_passed () {
		JsonException e = assertThrows (JsonException.class, () -> Json.objectNode ("key"));
		assertThat (e.getMessage (), containsString ("number of arguments"));
	}

	@Test
	public void it_should_created_nested_objects () {
		JsonNode created = Json.objectNode (
			"sub", Json.objectNode (
				"key", "value"
			)
		);

		assertThat (created.path ("sub").path ("key").asText (), is ("value"));
	}

	@Test
	public void it_should_create_object_node_from_a_map () {
		JsonNode node = Json.objectNode (Collections.singletonMap ("key", true));
		assertThat (node.get ("key"), is (BooleanNode.TRUE));
	}

	@Test
	public void it_should_create_json_objects_with_existing_nodes_with_mapper () throws IOException {
		ObjectMapper mapper = new ObjectMapper ();
		JsonNode node = mapper.readTree ("{ \"name\": \"john\" }");

		ObjectNode result = Json.objectNode (mapper, "name", node.get ("name"));

		assertThat (result.get ("name").asText (), is ("john"));
	}

	@Test
	public void it_should_complain_when_an_odd_number_of_argumetns_is_passed_with_mapper () {
		JsonException e = assertThrows (JsonException.class, () -> {
			Json.objectNode (new ObjectMapper (), "key");
		});
		assertThat (e.getMessage (), containsString ("number of arguments"));
	}

	@Test
	public void it_should_create_object_node_from_a_map_with_mapper () {
		JsonNode node = Json.objectNode (new ObjectMapper (), Collections.singletonMap ("key", true));
		assertThat (node.get ("key"), is (BooleanNode.TRUE));
	}

}

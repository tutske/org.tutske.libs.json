package org.tutske.lib.json;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;


public class JsonArrayCreationTest {

	@Test
	public void it_should_create_json_arrays_with_existing_nodes () throws IOException {
		ObjectMapper mapper = new ObjectMapper ();
		JsonNode node = mapper.readTree ("{ \"name\": \"john\" }");

		ArrayNode result = Json.arrayNode (node.get ("name"));

		assertThat (result.get (0).asText (), is ("john"));
	}

	@Test
	public void it_should_create_arrays_from_collections () {
		JsonNode node = Json.arrayNode (Arrays.asList (
			true, "entry", 12
		));

		assertThat (node.size (), is (3));

		assertThat (node.get (0), is (BooleanNode.TRUE));

		assertThat (node.get (1).isTextual (), is (true));
		assertThat (node.get (1).asText (), is ("entry"));

		assertThat (node.get (2).isNumber (), is (true));
		assertThat (node.get (2).intValue (), is (12));
	}

	@Test
	public void it_should_create_arrays_from_mapped_collections () {
		JsonNode node = Json.arrayNode (Arrays.asList (1, 2, 3), n -> n * 2);
		assertThat (node.size (), is (3));
		assertThat (node.get (0).intValue (), is (2));
	}

	@Test
	public void it_should_create_json_arrays_with_existing_nodes_with_mapper () throws IOException {
		ObjectMapper mapper = new ObjectMapper ();
		JsonNode node = mapper.readTree ("{ \"name\": \"john\" }");

		ArrayNode result = Json.arrayNode (mapper, node.get ("name"));

		assertThat (result.get (0).asText (), is ("john"));
	}

	@Test
	public void it_should_create_arrays_from_collections_mapper () {
		JsonNode node = Json.arrayNode (
			new ObjectMapper (), Arrays.asList (true, "entry", 12)
		);

		assertThat (node.size (), is (3));

		assertThat (node.get (0), is (BooleanNode.TRUE));

		assertThat (node.get (1).isTextual (), is (true));
		assertThat (node.get (1).asText (), is ("entry"));

		assertThat (node.get (2).isNumber (), is (true));
		assertThat (node.get (2).intValue (), is (12));
	}

	@Test
	public void it_should_create_arrays_from_mapped_collections_mapper () {
		JsonNode node = Json.arrayNode (new ObjectMapper (), Arrays.asList (1, 2, 3), n -> n * 2);
		assertThat (node.size (), is (3));
		assertThat (node.get (0).intValue (), is (2));
	}

}

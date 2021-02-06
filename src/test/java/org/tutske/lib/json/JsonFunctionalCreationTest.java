package org.tutske.lib.json;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.BooleanNode;
import org.junit.jupiter.api.Test;


public class JsonFunctionalCreationTest {

	@Test
	public void it_should_create_json () {
		JsonNode node = Json.json ((object, array) -> {
			return object.create (
				"key", array.create (true, 12)
			);
		});

		assertThat (node.get ("key").get (0), is (BooleanNode.TRUE));
	}

	@Test
	public void it_should_json_with_existing_nodes () throws Exception {
		ObjectMapper mapper = new ObjectMapper ();
		JsonNode node = mapper.readTree ("{ \"name\": \"john\" }");
		JsonNode created = Json.json ((object, array) -> {
			return object.create ("key", node);
		});

		assertThat (created.get ("key").get ("name").isTextual (), is (true));
	}

	@Test
	public void it_should_create_json_with_mapper () {
		JsonNode node = Json.json (new ObjectMapper (), (object, array) -> {
			return object.create (
				"key", array.create (true, 12)
			);
		});

		assertThat (node.get ("key").get (0), is (BooleanNode.TRUE));
	}

	@Test
	public void it_should_json_with_existing_nodes_with_mapper () throws Exception {
		ObjectMapper mapper = new ObjectMapper ();
		JsonNode node = mapper.readTree ("{ \"name\": \"john\" }");
		JsonNode created = Json.json (mapper, (object, array) -> {
			return object.create ("key", node);
		});

		assertThat (created.get ("key").get ("name").isTextual (), is (true));
	}

}

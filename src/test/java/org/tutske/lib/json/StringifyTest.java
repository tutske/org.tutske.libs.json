package org.tutske.lib.json;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThrows;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;

import java.io.IOException;


public class StringifyTest {

	@Test
	public void it_should_serialize_to_a_string () {
		ObjectNode data = Json.objectNode ("key", "value");
		String json = Json.stringify (new ObjectMapper (), data);
		assertThat (json, allOf (
			containsString ("{"),
			containsString ("\"key\""),
			containsString (":"),
			containsString ("\"value\""),
			containsString ("}")
		));
	}

	@Test
	public void it_should_serialize_to_a_string_with_a_writer () {
		ObjectNode data = Json.objectNode ("key", "value");
		String json = Json.stringify (new ObjectMapper ().writer (), data);
		assertThat (json, allOf (
			containsString ("{"),
			containsString ("\"key\""),
			containsString (":"),
			containsString ("\"value\""),
			containsString ("}")
		));
	}

	@Test
	public void it_should_serialize_to_a_string_without_a_mapper () {
		ObjectNode data = Json.objectNode ("key", "value");
		String json = Json.stringify (data);
		assertThat (json, allOf (
			containsString ("{"),
			containsString ("\"key\""),
			containsString (":"),
			containsString ("\"value\""),
			containsString ("}")
		));
	}

	@Test
	public void it_should_pretty_serialize_to_a_string () {
		ObjectNode data = Json.objectNode ("key", "value");
		String json = Json.prettyStringify (new ObjectMapper (), data);
		assertThat (json, allOf (
			containsString ("{\n"),
			containsString (" \"key\" : \"value\""),
			containsString ("\n}")
		));
	}

	@Test
	public void it_should_pretty_serialize_to_a_string_with_a_writer () {
		ObjectNode data = Json.objectNode ("key", "value");
		String json = Json.prettyStringify (new ObjectMapper ().writer (), data);
		assertThat (json, allOf (
			containsString ("{\n"),
			containsString (" \"key\" : \"value\""),
			containsString ("\n}")
		));
	}

	@Test
	public void it_should_pretty_serialize_to_a_string_without_a_mapper () {
		ObjectNode data = Json.objectNode ("key", "value");
		String json = Json.prettyStringify (data);
		assertThat (json, allOf (
			containsString ("{\n"),
			containsString (" \"key\" : \"value\""),
			containsString ("\n}")
		));
	}

	@Test
	public void it_should_parse_a_string_into_a_json_node () throws IOException {
		JsonNode node = Json.parse ("{ 'key': 'value' }".replaceAll ("'", "\""));
		assertThat (node.path ("key").isTextual (), is (true));
		assertThat (node.path ("key").asText (), is ("value"));
	}

	@Test
	public void it_should_parse_a_string_into_a_json_node_with_a_mapper () throws IOException {
		ObjectMapper mapper = new ObjectMapper ();
		JsonNode node = Json.parse (mapper, "{ 'key': 'value' }".replaceAll ("'", "\""));
		assertThat (node.path ("key").isTextual (), is (true));
		assertThat (node.path ("key").asText (), is ("value"));
	}

	@Test
	public void it_should_parse_a_string_into_a_json_node_with_an_object_reader () throws IOException {
		ObjectMapper mapper = new ObjectMapper ();
		JsonNode node = Json.parse (mapper.reader (), "{ 'key': 'value' }".replaceAll ("'", "\""));
		assertThat (node.path ("key").isTextual (), is (true));
		assertThat (node.path ("key").asText (), is ("value"));
	}

	@Test
	public void it_should_automatically_parse_to_objects () throws IOException {
		ObjectNode node = Json.parse ("{ 'key': 'value' }".replaceAll ("'", "\""));
		assertThat (node.path ("key").isTextual (), is (true));
		assertThat (node.path ("key").asText (), is ("value"));
	}

	@Test
	public void it_should_automatically_parse_to_arrays () throws IOException {
		ArrayNode arr = Json.parse (String.join ("\n",
			"[",
			"	{ 'id': 1, 'name': 'John Doe' },",
			"	{ 'id': 2, 'name': 'Jane Doe' },",
			"	{ 'id': 3, 'name': 'Jane Smith' },",
			"	{ 'id': 4, 'name': 'Jane Smith' }",
			"]"
		).replaceAll ("'", "\""));

		assertThat (arr.size (), is (4));
		assertThat (arr.path (1).path ("name").isTextual (), is (true));
		assertThat (arr.path (1).path ("name").asText (), is ("Jane Doe"));
	}

	@Test
	public void it_should_complain_about_parsing_errors_when_parsing_invalid_json () {
		assertThrows (JsonParseException.class, () -> {
			Json.parse ("{ 'open': 'object'".replaceAll ("'", "\""));
		});
	}

	@Test
	public void it_should_complain_about_parsing_errors_when_parsing_invalid_json_with_a_mapper() {
		assertThrows (JsonParseException.class, () -> {
			Json.parse (Mappers.mapper (), "{ 'open': 'object'".replaceAll ("'", "\""));
		});
	}

	@Test
	public void it_should_complain_about_parsing_errors_when_parsing_invalid_json_with_a_reader () {
		assertThrows (JsonParseException.class, () -> {
			Json.parse (Mappers.mapper ().reader (), "{ 'open': 'object'".replaceAll ("'", "\""));
		});
	}

	@Test
	public void it_should_propagate_io_exceptions_as_runtime_exceptions_when_serializing_with_object_mappers () {
		ObjectMapper mapper = Mappers.mapper (this::forceFailingSerialization);

		RuntimeException ex = assertThrows (RuntimeException.class, () -> {
			Json.stringify (mapper, new Object ());
		});

		assertThat (ex.getCause (), instanceOf (IOException.class));
	}

	@Test
	public void it_should_propagate_io_exceptions_as_runtime_exceptions_when_serializing_with_object_writers () {
		ObjectMapper mapper = Mappers.mapper (this::forceFailingSerialization);
		RuntimeException ex = assertThrows (RuntimeException.class, () -> {
			Json.stringify (mapper.writer (), new Object ());
		});

		assertThat (ex.getCause (), instanceOf (IOException.class));
	}

	private void forceFailingSerialization (SimpleModule module) {
		Mappers.serialize (module, Object.class, (value, gen, provider) -> {
			throw new IOException ("FORCE FAIL");
		});
	}

}

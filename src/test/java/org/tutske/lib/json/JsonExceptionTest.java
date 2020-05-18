package org.tutske.lib.json;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.tutske.lib.json.TestUtils.createConfiguredMapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;


public class JsonExceptionTest {

	@Test
	public void it_should_serialize_json_exceptions () throws JsonProcessingException {
		Exception exception = new JsonException ("Test", Json.objectNode ("key", "value"));
		JsonNode json = createConfiguredMapper ().valueToTree (exception);
		assertThat (json.path ("key").asText (), is ("value"));
	}

	@Test
	public void it_should_put_the_message_in_the_resulting_json () {
		Exception exception = new JsonException ("Test", Json.objectNode ("key", "value"));
		JsonNode json = createConfiguredMapper ().valueToTree (exception);
		assertThat (json.path ("error").asText (), is ("Test"));
	}

	@Test
	public void it_should_have_a_status_of_not_ok () {
		Exception exception = new JsonException ("Test", Json.objectNode ("key", "value"));
		JsonNode json = createConfiguredMapper ().valueToTree (exception);
		assertThat (json.path ("status").asText (), is ("nok"));
	}

	@Test
	public void it_should_have_an_error_property_even_without_error_message () {
		Exception exception = new JsonException ();
		JsonNode json = createConfiguredMapper ().valueToTree (exception);
		assertThat (json.has ("error"), is (true));
	}

	@Test
	public void it_should_serialize_appended_extra_data () {
		JsonException exception = new JsonException ("Test", Json.objectNode ("initial", "from start"));
		exception.addExtra (Json.objectNode ("extra", "appended later"));

		JsonNode json = createConfiguredMapper ().valueToTree (exception);

		assertThat (json.path ("initial").asText (), is ("from start"));
		assertThat (json.path ("extra").asText (), is ("appended later"));
	}

	@Test
	public void it_should_pick_error_from_the_extra_data () {
		JsonException exception = new JsonException (Json.objectNode ("error", "from extra"));
		JsonNode json = createConfiguredMapper ().valueToTree (exception);
		assertThat (json.path ("error").asText (), is ("from extra"));

	}

	@Test
	public void it_should_have_the_message_of_the_original_throwable () {
		Exception original = new RuntimeException ("original message");
		JsonException exception = new JsonException (original);
		JsonNode json = createConfiguredMapper ().valueToTree (exception);
		assertThat (json.path ("error").asText (), is ("original message"));
	}

	@Test
	public void it_should_prefer_the_passed_message_over_the_cause_message () {
		Exception original = new RuntimeException ("original message");
		JsonException exception = new JsonException ("custom message", original);
		JsonNode json = createConfiguredMapper ().valueToTree (exception);
		assertThat (json.path ("error").asText (), is ("custom message"));
	}

	@Test
	public void it_should_logically_copy_original_json_ecxeptions_message () {
		JsonException original = new JsonException ("original message");
		JsonException exception = new JsonException (original);
		JsonNode json = createConfiguredMapper ().valueToTree (exception);
		assertThat (json.path ("error").asText (), is ("original message"));
	}

	@Test
	public void it_should_keep_the_extra_data_of_the_original_json_exception () {
		JsonException original = new JsonException ("original message", Json.objectNode (
			"key", "original value"
		));

		JsonException exception = new JsonException (original);

		JsonNode json = createConfiguredMapper ().valueToTree (exception);
		assertThat (json.path ("key").asText (), is ("original value"));
	}

	@Test
	public void it_should_serialize_with_a_custom_configured_mapper () {
		ObjectMapper mapper = Mappers.configure (new ObjectMapper (), m -> {
			m.addSerializer (JsonException.class, new JsonException.JacksonSerializer ());
		});

		Exception exception = new JsonException ("Test", Json.objectNode ("key", "value"));
		JsonNode json = mapper.valueToTree (exception);

		assertThat (json.path ("status").asText (), is ("nok"));
		assertThat (json.path ("error").asText (), is ("Test"));
		assertThat (json.path ("key").asText (), is ("value"));
	}

	@Test
	public void it_should_serialize_with_a_method_configured_mapper () {
		ObjectMapper mapper = Mappers.configure (new ObjectMapper (), JsonException::configureJacksonMapper);

		Exception exception = new JsonException ("Test", Json.objectNode ("key", "value"));
		JsonNode json = mapper.valueToTree (exception);

		assertThat (json.path ("status").asText (), is ("nok"));
		assertThat (json.path ("error").asText (), is ("Test"));
		assertThat (json.path ("key").asText (), is ("value"));
	}

}

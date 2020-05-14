package org.tutske.lib.json;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;

import java.util.Collections;
import java.util.stream.Stream;


public class StreamIntegrationTest {

	@Test
	public void it_should_turn_a_stream_of_string_primitives_into_an_array_node () {
		ArrayNode result = Stream.of ("a", "b", "c", "d").collect (Json.collectToArray ());
		assertThat (result.size (), is (4));
		assertThat (result.path (0).isTextual (), is (true));
		assertThat (result.path (1).asText (), is ("b"));
	}

	@Test
	public void it_should_turn_a_stream_of_number_primitives_into_an_array_node () {
		ArrayNode result = Stream.of (1, 1L, 1.0, 1.0D).collect (Json.collectToArray ());
		assertThat (result.size (), is (4));
		assertThat (result.path (0).isIntegralNumber (), is (true));
		assertThat (result.path (1).isIntegralNumber (), is (true));
		assertThat (result.path (2).isFloatingPointNumber (), is (true));
		assertThat (result.path (3).isFloatingPointNumber (), is (true));
	}

	@Test
	public void it_should_turn_a_stream_of_maps_into_an_array_node () {
		ArrayNode result = Stream.of (
			Collections.singletonMap ("key", "one"),
			Collections.singletonMap ("key", "two"),
			Collections.singletonMap ("key", "three")
		).collect (Json.collectToArray ());

		assertThat (result.size (), is (3));
		assertThat (result.path (0).isContainerNode (), is (true));
		assertThat (result.path (1).isObject (), is (true));
		assertThat (result.path (2).path ("key").asText (), is ("three"));
	}

	@Test
	public void it_should_collect_stream_of_map_entries_to_an_object_node () {
		ObjectNode result = Collections.singletonMap ("key", "value")
			.entrySet ().stream ()
			.collect (Json.collectToObject ());

		assertThat (result.path ("key").asText (), is ("value"));
	}

	@Test
	public void it_should_collect_a_stream_of_things_to_an_object_node () {
		ObjectNode result = Stream.of (
			new User ("abc", "John Doe"),
			new User ("def", "Jane Doe"),
			new User ("ghi", "John Smith"),
			new User ("jkl", "Jane Smith")
		).collect (Json.collectToObject (u -> u.id, u -> u.name));

		assertThat (result.size (), is (4));
		assertThat (result.path ("def").asText (), is ("Jane Doe"));
	}

	@Test
	public void it_should_have_an_array_collector_that_combines () {
		ArrayNode result = Json.collectToArray ().combiner ().apply (
			Json.arrayNode ("a", "b"),
			Json.arrayNode ("c", "d")
		);

		assertThat (result.size (), is (4));
		assertThat (result.path (0).asText (), is ("a"));
		assertThat (result.path (3).asText (), is ("d"));
	}

	@Test
	public void it_should_have_an_object_collector_that_combines () {
		ObjectNode result = Json.collectToObject ().combiner ().apply (
			Json.objectNode ("a", "first"),
			Json.objectNode ("b", "second")
		);

		assertThat (result.path ("a").asText (), is ("first"));
		assertThat (result.path ("b").asText (), is ("second"));
	}

	@Test
	public void it_should_have_an_computing_object_collector_that_combines () {
		ObjectNode result = Json.<User>collectToObject (u -> u.id, u -> u).combiner ().apply (
			Json.objectNode ("abc", "John Doe"),
			Json.objectNode ("def", "Jane Doe")
		);

		assertThat (result.path ("abc").asText (), is ("John Doe"));
		assertThat (result.path ("def").asText (), is ("Jane Doe"));
	}

	private static class User {
		public final String id;
		public final String name;

		public User (String id, String name) {
			this.id = id;
			this.name = name;
		}
	}

}

package org.tutske.lib.json;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
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
	public void it_should_turn_a_stream_of_custom_types_into_an_array_node () {
		ObjectMapper mapper = Mappers.mapper (m -> {
			Mappers.serialize (m, User.class, this::serializeUser);
		});

		ArrayNode result = Stream.of (
			new User ("abc", "John Doe"),
			new User ("def", "Jane Doe"),
			new User ("ghi", "John Smith"),
			new User ("jkl", "Jane Smith")
		).collect (Json.collectToArray (mapper));

		assertThat (result.size (), is (4));
		assertThat (result.path (0).isContainerNode (), is (true));
		assertThat (result.path (1).isObject (), is (true));
		assertThat (result.path (2).path ("id").asText (), is ("ghi"));
	}

	@Test
	public void it_should_collect_stream_of_map_entries_to_an_object_node () {
		ObjectNode result = Collections.singletonMap ("key", "value")
			.entrySet ().stream ()
			.collect (Json.collectToObject ());

		assertThat (result.path ("key").asText (), is ("value"));
	}

	@Test
	public void it_should_collect_stream_of_map_entries_with_custom_types_to_an_object_node () {
		ObjectMapper mapper = Mappers.mapper (m -> {
			Mappers.serialize (m, User.class, this::serializeUser);
		});

		ObjectNode result = Collections.singletonMap ("owner", new User ("abc", "John Doe"))
			.entrySet ().stream ()
			.collect (Json.collectToObject (mapper));

		assertThat (result.has ("owner"), is (true));
		assertThat (result.path ("owner").isContainerNode (), is (true));
		assertThat (result.path ("owner").isObject (), is (true));
		assertThat (result.path ("owner").path ("name").asText (), is ("John Doe"));
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
	public void it_should_collect_a_stream_of_things_maped_to_custom_types_to_an_object_node () {
		ObjectMapper mapper = Mappers.mapper (m -> {
			Mappers.serialize (m, User.class, this::serializeUser);
		});

		ObjectNode result = Stream.of (
			new User ("abc", "John Doe"),
			new User ("def", "Jane Doe"),
			new User ("ghi", "John Smith"),
			new User ("jkl", "Jane Smith")
		).collect (Json.collectToObject (mapper, u -> u.id, Function.identity ()));

		assertThat (result.size (), is (4));
		assertThat (result.path ("def").isContainerNode (), is (true));
		assertThat (result.path ("def").isObject (), is (true));
		assertThat (result.path ("def").path ("name").asText (), is ("Jane Doe"));
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
	public void it_should_have_a_mapped_array_collector_that_combines () {
		ArrayNode result = Json.collectToArray (new ObjectMapper ()).combiner ().apply (
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
	public void it_should_have_a_mapped_object_collector_for_that_combines () {
		ObjectNode result = Json.collectToObject (new ObjectMapper ()).combiner ().apply (
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

	@Test
	public void it_should_have_a_mapped_computing_object_collector_that_combines () {
		ObjectNode result = Json.<User>collectToObject (new ObjectMapper (), u -> u.id, u -> u).combiner ().apply (
			Json.objectNode ("abc", "John Doe"),
			Json.objectNode ("def", "Jane Doe")
		);

		assertThat (result.path ("abc").asText (), is ("John Doe"));
		assertThat (result.path ("def").asText (), is ("Jane Doe"));
	}

	@Test
	public void it_should_turn_array_nodes_into_streams () {
		ArrayNode arr = Json.arrayNode (1, 2, 3, 4);
		List<Integer> numbers = Json.stream (arr).map (i -> i.intValue () * 2).collect (Collectors.toList ());
		assertThat (numbers, contains (2, 4, 6, 8));
	}

	@Test
	public void it_should_turn_arrays_of_objects_int_streams () {
		ArrayNode arr = Json.arrayNode (
			Json.objectNode ("name", "John"),
			Json.objectNode ("name", "Jane")
		);

		List<String> names = Json.stream (arr, ObjectNode.class)
			.map (o -> o.get ("name").asText ())
			.collect (Collectors.toList ());

		assertThat (names, contains ("John", "Jane"));
	}

	private static class User {
		public final String id;
		public final String name;

		public User (String id, String name) {
			this.id = id;
			this.name = name;
		}
	}

	private void serializeUser (User value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		gen.writeStartObject ();
		gen.writeObjectField ("id", value.id);
		gen.writeObjectField ("name", value.name);
		gen.writeEndObject ();
	}

}

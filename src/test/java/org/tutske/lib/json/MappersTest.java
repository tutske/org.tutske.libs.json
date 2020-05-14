package org.tutske.lib.json;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.Test;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;


public class MappersTest {

	@Test
	public void it_should_add_custom_serializers_to_a_mapper () {
		ObjectMapper mapper = Mappers.configure (new ObjectMapper (), module -> {
			Mappers.serialize (module, CustomEntity.class, (value, gen, provider) -> {
				gen.writeStartObject ();
				gen.writeObjectField ("__type__", "CustomEntity");
				gen.writeObjectField (String.valueOf (value.id), value.name);
				gen.writeEndObject ();
			});
		});

		JsonNode node = mapper.valueToTree (new CustomEntity (1, "John Doe"));
		assertThat (node.path ("__type__").asText (), is ("CustomEntity"));
	}

	@Test
	public void it_should_add_custom_serializers_to_a_default_mapper () {
		ObjectMapper mapper = Mappers.mapper (module -> {
			Mappers.serialize (module, CustomEntity.class, (value, gen, provider) -> {
				gen.writeStartObject ();
				gen.writeObjectField ("__type__", "CustomEntity");
				gen.writeObjectField (String.valueOf (value.id), value.name);
				gen.writeEndObject ();
			});
		});

		JsonNode node = mapper.valueToTree (new CustomEntity (1, "John Doe"));
		assertThat (node.path ("__type__").asText (), is ("CustomEntity"));
	}


	@Test
	public void it_should_allow_configuring_with_modules () {
		ObjectMapper mapper = Mappers.configure (new ObjectMapper (),
			new Jdk8Module (), new JavaTimeModule ()
		);

		Instant now = Instant.now ();
		JsonNode node = mapper.valueToTree (now);

		assertThat (node.isNumber (), is (true));
		assertThat (node.asLong (), is (now.getEpochSecond ()));
	}

	@Test
	public void it_should_create_a_mapper_with_modules () {
		ObjectMapper mapper = Mappers.mapper (new Jdk8Module (), new JavaTimeModule ());

		Instant now = Instant.now ();
		JsonNode node = mapper.valueToTree (now);

		assertThat (node.isTextual (), is (true));
		assertThat (node.asText (), containsString (DateTimeFormatter.ISO_INSTANT.format (now)));
	}

	@Test
	public void it_should_create_a_simple_module_with_serializers () {
		SimpleModule module = Mappers.module (m -> {
			Mappers.serialize (m, CustomEntity.class, (value, gen, provider) -> {
				gen.writeStartObject ();
				gen.writeObjectField ("__type__", "CustomEntity");
				gen.writeObjectField (String.valueOf (value.id), value.name);
				gen.writeEndObject ();
			});
		});

		ObjectMapper mapper = Mappers.mapper (module);

		JsonNode node = mapper.valueToTree (new CustomEntity (1, "John Doe"));
		assertThat (node.path ("__type__").asText (), is ("CustomEntity"));
	}

	@Test
	public void it_should_create_a_base_mapper_that_uses_handles_optionals () {
		ObjectMapper mapper = Mappers.mapper ();
		JsonNode node = mapper.valueToTree (Json.objectNode (
			"present", Optional.of ("value"),
			"absent", Optional.empty ()
		));

		assertThat (node.path ("present").isTextual (), is (true));
		assertThat (node.path ("present").asText (), is ("value"));
		assertThat (node.path ("absent").isNull (), is (true));
	}

	@Test
	public void it_should_create_a_base_mapper_that_uses_iso_date_time_formats () {
		ObjectMapper mapper = Mappers.mapper ();

		JsonNode node = mapper.valueToTree (Json.objectNode (
			"iat", LocalDateTime.of (2020, 11, 23, 16, 5).atZone (ZoneId.of ("UTC")).toInstant ()
		));

		assertThat (node.path ("iat").isTextual (), is (true));
		assertThat (node.path ("iat").asText (), containsString ("2020-11-23T16:05"));
	}

	@Test
	public void it_should_configure_modules_with_methods_that_return_something () {
		ObjectMapper mapper = Mappers.mapper (Mappers.module (this::addSerializers));
		JsonNode node = mapper.valueToTree (new CustomEntity (1, "John Doe"));
		assertThat (node.path ("__type__").asText (), is ("CustomEntity"));
	}

	public SimpleModule addSerializers (SimpleModule module) {
		Mappers.serialize (module, CustomEntity.class, (value, gen, provider) -> {
			gen.writeStartObject ();
			gen.writeObjectField ("__type__", "CustomEntity");
			gen.writeObjectField (String.valueOf (value.id), value.name);
			gen.writeEndObject ();
		});
		return module;
	}

	@Test
	public void it_should_allow_configuring_a_deserializer () throws IOException {
		ObjectMapper mapper = Mappers.configure (new ObjectMapper (), module -> {
			Mappers.deserialize (module, CustomEntity.class, (p, ctxt) -> (
				new CustomEntity (1, "John Doe")
			));
		});

		String json = "{ '__type__': 'CustomEntity', '1': 'John Doe' }".replaceAll ("'", "\"");
		CustomEntity entity = mapper.readValue (json, CustomEntity.class);

		assertThat (entity.id, is (1L));
		assertThat (entity.name, is ("John Doe"));
	}

	public static class CustomEntity {
		public final long id;
		public final String name;

		public CustomEntity (long id, String name) {
			this.id = id;
			this.name = name;
		}
	}

}

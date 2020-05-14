package org.tutske.lib.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.util.function.Consumer;


public class Mappers {

	static final ObjectMapper instance = mapper ();

	public static ObjectMapper mapper () {
		return new ObjectMapper ()
			.disable (SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
			.registerModule (new Jdk8Module ())
			.registerModule (new JavaTimeModule ())
			.registerModule (module (m -> m.addSerializer (
				JsonException.class,
				new JsonException.ResponseExceptionSerializer ()
			)))
		;
	}

	public static ObjectMapper mapper (Module ... modules) {
		return configure (mapper (), modules);
	}

	public static ObjectMapper mapper (Consumer<SimpleModule> ... configs) {
		return configure (mapper (), configs);
	}

	public static SimpleModule module (Consumer<SimpleModule> ... configs) {
		SimpleModule module = new SimpleModule ();
		for (Consumer<SimpleModule> config : configs ) { config.accept (module); }
		return module;
	}

	public static ObjectMapper configure (ObjectMapper mapper, Module ... modules) {
		for ( Module module : modules ) { mapper.registerModule (module); }
		return mapper;
	}

	public static ObjectMapper configure (ObjectMapper mapper, Consumer<SimpleModule> ... configs) {
		for ( Consumer<SimpleModule> config : configs ) {
			SimpleModule module = new SimpleModule ();
			config.accept (module);
			mapper.registerModule (module);
		}
		return mapper;
	}

	public static <T> void serialize (SimpleModule module, Class<T> clazz, SerializerFn<T> serializer) {
		module.addSerializer (clazz, new StdSerializer<T> (clazz) {
			@Override public void serialize (T value, JsonGenerator gen, SerializerProvider provider)
			throws IOException {
				serializer.serialize (value, gen, provider);
			}
		});
	}

	public static <T> void deserialize (SimpleModule module, Class<T> clazz, DeserializerFn<T> deserializer) {
		module.addDeserializer (clazz, new StdDeserializer<T> (clazz) {
			@Override public T deserialize (JsonParser p, DeserializationContext ctxt)
			throws IOException {
				return deserializer.deserialize (p, ctxt);
			}
		});
	}

	@FunctionalInterface
	public static interface SerializerFn<T> {
		public void serialize (T value, JsonGenerator gen, SerializerProvider provider) throws IOException;
	}

	@FunctionalInterface
	public static interface DeserializerFn<T> {
		public T deserialize (JsonParser p, DeserializationContext ctxt) throws IOException;
	}

}

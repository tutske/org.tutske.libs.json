package org.tutske.lib.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;


public class JsonException extends RuntimeException {

	protected final ObjectNode data = Json.objectNode ();

	public JsonException () {}
	public JsonException (String message) { super (message); }
	public JsonException (String message, Throwable cause) { super (message, cause); }
	public JsonException (Throwable cause) { super (cause.getMessage (), cause); }
	public JsonException (JsonException cause) {
		super (cause.getMessage (), cause);
		this.data.setAll (cause.data);
	}

	public JsonException (ObjectNode data) {
		this.data.setAll (data);
	}

	public JsonException (String message, ObjectNode data) {
		this (message);
		this.data.setAll (data);
	}

	public void addExtra (ObjectNode extra) {
		this.data.setAll (extra);
	}

	public static class JacksonSerializer extends StdSerializer<JsonException> {
		public JacksonSerializer () { super (JsonException.class); }

		@Override public void serialize (JsonException value, JsonGenerator gen, SerializerProvider provider)
		throws IOException {
			gen.writeStartObject ();
			gen.writeStringField ("status", "nok");
			gen.writeStringField ("error", value.getMessage ());

			Iterator<Map.Entry<String, JsonNode>> it = value.data.fields ();
			while ( it.hasNext () ) {
				Map.Entry<String, JsonNode> field = it.next ();
				gen.writeObjectField (field.getKey (), field.getValue ());
			}

			gen.writeEndObject ();
		}
	}

	public static void configureJacksonMapper (SimpleModule module) {
		module.addSerializer (JsonException.class, new JacksonSerializer ());
	}

}

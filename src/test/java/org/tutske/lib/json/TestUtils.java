package org.tutske.lib.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;


public class TestUtils {

	public static ObjectMapper createConfiguredMapper () {
		ObjectMapper mapper = new ObjectMapper ();
		SimpleModule module = new SimpleModule ();
		module.addSerializer (new JsonException.JacksonSerializer ());
		mapper.registerModule (module);
		return mapper;
	}

	public static void show (Object object) {
		try {
			ObjectWriter writer = createConfiguredMapper ().writerWithDefaultPrettyPrinter ();
			System.out.println ("json:\n" + writer.writeValueAsString (object));
		} catch ( IOException e ) {
			throw new RuntimeException (e);
		}
	}

}

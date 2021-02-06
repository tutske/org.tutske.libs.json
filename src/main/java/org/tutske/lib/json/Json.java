package org.tutske.lib.json;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


public class Json {

	public static interface JsonCreator<T extends JsonNode> {
		public T create (Object ... args);
	}

	public static <T> Collector<T, ArrayNode, ArrayNode> collectToArray () {
		return Collector.of (
			JsonNodeFactory.instance::arrayNode,
			(acc, curr) -> { acc.add (valueOf (curr)); },
			ArrayNode::addAll
		);
	}

	public static <T> Collector<T, ArrayNode, ArrayNode> collectToArray (ObjectMapper mapper) {
		return Collector.of (
			JsonNodeFactory.instance::arrayNode,
			(acc, curr) -> { acc.add (mapper.valueToTree (curr)); },
			ArrayNode::addAll
		);
	}

	public static Collector<Map.Entry<String, ?>, ObjectNode, ObjectNode> collectToObject () {
		return collectToObject (Map.Entry::getKey, Map.Entry::getValue);
	}

	public static Collector<Map.Entry<String, ?>, ObjectNode, ObjectNode> collectToObject (ObjectMapper mapper) {
		return collectToObject (mapper, Map.Entry::getKey, Map.Entry::getValue);
	}

	public static <T> Collector<T, ObjectNode, ObjectNode> collectToObject (Function<T, String> keyFn, Function<T, ?> valueFn) {
		return Collector.of (
			JsonNodeFactory.instance::objectNode,
			(acc, curr) -> acc.set (keyFn.apply (curr), valueOf (valueFn.apply (curr))),
			(l, r) -> (ObjectNode) l.setAll (r)
		);
	}

	public static <T> Collector<T, ObjectNode, ObjectNode> collectToObject (
		ObjectMapper mapper, Function<T, String> keyFn, Function<T, ?> valueFn
	) {
		return Collector.of (
			JsonNodeFactory.instance::objectNode,
			(acc, curr) -> acc.set (keyFn.apply (curr), mapper.valueToTree (valueFn.apply (curr))),
			(l, r) -> (ObjectNode) l.setAll (r)
		);
	}

	public static Stream<JsonNode> stream (JsonNode node) {
		return StreamSupport.stream (node.spliterator (), false);
	}

	public static <T extends JsonNode> Stream<T> stream (JsonNode node, Class<T> clazz) {
		return StreamSupport.stream (node.spliterator (), false).map (clazz::cast);
	}

	public static ObjectNode objectNode (Object ... args) {
		if ( (args.length & 1) != 0 ) {
			String msg = "Can only create object node from even number of arguments";

			ObjectNode data = JsonNodeFactory.instance.objectNode ();
			data.put ("length", args.length);
			data.putPOJO ("arguments", args);

			throw new JsonException (msg, data);
		}

		int i = 0;
		ObjectNode node = JsonNodeFactory.instance.objectNode ();
		while ( i < args.length ) {
			node.set ((String) args[i++], valueOf (args[i++]));
		}

		return node;
	}

	public static ObjectNode objectNode (ObjectMapper mapper, Object ... args) {
		if ( (args.length & 1) != 0 ) {
			String msg = "Can only create object node from even number of arguments";

			ObjectNode data = JsonNodeFactory.instance.objectNode ();
			data.put ("length", args.length);
			data.putPOJO ("arguments", args);

			throw new JsonException (msg, data);
		}

		int i = 0;
		ObjectNode node = JsonNodeFactory.instance.objectNode ();
		while ( i < args.length ) {
			node.set ((String) args[i++], mapper.valueToTree (args[i++]));
		}

		return node;
	}

	public static ObjectNode objectNode (Map<String, ?> map) {
		ObjectNode node = JsonNodeFactory.instance.objectNode ();
		for ( Map.Entry<String, ?> entry : map.entrySet () ) {
			node.set (entry.getKey (), valueOf (entry.getValue ()));
		}
		return node;
	}

	public static ObjectNode objectNode (ObjectMapper mapper, Map<String, ?> map) {
		ObjectNode node = JsonNodeFactory.instance.objectNode ();
		for ( Map.Entry<String, ?> entry : map.entrySet () ) {
			node.set (entry.getKey (), mapper.valueToTree (entry.getValue ()));
		}
		return node;
	}

	public static ArrayNode arrayNode (Object ... args) {
		ArrayNode node = JsonNodeFactory.instance.arrayNode ();
		for ( Object arg : args ) { node.add (valueOf (arg)); }
		return node;
	}

	public static ArrayNode arrayNode (ObjectMapper mapper, Object ... args) {
		ArrayNode node = JsonNodeFactory.instance.arrayNode ();
		for ( Object arg : args ) { node.add (mapper.valueToTree (arg)); }
		return node;
	}

	public static ArrayNode arrayNode (Collection<?> args) {
		ArrayNode node = JsonNodeFactory.instance.arrayNode ();
		for ( Object arg : args ) { node.add (valueOf (arg)); }
		return node;
	}

	public static <T> ArrayNode arrayNode (Collection<T> args, Function<T, ?> fn) {
		ArrayNode node = JsonNodeFactory.instance.arrayNode ();
		for ( T arg : args ) { node.add (valueOf (fn.apply (arg))); }
		return node;
	}

	public static ArrayNode arrayNode (ObjectMapper mapper, Collection<?> args) {
		ArrayNode node = JsonNodeFactory.instance.arrayNode ();
		for ( Object arg : args ) { node.add (mapper.valueToTree (arg)); }
		return node;
	}

	public static <T> ArrayNode arrayNode (ObjectMapper mapper, Collection<T> args, Function<T, ?> fn) {
		ArrayNode node = JsonNodeFactory.instance.arrayNode ();
		for ( T arg : args ) { node.add (mapper.valueToTree (fn.apply (arg))); }
		return node;
	}

	public static <T> T json (BiFunction<JsonCreator<ObjectNode>, JsonCreator<ArrayNode>, T> fn) {
		return fn.apply (Json::objectNode, Json::arrayNode);
	}

	public static <T> T json (ObjectMapper mapper, BiFunction<JsonCreator<ObjectNode>, JsonCreator<ArrayNode>, T> fn) {
		return fn.apply (
			args -> objectNode (mapper, args),
			args -> arrayNode (mapper, args)
		);
	}

	public static boolean contains (JsonNode node, JsonNode entry) {
		if ( node.isArray () ) { return contains ((ArrayNode) node, entry); }
		throw new JsonException ("Can only find in arrays",
			objectNode ("json", node)
		);
	}

	public static boolean contains (ArrayNode node, JsonNode entry) {
		for ( JsonNode candidate : node ) {
			if ( candidate.equals (entry) ) { return true; }
		}
		return false;
	}

	public static <T> List<T> toList (JsonNode node, Function<JsonNode, T> fn) {
		if ( node.isArray () ) { return toList ((ArrayNode) node, fn); }
		throw new JsonException	("Can only convert arrays to lists",
			objectNode ("json", node)
		);
	}

	public static <T> List<T> toList (ArrayNode node, Function<JsonNode, T> fn) {
		List<T> results = new LinkedList<> ();
		node.forEach (element -> results.add (fn.apply (element)));
		return results;
	}

	public static ArrayNode map (JsonNode node, Function<JsonNode, JsonNode> fn) {
		if ( node.isArray () ) { return map ((ArrayNode) node, fn); }
		throw new JsonException ("Can only map over arrays",
			objectNode ("json", node)
		);
	}

	public static ArrayNode map (ArrayNode in, Function<JsonNode, JsonNode> fn) {
		ArrayNode newArray = JsonNodeFactory.instance.arrayNode ();
		in.forEach (node -> newArray.add (fn.apply (node)));
		return newArray;
	}

	public static ArrayNode mapObjects (JsonNode in, Function<ObjectNode, JsonNode> fn) {
		if ( in.isArray () ) { return mapObjects ((ArrayNode) in, fn); }
		throw new JsonException ("Can only map over arrays",
			objectNode ("json", in)
		);
	}

	public static ArrayNode mapObjects (ArrayNode in, Function<ObjectNode, JsonNode> fn) {
		return map (in, el -> {
			if ( el.isObject () ) { return fn.apply ((ObjectNode) el); }
			throw new JsonException ("Mapping objects encountered a non object node",
				objectNode (
					"type", el.getNodeType (),
					"element", el,
					"json", in
				)
			);
		});
	}

	public static ArrayNode filter (JsonNode node, Predicate<JsonNode> fn) {
		if ( node.isArray () ) { return filter ((ArrayNode) node, fn); }
		throw new JsonException ("Can only filter arrays",
			objectNode ("json", node)
		);
	}

	public static ArrayNode filter (ArrayNode array, Predicate<JsonNode> fn) {
		ArrayNode result = JsonNodeFactory.instance.arrayNode ();
		array.forEach (el -> {
			if ( fn.test (el) ) {
				result.add (el);
			}
		});
		return result;
	}

	public static ArrayNode filterObjects (JsonNode node, Predicate<ObjectNode> fn) {
		if ( node.isArray () ) { return filterObjects ((ArrayNode) node, fn); }
		throw new JsonException ("Can only filter arrays",
			objectNode ("json", node)
		);
	}

	public static ArrayNode filterObjects (ArrayNode array, Predicate<ObjectNode> fn) {
		return filter (array, el -> {
			if ( el.isObject () ) { return fn.test ((ObjectNode) el); }
			throw new JsonException ("Filtering objects encountered a non object node",
				objectNode (
					"type", el.getNodeType (),
					"element", el,
					"json", array
				)
			);
		});
	}

	public static JsonNode find (JsonNode node, Predicate<JsonNode> fn) {
		if ( node.isArray () ) { return find ((ArrayNode) node, fn); }
		throw new JsonException ("Can only find in arrays", objectNode ("json", node));
	}

	public static JsonNode find (ArrayNode array, Predicate<JsonNode> fn) {
		int size = array.size ();
		for ( int i = 0; i < size; i++ ) {
			JsonNode node = array.get (i);
			if ( fn.test (node) ) { return node; }
		}

		return MissingNode.getInstance ();
	}

	public static ObjectNode findObject (JsonNode node, Predicate<ObjectNode> fn) {
		if ( node.isArray () ) { return findObject ((ArrayNode) node, fn); }
		throw new JsonException ("Can only find in arrays", objectNode ("json", node));
	}

	public static ObjectNode findObject (ArrayNode array, Predicate<ObjectNode> fn) {
		return (ObjectNode) find (array, el -> {
			if ( el.isObject () ) { return fn.test ((ObjectNode) el); }
			throw new JsonException ("Finding an object encountered a non object node",
				objectNode (
					"type", el.getNodeType (),
					"element", el,
					"json", array
				)
			);
		});
	}

	public static ArrayNode concat (ArrayNode ... arrays) {
		ArrayNode result = JsonNodeFactory.instance.arrayNode ();
		for ( ArrayNode array : arrays ) { result.addAll (array); }
		return result;
	}

	public static <T> T reduce (JsonNode node, T initial, BiFunction<T, JsonNode, T> fn) {
		if ( node.isArray () ) { return reduce ((ArrayNode) node, initial, fn); }
		throw new JsonException ("Can only reduce arrays", objectNode ("json", node));
	}

	public static <T> T reduce (ArrayNode source, T initial, BiFunction<T, JsonNode, T> fn) {
		T value = initial;
		for ( JsonNode node : source ) {
			value = fn.apply (value, node);
		}
		return value;
	}

	public static ObjectNode keep (JsonNode node, String ... keys) {
		if ( node.isObject () ) { return keep ((ObjectNode) node, keys); }
		throw new JsonException ("Can select keys from objects", objectNode ("json", node));
	}

	public static ObjectNode keep (ObjectNode obj, String... keys) {
		ObjectNode copy = JsonNodeFactory.instance.objectNode ();
		for ( String key : keys ) {
			if ( obj.has (key) ) { copy.set (key, obj.get (key)); }
		}
		return copy;
	}

	public static ObjectNode purge (JsonNode node, String ... keys) {
		if ( node.isObject () ) { return purge ((ObjectNode) node, keys); }
		throw new JsonException ("Can only purge keys from objects", objectNode ("json", node));
	}

	public static ObjectNode purge (ObjectNode obj, String ... keys) {
		ObjectNode copy = JsonNodeFactory.instance.objectNode ();
		obj.fields ().forEachRemaining (field -> copy.set (field.getKey (), field.getValue ()));
		for ( String key : keys ) { copy.remove (key); }
		return copy;
	}

	public static ObjectNode merge (ObjectNode target, ObjectNode ... sources) {
		for ( ObjectNode source : sources ) {
			if ( source == null || source.isNull () || source.isMissingNode () ) { continue; }
			target.setAll (source);
		}
		return target;
	}

	public static ObjectNode mergeAbsent (ObjectNode target, ObjectNode ... sources) {
		for ( ObjectNode source : sources ) {
			if ( source == null || source.isNull () || source.isMissingNode () ) { continue; }
			source.fields ().forEachRemaining (field -> {
				if ( target.has (field.getKey ()) ) { return; }
				target.set (field.getKey (), field.getValue ());
			});
		}
		return target;
	}

	public static ObjectNode computeIfAbsent (JsonNode target, String key, BiFunction<ObjectNode, String, JsonNode> fn) {
		if ( target.isObject () ) { return computeIfAbsent ((ObjectNode) target, key, fn); }
		throw new JsonException ("Can only compute mising values on objects", objectNode ("json", target));
	}

	public static ObjectNode computeIfAbsent (ObjectNode target, String key, BiFunction<ObjectNode, String, JsonNode> fn) {
		if ( ! target.has (key) ) {
			target.set (key, fn.apply (target, key));
		}
		return target;
	}

	public static JsonNode purgeNulls (JsonNode node) {
		if ( node.isObject () ) { return purgeNulls ((ObjectNode) node); }
		if ( node.isArray () ) { return purgeNulls ((ArrayNode) node); }
		throw new JsonException ("Can only purge nulls from object and array nodes",
			objectNode ("type", node.getNodeType (), "json", node)
		);
	}

	public static ObjectNode purgeNulls (ObjectNode node) {
		Set<String> properties = new HashSet<> ();

		node.fields ().forEachRemaining (field -> {
			if ( field.getValue ().isNull () ) {
				properties.add (field.getKey ());
			}
		});

		if ( properties.isEmpty () ) { return node; }

		ObjectNode copy = JsonNodeFactory.instance.objectNode ();
		copy.setAll (node);
		properties.forEach (copy::remove);

		return copy;
	}

	public static ArrayNode purgeNulls (ArrayNode node) {
		ArrayNode copy = JsonNodeFactory.instance.arrayNode ();
		node.forEach (child -> {
			if ( child != null && ! child.isNull () ) {
				copy.add (child);
			}
		});
		return copy.size () == node.size () ? node : copy;
	}

	public static <T> Map<String, T> toMap (JsonNode node, BiFunction<String, JsonNode, T> fn) {
		if ( node.isObject () ) { return toMap ((ObjectNode) node, fn); }
		throw new JsonException ("Can only turn objects into maps", objectNode ("json", node));
	}

	public static <T> Map<String, T> toMap (ObjectNode node, BiFunction<String, JsonNode, T> fn) {
		Map<String, T> result = new LinkedHashMap<> ();
		node.fields ().forEachRemaining (field -> {
			result.put (field.getKey (), fn.apply (field.getKey (), field.getValue ()));
		});
		return result;
	}

	public static JsonNode valueOf (JsonNode node) {
		return node == null ? JsonNodeFactory.instance.nullNode () : node;
	}

	public static JsonNode valueOf (String value) {
		return (
			value == null ? JsonNodeFactory.instance.nullNode () :
			JsonNodeFactory.instance.textNode (value)
		);
	}

	public static JsonNode valueOf (BigInteger value) {
		return (
			value == null ? JsonNodeFactory.instance.nullNode () :
			JsonNodeFactory.instance.numberNode (value)
		);
	}

	public static JsonNode valueOf (BigDecimal value) {
		return (
			value == null ? JsonNodeFactory.instance.nullNode () :
			JsonNodeFactory.instance.numberNode (value)
		);
	}

	public static JsonNode valueOf (byte [] bytes) {
		return (
			bytes == null ? JsonNodeFactory.instance.nullNode () :
			JsonNodeFactory.instance.binaryNode (bytes)
		);
	}

	public static JsonNode valueOf (Collection<?> value) {
		return (
			value == null ? JsonNodeFactory.instance.nullNode () :
			arrayNode (value)
		);
	}

	public static JsonNode valueOf (Map<String, ?> value) {
		return (
			value == null ? JsonNodeFactory.instance.nullNode () :
			objectNode (value)
		);
	}

	public static JsonNode valueOf (short value) { return JsonNodeFactory.instance.numberNode (value); }
	public static JsonNode valueOf (byte value) { return JsonNodeFactory.instance.numberNode (value); }
	public static JsonNode valueOf (int value) { return JsonNodeFactory.instance.numberNode (value); }
	public static JsonNode valueOf (long value) { return JsonNodeFactory.instance.numberNode (value); }
	public static JsonNode valueOf (float value) { return JsonNodeFactory.instance.numberNode (value); }
	public static JsonNode valueOf (double value) { return JsonNodeFactory.instance.numberNode (value); }
	public static JsonNode valueOf (boolean value) { return JsonNodeFactory.instance.booleanNode (value); }

	public static JsonNode valueOf (Object value) {
		if ( value == null ) { return JsonNodeFactory.instance.nullNode (); }
		if ( value instanceof JsonNode ) { return (JsonNode) value; }

		if ( value instanceof String ) { return valueOf ((String) value); }
		if ( value instanceof Integer ) { return valueOf ((int) value); }
		if ( value instanceof Long) { return valueOf ((long) value); }
		if ( value instanceof Float) { return valueOf ((float) value); }
		if ( value instanceof Double) { return valueOf ((double) value); }
		if ( value instanceof Boolean ) { return valueOf ((boolean) value); }

		if ( value instanceof BigInteger ) { return valueOf ((BigInteger) value); }
		if ( value instanceof BigDecimal ) { return valueOf ((BigDecimal) value); }
		if ( value instanceof Byte) { return valueOf ((byte) value); }
		if ( value instanceof Short) { return valueOf ((short) value); }
		if ( value instanceof byte [] ) { return valueOf ((byte []) value); }

		if ( value instanceof Map ) { return objectNode ((Map) value); }
		if ( value instanceof Collection ) { return arrayNode ((Collection) value); }

		return JsonNodeFactory.instance.pojoNode (value);
	}

	public static String stringify (Object node) {
		return stringify (Mappers.instance, node);
	}

	public static String stringify (ObjectMapper mapper, Object node) {
		try { return mapper.writeValueAsString (node); }
		catch ( IOException e ) { throw new RuntimeException (e); }
	}

	public static String stringify (ObjectWriter writer, Object node) {
		try { return writer.writeValueAsString (node); }
		catch ( IOException e ) { throw new RuntimeException (e); }
	}

	public static String prettyStringify (Object node) {
		return prettyStringify (Mappers.instance, node);
	}

	public static String prettyStringify (ObjectMapper mapper, Object node) {
		return stringify (mapper.writerWithDefaultPrettyPrinter (), node);
	}

	public static String prettyStringify (ObjectWriter writer, Object node) {
		return stringify (writer.withDefaultPrettyPrinter (), node);
	}

	public static <T extends JsonNode> T parse (String json)
	throws JsonParseException {
		return parse (Mappers.instance, json);
	}

	public static <T extends JsonNode> T parse (ObjectMapper mapper, String json)
	throws JsonParseException {
		try { return (T) mapper.readTree (json); }
		catch (JsonParseException e ) { throw e; }
		catch (IOException e ) { throw new RuntimeException (e); }
	}

	public static <T extends JsonNode> T parse (ObjectReader reader, String json)
	throws JsonParseException {
		try { return (T) reader.readTree (json); }
		catch (JsonParseException e ) { throw e; }
		catch (IOException e ) { throw new RuntimeException (e); }
	}

}

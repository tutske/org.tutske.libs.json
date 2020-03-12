package org.tutske.lib.json;

import static org.tutske.lib.json.Json.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;


public class Validate {

	public static void assureNonNulls (JsonNode ... nodes) {
		for ( JsonNode node : nodes ) { assureNonNull (node); }
	}

	public static void assureNonNull (JsonNode node) {
		if ( node == null || node.isNull ()) throw new JsonException (
			"Required non null json element",
			objectNode (
				"original", node,
				"jsonNull", node != null && node.isNull ()
			)
		);
	}

	public static void assureArrays (JsonNode ... nodes) {
		for ( JsonNode node : nodes ) { assureArray (node); }
	}

	public static void assureArray (JsonNode node) {
		assureNonNull (node);
		if ( ! node.isArray () ) throw new JsonException (
			"Required an array, but got something else.",
			objectNode ("original", node)
		);
	}

	public static void assureObjects (JsonNode ... nodes) {
		for ( JsonNode node : nodes ) { assureObject (node); }
	}

	public static void assureObject (JsonNode node) {
		assureNonNull (node);
		if ( ! node.isObject () ) throw new JsonException (
			"Required an object, but got something else.",
			objectNode ("original", node)
		);
	}

	public static void assurePrimitiveValues (JsonNode ... nodes) {
		for ( JsonNode node : nodes ) { assurePrimitiveValue (node); }
	}

	public static void assurePrimitiveValue (JsonNode node) {
		assureNonNull (node);
		if ( node.isContainerNode () ) throw new JsonException (
			"Required a json primitive, but got something else.",
			objectNode ("original", node)
		);
	}

	public static void assureFields (JsonNode node, String ... fields) {
		assureObject (node);
		assureFields ((ObjectNode) node, fields);
	}

	public static void assureFields (ObjectNode obj, String ... fields) {
		for ( String field : fields ) {
			if ( ! obj.has (field) || obj.get (field).isNull () ) throw new JsonException (
				"Found a field that is missing.",
				objectNode (
					"original", obj,
					"fields", fields,
					"field", field
				)
			);
		}
	}

	public static void assureAbsence (JsonNode node, String ... fields) {
		assureObject (node);
		assureAbsence ((ObjectNode) node, fields);
	}

	public static void assureAbsence (ObjectNode obj, String ... fields) {
		for ( String field : fields ) {
			if ( obj.has (field) ) throw new JsonException (
				"Found a field that should have been absent",
				objectNode (
					"original", obj,
					"fields", fields,
					"field", field
				)
			);
		}
	}

	public static void assurePrimitiveFields (JsonNode node, String ... fields) {
		assureObject (node);
		assurePrimitiveFields ((ObjectNode) node, fields);
	}

	public static void assurePrimitiveFields (ObjectNode obj, String ... fields) {
		assureFields (obj, fields);
		for ( String field : fields ) {
			if ( obj.get (field).isContainerNode () ) throw new JsonException (
				"Found a fields that should have been a json primitive.",
				objectNode (
					"original", obj,
					"fields", fields,
					"field", field,
					"value", obj.get (field)
				)
			);
		}
	}

	public static void assureNonEmptyStrings (JsonNode node, String ... fields) {
		assureObject (node);
		assureNonEmptyStrings ((ObjectNode) node, fields);
	}

	public static void assureNonEmptyStrings (ObjectNode obj, String ... fields) {
		assurePrimitiveFields (obj, fields);
		for ( String field : fields ) {
			JsonNode node = obj.path (field);
			String value = node.asText ();
			if ( ! node.isTextual () || value.isEmpty () ) throw new JsonException (
				"Found a field that should have been a non empty string.",
				objectNode (
					"original", obj,
					"fields", fields,
					"field", field,
					"value", obj.get (field)
				)
			);
		}
	}

}

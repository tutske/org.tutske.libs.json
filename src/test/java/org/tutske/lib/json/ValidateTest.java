package org.tutske.lib.json;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.BigIntegerNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.DecimalNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.FloatNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.math.BigDecimal;
import java.math.BigInteger;


public class ValidateTest {

	@Test
	public void it_should_complain_about_null_values () {
		assertValidation ("non null", () -> Validate.assureNonNull (null));
		assertValidation ("non null", () -> Validate.assureNonNull (NullNode.getInstance ()));
	}

	@Test
	public void it_should_not_complain_about_regular_json_nodes () {
		Validate.assureNonNull (Json.objectNode ());
		Validate.assureNonNull (Json.arrayNode ());
		Validate.assureNonNull (BooleanNode.TRUE);
		Validate.assureNonNull (BooleanNode.FALSE);
		Validate.assureNonNull (new TextNode ("text"));
		Validate.assureNonNull (new IntNode (3));
		Validate.assureNonNull (new LongNode (3));
		Validate.assureNonNull (new FloatNode (3.0F));
		Validate.assureNonNull (new DoubleNode (3.0D));
		Validate.assureNonNull (new DecimalNode (BigDecimal.ONE));
		Validate.assureNonNull (new BigIntegerNode (BigInteger.ONE));
	}

	@Test
	public void it_should_complain_when_any_node_is_null_value () {
		assertValidation ("non null", () -> Validate.assureNonNulls (Json.objectNode (), null));
		assertValidation ("non null", () -> Validate.assureNonNulls (Json.objectNode (), NullNode.getInstance ()));
	}

	@Test
	public void it_should_not_complain_about_any_of_the_regular_json_nodes () {
		Validate.assureNonNulls (
			Json.objectNode (),
			Json.arrayNode (),
			BooleanNode.TRUE,
			BooleanNode.FALSE,
			new TextNode ("text"),
			new IntNode (3),
			new LongNode (3),
			new FloatNode (3.0F),
			new DoubleNode (3.0D),
			new DecimalNode (BigDecimal.ONE),
			new BigIntegerNode (BigInteger.ONE)
		);
	}

	@Test
	public void it_should_complain_when_not_getting_an_array () {
		assertValidation ("non null", () -> Validate.assureArray (null));
		assertValidation ("non null", () -> Validate.assureArray (NullNode.getInstance ()));
		assertValidation ("Required an array", () -> Validate.assureArray (Json.objectNode ()));
		assertValidation ("Required an array", () -> Validate.assureArray (BooleanNode.TRUE));
		assertValidation ("Required an array", () -> Validate.assureArray (BooleanNode.FALSE));
		assertValidation ("Required an array", () -> Validate.assureArray (new TextNode ("text")));
		assertValidation ("Required an array", () -> Validate.assureArray (new IntNode (3)));
		assertValidation ("Required an array", () -> Validate.assureArray (new LongNode (3)));
		assertValidation ("Required an array", () -> Validate.assureArray (new FloatNode (3.0F)));
		assertValidation ("Required an array", () -> Validate.assureArray (new DoubleNode (3.0D)));
		assertValidation ("Required an array", () -> Validate.assureArray (new DecimalNode (BigDecimal.ONE)));
		assertValidation ("Required an array", () -> Validate.assureArray (new BigIntegerNode (BigInteger.ONE)));
	}

	@Test
	public void it_should_not_complain_when_getting_an_array () {
		Validate.assureArray (Json.arrayNode ());
	}

	@Test
	public void it_should_complain_when_getting_some_values_that_are_not_arrays () {
		assertValidation ("non null", () -> Validate.assureArrays (Json.arrayNode (), null));
		assertValidation ("non null", () -> Validate.assureArrays (Json.arrayNode (), NullNode.getInstance ()));
		assertValidation ("Required an array", () -> Validate.assureArrays (Json.arrayNode (), Json.objectNode ()));
		assertValidation ("Required an array", () -> Validate.assureArrays (Json.arrayNode (), BooleanNode.TRUE));
		assertValidation ("Required an array", () -> Validate.assureArrays (Json.arrayNode (), BooleanNode.FALSE));
		assertValidation ("Required an array", () -> Validate.assureArrays (Json.arrayNode (), new TextNode ("text")));
		assertValidation ("Required an array", () -> Validate.assureArrays (Json.arrayNode (), new IntNode (3)));
		assertValidation ("Required an array", () -> Validate.assureArrays (Json.arrayNode (), new LongNode (3)));
		assertValidation ("Required an array", () -> Validate.assureArrays (Json.arrayNode (), new FloatNode (3.0F)));
		assertValidation ("Required an array", () -> Validate.assureArrays (Json.arrayNode (), new DoubleNode (3.0D)));
		assertValidation ("Required an array", () -> Validate.assureArrays (Json.arrayNode (), new DecimalNode (BigDecimal.ONE)));
		assertValidation ("Required an array", () -> Validate.assureArrays (Json.arrayNode (), new BigIntegerNode (BigInteger.ONE)));
	}

	@Test
	public void it_should_not_complain_when_getting_only_arrays () {
		Validate.assureArrays (Json.arrayNode (0), Json.arrayNode (1), Json.arrayNode (3));
	}

	@Test
	public void it_should_complain_when_not_getting_an_object () {
		assertValidation ("non null", () -> Validate.assureObject (null));
		assertValidation ("non null", () -> Validate.assureObject (NullNode.getInstance ()));
		assertValidation ("Required an object", () -> Validate.assureObject (Json.arrayNode ()));
		assertValidation ("Required an object", () -> Validate.assureObject (BooleanNode.TRUE));
		assertValidation ("Required an object", () -> Validate.assureObject (BooleanNode.FALSE));
		assertValidation ("Required an object", () -> Validate.assureObject (new TextNode ("text")));
		assertValidation ("Required an object", () -> Validate.assureObject (new IntNode (3)));
		assertValidation ("Required an object", () -> Validate.assureObject (new LongNode (3)));
		assertValidation ("Required an object", () -> Validate.assureObject (new FloatNode (3.0F)));
		assertValidation ("Required an object", () -> Validate.assureObject (new DoubleNode (3.0D)));
		assertValidation ("Required an object", () -> Validate.assureObject (new DecimalNode (BigDecimal.ONE)));
		assertValidation ("Required an object", () -> Validate.assureObject (new BigIntegerNode (BigInteger.ONE)));
	}

	@Test
	public void it_should_not_complain_when_getting_an_object () {
		Validate.assureObject (Json.objectNode ("key", "value"));
	}

	@Test
	public void it_should_complain_when_getting_some_values_that_are_not_objects () {
		assertValidation ("non null", () -> Validate.assureObjects (Json.objectNode (), null));
		assertValidation ("non null", () -> Validate.assureObjects (Json.objectNode (), NullNode.getInstance ()));
		assertValidation ("Required an object", () -> Validate.assureObjects (Json.objectNode (), Json.arrayNode ()));
		assertValidation ("Required an object", () -> Validate.assureObjects (Json.objectNode (), BooleanNode.TRUE));
		assertValidation ("Required an object", () -> Validate.assureObjects (Json.objectNode (), BooleanNode.FALSE));
		assertValidation ("Required an object", () -> Validate.assureObjects (Json.objectNode (), new TextNode ("text")));
		assertValidation ("Required an object", () -> Validate.assureObjects (Json.objectNode (), new IntNode (3)));
		assertValidation ("Required an object", () -> Validate.assureObjects (Json.objectNode (), new LongNode (3)));
		assertValidation ("Required an object", () -> Validate.assureObjects (Json.objectNode (), new FloatNode (3.0F)));
		assertValidation ("Required an object", () -> Validate.assureObjects (Json.objectNode (), new DoubleNode (3.0D)));
		assertValidation ("Required an object", () -> Validate.assureObjects (Json.objectNode (), new DecimalNode (BigDecimal.ONE)));
		assertValidation ("Required an object", () -> Validate.assureObjects (Json.objectNode (), new BigIntegerNode (BigInteger.ONE)));
	}

	@Test
	public void it_should_not_complain_when_getting_only_objects () {
		Validate.assureObjects (Json.objectNode (), Json.objectNode ("key", "value"));
	}

	@Test
	public void it_should_complain_when_not_getting_a_primitive_value () {
		String msg = "Required a json primitive";
		assertValidation ("non null", () -> Validate.assurePrimitiveValue (null));
		assertValidation ("non null", () -> Validate.assurePrimitiveValue (NullNode.getInstance ()));
		assertValidation (msg, () -> Validate.assurePrimitiveValue (Json.arrayNode ()));
		assertValidation (msg, () -> Validate.assurePrimitiveValue (Json.objectNode ()));
	}

	@Test
	public void it_should_not_complain_when_getting_a_primitive () {
		Validate.assurePrimitiveValue (BooleanNode.TRUE);
		Validate.assurePrimitiveValue (BooleanNode.FALSE);
		Validate.assurePrimitiveValue (new TextNode ("text"));
		Validate.assurePrimitiveValue (new IntNode (3));
		Validate.assurePrimitiveValue (new LongNode (3));
		Validate.assurePrimitiveValue (new FloatNode (3.0F));
		Validate.assurePrimitiveValue (new DoubleNode (3.0D));
		Validate.assurePrimitiveValue (new DecimalNode (BigDecimal.ONE));
		Validate.assurePrimitiveValue (new BigIntegerNode (BigInteger.ONE));
	}

	@Test
	public void it_should_complain_when_getting_some_values_that_are_not_primitives () {
		String msg = "Required a json primitive";
		JsonNode TRUE = BooleanNode.TRUE;
		assertValidation ("non null", () -> Validate.assurePrimitiveValues (TRUE, null));
		assertValidation ("non null", () -> Validate.assurePrimitiveValues (TRUE, NullNode.getInstance ()));
		assertValidation (msg, () -> Validate.assurePrimitiveValues (TRUE, Json.arrayNode ()));
		assertValidation (msg, () -> Validate.assurePrimitiveValues (TRUE, Json.objectNode ()));
	}

	@Test
	public void it_should_not_complain_when_getting_only_primitives () {
		Validate.assurePrimitiveValues (BooleanNode.TRUE, BooleanNode.TRUE);
		Validate.assurePrimitiveValues (BooleanNode.TRUE, BooleanNode.FALSE);
		Validate.assurePrimitiveValues (BooleanNode.TRUE, new TextNode ("text"));
		Validate.assurePrimitiveValues (BooleanNode.TRUE, new IntNode (3));
		Validate.assurePrimitiveValues (BooleanNode.TRUE, new LongNode (3));
		Validate.assurePrimitiveValues (BooleanNode.TRUE, new FloatNode (3.0F));
		Validate.assurePrimitiveValues (BooleanNode.TRUE, new DoubleNode (3.0D));
		Validate.assurePrimitiveValues (BooleanNode.TRUE, new DecimalNode (BigDecimal.ONE));
		Validate.assurePrimitiveValues (BooleanNode.TRUE, new BigIntegerNode (BigInteger.ONE));
	}

	@Test
	public void it_should_complain_when_an_objects_is_missing_fields () {
		assertValidation ("missing", () -> Validate.assurePrimitiveFields (
			Json.objectNode (), "key"
		));
	}

	@Test
	public void it_should_complain_when_an_objects_is_missing_some_fields () {
		assertValidation ("missing", () -> Validate.assurePrimitiveFields (
			Json.objectNode ("key", "value", "second", "value"),
			"key", "not_there", "second"
		));
	}

	@Test
	public void it_should_complain_when_an_objects_has_null_values_in_required_primitive_fields () {
		assertValidation ("missing", () -> Validate.assurePrimitiveFields (
			Json.objectNode ("key", NullNode.instance), "key"
		));
		assertValidation ("missing", () -> Validate.assurePrimitiveFields (
			Json.objectNode ("key", null), "key"
		));
	}

	@Test
	public void it_should_complain_when_an_objects_has_non_primitives_in_required_primitive_fields () {
		assertValidation ("json primitive", () -> Validate.assurePrimitiveFields (
			Json.objectNode ("key", Json.objectNode ()), "key"
		));
		assertValidation ("json primitive", () -> Validate.assurePrimitiveFields (
			Json.objectNode ("key", Json.arrayNode ()), "key"
		));
	}

	@Test
	public void it_should_not_complain_when_all_fields_are_there_and_primitives () {
		Validate.assurePrimitiveFields (
			Json.objectNode ("key", "value", "second", "value"),
			"key", "second"
		);
	}

	@Test
	public void it_should_complain_when_checking_primitive_fields_on_a_node () {
		JsonNode node = Json.objectNode ("key", "value", "second", "value");
		assertValidation ("missing", () -> Validate.assurePrimitiveFields (node, "key", "not_there"));
	}

	@Test
	public void it_should_not_complain_when_all_primitive_fields_are_present_on_a_node () {
		JsonNode node = Json.objectNode ("key", "value", "second", "value");
		Validate.assurePrimitiveFields (node, "key", "second");
	}

	@Test
	public void it_should_complain_checking_primitive_fields_on_a_non_object () {
		String msg = "Required an object";
		assertValidation ("non null", () -> Validate.assurePrimitiveFields ((JsonNode) null, "key"));
		assertValidation ("non null", () -> Validate.assurePrimitiveFields (NullNode.getInstance (), "key"));
		assertValidation (msg, () -> Validate.assurePrimitiveFields (Json.arrayNode (), "key"));
		assertValidation (msg, () -> Validate.assurePrimitiveFields (BooleanNode.TRUE, "key"));
		assertValidation (msg, () -> Validate.assurePrimitiveFields (BooleanNode.FALSE, "key"));
		assertValidation (msg, () -> Validate.assurePrimitiveFields (new TextNode ("text"), "key"));
		assertValidation (msg, () -> Validate.assurePrimitiveFields (new IntNode (3), "key"));
		assertValidation (msg, () -> Validate.assurePrimitiveFields (new LongNode (3), "key"));
		assertValidation (msg, () -> Validate.assurePrimitiveFields (new FloatNode (3.0F), "key"));
		assertValidation (msg, () -> Validate.assurePrimitiveFields (new DoubleNode (3.0D), "key"));
		assertValidation (msg, () -> Validate.assurePrimitiveFields (new DecimalNode (BigDecimal.ONE), "key"));
		assertValidation (msg, () -> Validate.assurePrimitiveFields (new BigIntegerNode (BigInteger.ONE), "key"));
	}

	@Test
	public void it_should_not_complain_when_all_fields_are_present_on_a_node () {
		JsonNode node = Json.objectNode ("key", "value", "second", "value");
		Validate.assureFields (node, "key", "second");
	}

	@Test
	public void it_should_complain_checking_any_fields_on_a_non_object () {
		String msg = "Required an object";
		assertValidation ("non null", () -> Validate.assureFields ((JsonNode) null, "key"));
		assertValidation ("non null", () -> Validate.assureFields (NullNode.getInstance (), "key"));
		assertValidation (msg, () -> Validate.assureFields (Json.arrayNode (), "key"));
		assertValidation (msg, () -> Validate.assureFields (BooleanNode.TRUE, "key"));
		assertValidation (msg, () -> Validate.assureFields (BooleanNode.FALSE, "key"));
		assertValidation (msg, () -> Validate.assureFields (new TextNode ("text"), "key"));
		assertValidation (msg, () -> Validate.assureFields (new IntNode (3), "key"));
		assertValidation (msg, () -> Validate.assureFields (new LongNode (3), "key"));
		assertValidation (msg, () -> Validate.assureFields (new FloatNode (3.0F), "key"));
		assertValidation (msg, () -> Validate.assureFields (new DoubleNode (3.0D), "key"));
		assertValidation (msg, () -> Validate.assureFields (new DecimalNode (BigDecimal.ONE), "key"));
		assertValidation (msg, () -> Validate.assureFields (new BigIntegerNode (BigInteger.ONE), "key"));
	}

	@Test
	public void it_should_complain_when_containing_banned_field () {
		assertValidation ("should have been absent", () -> Validate.assureAbsence (
			Json.objectNode ("key", "value"), "key"
		));
	}

	@Test
	public void it_should_complain_when_containing_some_banned_field () {
		assertValidation ("should have been absent", () -> Validate.assureAbsence (
			Json.objectNode ("key", "value", "allowed", "value"),
			"key", "banned"
		));
	}

	@Test
	public void it_should_complain_when_any_json_contains_some_banned_fields () {
		JsonNode node = Json.objectNode ("key", "value", "allowed", "value");
		assertValidation ("should have been absent", () -> Validate.assureAbsence (node, "key", "banned"));
	}

	@Test
	public void it_should_not_complain_when_not_containing_banned_fields () {
		Validate.assureAbsence (Json.objectNode ("allowed", "value"), "banned");
	}

	@Test
	public void it_should_not_complain_when_any_json_does_not_contain_banned_fields () {
		JsonNode node = Json.objectNode ("allowed", "value");
		Validate.assureAbsence (node, "banned");
	}

	@Test
	public void it_should_complain_when_banning_fields_on_non_objects () {
		String msg = "Required an object";
		assertValidation ("non null", () -> Validate.assureAbsence ((JsonNode) null, "key"));
		assertValidation ("non null", () -> Validate.assureAbsence (NullNode.getInstance (), "key"));
		assertValidation (msg, () -> Validate.assureAbsence (Json.arrayNode (), "key"));
		assertValidation (msg, () -> Validate.assureAbsence (BooleanNode.TRUE, "key"));
		assertValidation (msg, () -> Validate.assureAbsence (BooleanNode.FALSE, "key"));
		assertValidation (msg, () -> Validate.assureAbsence (new TextNode ("text"), "key"));
		assertValidation (msg, () -> Validate.assureAbsence (new IntNode (3), "key"));
		assertValidation (msg, () -> Validate.assureAbsence (new LongNode (3), "key"));
		assertValidation (msg, () -> Validate.assureAbsence (new FloatNode (3.0F), "key"));
		assertValidation (msg, () -> Validate.assureAbsence (new DoubleNode (3.0D), "key"));
		assertValidation (msg, () -> Validate.assureAbsence (new DecimalNode (BigDecimal.ONE), "key"));
		assertValidation (msg, () -> Validate.assureAbsence (new BigIntegerNode (BigInteger.ONE), "key"));
	}

	@Test
	public void it_should_complain_when_containing_non_empty_string_field () {
		assertValidation ("non empty string", () -> Validate.assureNonEmptyStrings (
			Json.objectNode ("key", Boolean.TRUE), "key"
		));
	}

	@Test
	public void it_should_complain_when_not_containing_non_empty_string_field () {
		assertValidation ("missing", () -> Validate.assureNonEmptyStrings (
			Json.objectNode ("key", "value"), "missing"
		));
	}

	@Test
	public void it_should_complain_when_containing_some_non_strings_field () {
		assertValidation ("non empty string", () -> Validate.assureNonEmptyStrings (
			Json.objectNode ("correct", "value", "faulty", BooleanNode.TRUE),
			"correct", "faulty"
		));
	}

	@Test
	public void it_should_complain_when_any_json_contains_some_non_strings_field () {
		JsonNode node = Json.objectNode ("correct", "value", "faulty", BooleanNode.TRUE);
		assertValidation ("non empty string", () -> Validate.assureNonEmptyStrings (
			node, "correct", "faulty"
		));
	}

	@Test
	public void it_should_complain_when_containing_some_empty_strings_field () {
		assertValidation ("non empty string", () -> Validate.assureNonEmptyStrings (
			Json.objectNode ("correct", "value", "faulty", ""),
			"correct", "faulty"
		));
	}

	@Test
	public void it_should_complain_when_any_json_contain_some_empty_strings_field () {
		JsonNode node = Json.objectNode ("correct", "value", "faulty", "");
		assertValidation ("non empty string", () -> Validate.assureNonEmptyStrings (
			node, "correct", "faulty"
		));
	}

	@Test
	public void it_should_complain_when_missing_required_non_empty_string_fields () {
		assertValidation ("missing", () -> Validate.assureNonEmptyStrings (
			Json.objectNode ("correct", "value"),
			"correct", "not_there"
		));
	}

	@Test
	public void it_should_complain_when_any_json_misses_required_non_empty_string_fields () {
		JsonNode node = Json.objectNode ("correct", "value");
		assertValidation ("missing", () -> Validate.assureNonEmptyStrings (
			node, "correct", "not_there"
		));
	}

	@Test
	public void it_should_complain_when_checking_non_empty_strings_fields_on_non_objects () {
		String msg = "Required an object";
		assertValidation ("non null", () -> Validate.assureNonEmptyStrings ((JsonNode) null, "key"));
		assertValidation ("non null", () -> Validate.assureNonEmptyStrings (NullNode.getInstance (), "key"));
		assertValidation (msg, () -> Validate.assureNonEmptyStrings (Json.arrayNode (), "key"));
		assertValidation (msg, () -> Validate.assureNonEmptyStrings (BooleanNode.TRUE, "key"));
		assertValidation (msg, () -> Validate.assureNonEmptyStrings (BooleanNode.FALSE, "key"));
		assertValidation (msg, () -> Validate.assureNonEmptyStrings (new TextNode ("text"), "key"));
		assertValidation (msg, () -> Validate.assureNonEmptyStrings (new IntNode (3), "key"));
		assertValidation (msg, () -> Validate.assureNonEmptyStrings (new LongNode (3), "key"));
		assertValidation (msg, () -> Validate.assureNonEmptyStrings (new FloatNode (3.0F), "key"));
		assertValidation (msg, () -> Validate.assureNonEmptyStrings (new DoubleNode (3.0D), "key"));
		assertValidation (msg, () -> Validate.assureNonEmptyStrings (new DecimalNode (BigDecimal.ONE), "key"));
		assertValidation (msg, () -> Validate.assureNonEmptyStrings (new BigIntegerNode (BigInteger.ONE), "key"));
	}

	@Test
	public void it_should_not_complain_when_all_non_empty_string_fields_are_correct () {
		Validate.assureNonEmptyStrings (Json.objectNode ("key", "value"), "key");
	}

	@Test
	public void it_should_not_complain_when_all_non_empty_string_fields_are_correct_for_any_json () {
		JsonNode node = Json.objectNode ("key", "value");
		Validate.assureNonEmptyStrings (node, "key");
	}

	private void assertValidation (String contained, Executable runnable) {
		JsonException e = assertThrows (JsonException.class, runnable);
		assertThat (e.getMessage (), containsString (contained));
	}

}

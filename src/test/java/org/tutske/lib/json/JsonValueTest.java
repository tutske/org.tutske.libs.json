package org.tutske.lib.json;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;


public class JsonValueTest {

	@Test
	public void it_should_turn_null_into_null_node () {
		assertThat (Json.valueOf ((Object) null).isNull (), is (true));
		assertThat (Json.valueOf ((String) null).isNull (), is (true));
		assertThat (Json.valueOf ((JsonNode) null).isNull (), is (true));
		assertThat (Json.valueOf ((BigInteger) null).isNull (), is (true));
		assertThat (Json.valueOf ((BigDecimal) null).isNull (), is (true));
		assertThat (Json.valueOf ((byte []) null).isNull (), is (true));
	}

	@Test
	public void it_should_turn_json_nodes_into_themselves () {
		JsonNode node = Json.objectNode ();
		assertThat (Json.valueOf (node), is (node));
		assertThat (Json.valueOf ((Object) node), is (node));
	}

	@Test
	public void it_should_turn_strings_into_textual_nodes () {
		assertThat (Json.valueOf ("Text").isTextual (), is (true));
		assertThat (Json.valueOf ((Object) "Text").isTextual (), is (true));
	}

	@Test
	public void it_should_turn_integers_into_number_nodes () {
		assertThat (Json.valueOf (1).isNumber (), is (true));
		assertThat (Json.valueOf (1).isIntegralNumber (), is (true));
		assertThat (Json.valueOf ((Object) 1).isNumber (), is (true));
		assertThat (Json.valueOf ((Object) 1).isIntegralNumber (), is (true));
	}

	@Test
	public void it_should_turn_longs_into_number_nodes () {
		assertThat (Json.valueOf (1L).isNumber (), is (true));
		assertThat (Json.valueOf (1L).isIntegralNumber (), is (true));
		assertThat (Json.valueOf ((Object) 1L).isNumber (), is (true));
		assertThat (Json.valueOf ((Object) 1L).isIntegralNumber (), is (true));
	}

	@Test
	public void it_should_turn_floats_into_number_nodes () {
		assertThat (Json.valueOf (1.1F).isNumber (), is (true));
		assertThat (Json.valueOf (1.1F).isIntegralNumber (), is (false));
		assertThat (Json.valueOf ((Object) 1.1F).isNumber (), is (true));
		assertThat (Json.valueOf ((Object) 1.1F).isIntegralNumber (), is (false));
	}

	@Test
	public void it_should_turn_doubles_into_number_nodes () {
		assertThat (Json.valueOf (1.1D).isNumber (), is (true));
		assertThat (Json.valueOf (1.1D).isIntegralNumber (), is (false));
		assertThat (Json.valueOf ((Object) 1.1D).isNumber (), is (true));
		assertThat (Json.valueOf ((Object) 1.1D).isIntegralNumber (), is (false));
	}

	@Test
	public void it_should_booleans_into_boolean_nodes () {
		assertThat (Json.valueOf (true).isBoolean (), is (true));
		assertThat (Json.valueOf (false).isBoolean (), is (true));
		assertThat (Json.valueOf (true).booleanValue (), is (true));
		assertThat (Json.valueOf (false).booleanValue (), is (false));

		assertThat (Json.valueOf ((Object) true).isBoolean (), is (true));
		assertThat (Json.valueOf ((Object) false).isBoolean (), is (true));
		assertThat (Json.valueOf ((Object) true).booleanValue (), is (true));
		assertThat (Json.valueOf ((Object) false).booleanValue (), is (false));
	}

	@Test
	public void it_should_turn_big_integers_into_number_nodes () {
		BigInteger n = new BigInteger ("" + Long.MAX_VALUE + "12");
		assertThat (Json.valueOf (n).isNumber (), is (true));
		assertThat (Json.valueOf (n).isIntegralNumber (), is (true));
		assertThat (Json.valueOf (n).isBigInteger (), is (true));

		assertThat (Json.valueOf ((Object) n).isNumber (), is (true));
		assertThat (Json.valueOf ((Object) n).isIntegralNumber (), is (true));
		assertThat (Json.valueOf ((Object) n).isBigInteger (), is (true));
	}

	@Test
	public void it_should_turn_big_decimal_into_number_nodes () {
		BigDecimal n = new BigDecimal ("" + Long.MAX_VALUE + "12.5");
		assertThat (Json.valueOf (n).isNumber (), is (true));
		assertThat (Json.valueOf (n).isIntegralNumber (), is (false));
		assertThat (Json.valueOf (n).isBigDecimal (), is (true));

		assertThat (Json.valueOf ((Object) n).isNumber (), is (true));
		assertThat (Json.valueOf ((Object) n).isIntegralNumber (), is (false));
		assertThat (Json.valueOf ((Object) n).isBigDecimal (), is (true));
	}

	@Test
	public void it_should_turn_bytes_into_number_nodes () {
		assertThat (Json.valueOf ((byte) 0xa1).isNumber (), is (true));
		assertThat (Json.valueOf ((byte) 0xa1).isIntegralNumber (), is (true));
		assertThat (Json.valueOf ((Object) (byte) 0xa1).isNumber (), is (true));
		assertThat (Json.valueOf ((Object) (byte) 0xa1).isIntegralNumber (), is (true));
	}

	@Test
	public void it_should_turn_shorts_into_number_nodes () {
		assertThat (Json.valueOf ((short) 0xa1).isNumber (), is (true));
		assertThat (Json.valueOf ((short) 0xa1).isIntegralNumber (), is (true));
		assertThat (Json.valueOf ((Object) (short) 0xa1).isNumber (), is (true));
		assertThat (Json.valueOf ((Object) (short) 0xa1).isIntegralNumber (), is (true));
	}

	@Test
	public void it_should_turn_byte_arrays_into_binary_nodes () {
		byte [] bytes = new byte [] { (byte) 0xa1, (byte) 0xb1, (byte) 0x1c };
		assertThat (Json.valueOf (bytes).isBinary (), is (true));
		assertThat (Json.valueOf ((Object) bytes).isBinary (), is (true));
	}

}

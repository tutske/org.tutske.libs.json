package org.tutske.lib.json;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThrows;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.function.Function;


public class JsonAlterTest {

	@Test
	public void it_should_check_if_an_array_contains_an_element () {
		JsonNode node = Json.objectNode ();
		ArrayNode arr = Json.arrayNode (1, node, 2);

		assertThat (Json.contains (arr, node), is (true));
	}

	@Test
	public void it_should_check_if_a_json_node_array_contains_an_element () {
		JsonNode node = Json.objectNode ();
		ArrayNode arr = Json.arrayNode (1, node, 2);

		assertThat (Json.contains ((JsonNode) arr, node), is (true));
	}

	@Test
	public void it_should_say_similar_objects_are_contained () {
		ArrayNode arr = Json.arrayNode (1, Json.objectNode (), 2);
		assertThat (Json.contains (arr, Json.objectNode ()), is (true));
	}

	@Test
	public void it_should_say_similar_objects_are_contained_in_a_json_node_array () {
		ArrayNode arr = Json.arrayNode (1, Json.objectNode (), 2);
		assertThat (Json.contains ((JsonNode) arr, Json.objectNode ()), is (true));
	}

	@Test
	public void it_should_say_when_an_array_does_not_contain_an_element () {
		ArrayNode arr = Json.arrayNode (1, 2, 3);
		assertThat (Json.contains (arr, Json.valueOf (4)), is (false));
	}

	@Test
	public void it_should_say_when_a_json_node_array_does_not_contain_an_element () {
		ArrayNode arr = Json.arrayNode (1, 2, 3);
		assertThat (Json.contains ((JsonNode) arr, Json.valueOf (4)), is (false));
	}

	@Test
	public void it_should_complain_when_checking_containment_in_no_array_nodes () {
		JsonException ex = assertThrows (JsonException.class, () -> {
			Json.contains (Json.valueOf (1), Json.valueOf (1));
		});
		assertThat (ex.getMessage (), containsString ("array"));
	}

	@Test
	public void it_should_map_over_an_array () {
		ArrayNode node = Json.arrayNode (1, 2, 3, 4);
		ArrayNode result = Json.map (node, el -> (
			Json.valueOf (el.intValue () * 2)
		));

		assertThat (result.size (), is (4));
		assertThat (result.get (0).intValue (), is (2));
		assertThat (result.get (3).intValue (), is (8));
	}

	@Test
	public void it_should_map_over_any_json () {
		ArrayNode node = Json.arrayNode (1, 2, 3, 4);
		ArrayNode result = Json.map ((JsonNode) node, el -> (
			Json.valueOf (el.intValue () * 2)
		));

		assertThat (result.size (), is (4));
		assertThat (result.get (0).intValue (), is (2));
		assertThat (result.get (3).intValue (), is (8));
	}

	@Test
	public void it_should_complain_when_mapping_over_non_arrays () {
		JsonException ex = assertThrows (JsonException.class, () -> {
			Json.map (Json.objectNode (), Function.identity ());
		});
		assertThat (ex.getMessage (), containsString ("array"));
	}

	@Test
	public void it_should_map_over_an_array_of_objects () {
		ArrayNode arr = Json.arrayNode (
			Json.objectNode ("key", "first"),
			Json.objectNode ("key", "second"),
			Json.objectNode ("key", "third")
		);

		ArrayNode result = Json.mapObjects (arr, obj -> Json.objectNode ("key", "fixed"));

		assertThat (result.size (), is (3));
		assertThat (result.get (0), is (Json.objectNode ("key", "fixed")));
		assertThat (result.get (1), is (Json.objectNode ("key", "fixed")));
		assertThat (result.get (2), is (Json.objectNode ("key", "fixed")));
	}

	@Test
	public void it_should_map_over_any_json_array_of_objects () {
		ArrayNode arr = Json.arrayNode (
			Json.objectNode ("key", "first"),
			Json.objectNode ("key", "second"),
			Json.objectNode ("key", "third")
		);

		ArrayNode result = Json.mapObjects ((JsonNode) arr, obj -> Json.objectNode ("key", "fixed"));

		assertThat (result.size (), is (3));
		assertThat (result.get (0), is (Json.objectNode ("key", "fixed")));
		assertThat (result.get (1), is (Json.objectNode ("key", "fixed")));
		assertThat (result.get (2), is (Json.objectNode ("key", "fixed")));
	}

	@Test
	public void it_should_complain_when_mapping_objects_over_non_arrays () {
		JsonException ex = assertThrows (JsonException.class, () -> {
			Json.mapObjects (Json.objectNode (), obj -> obj);
		});
		assertThat (ex.getMessage (), containsString ("array"));
	}

	@Test
	public void it_should_complain_when_mapping_over_an_array_with_non_objects () {
		JsonException ex = assertThrows (JsonException.class, () -> {
			Json.mapObjects (Json.arrayNode (true), obj -> obj);
		});
		assertThat (ex.getMessage (), containsString ("non object node"));
	}

	@Test
	public void it_should_filter_arrays () {
		ArrayNode arr = Json.arrayNode (1, 2, 3, 4, 5);
		ArrayNode result = Json.filter (arr, el -> el.intValue () % 2 == 0);
		assertThat (result.size (), is (2));
		assertThat (result.get (0).intValue (), is (2));
		assertThat (result.get (1).intValue (), is (4));
	}

	@Test
	public void it_should_filter_any_json_arrays () {
		ArrayNode arr = Json.arrayNode (1, 2, 3, 4, 5);
		ArrayNode result = Json.filter ((JsonNode) arr, el -> el.intValue () % 2 == 0);
		assertThat (result.size (), is (2));
		assertThat (result.get (0).intValue (), is (2));
		assertThat (result.get (1).intValue (), is (4));
	}

	@Test
	public void it_should_complain_when_filtering_non_arrays () {
		JsonException ex = assertThrows (JsonException.class, () -> {
			Json.filter (Json.objectNode (), el -> el.intValue () % 2 == 0);
		});
		assertThat (ex.getMessage (), containsString ("array"));
	}

	@Test
	public void it_should_filter_over_an_array_of_objects () {
		ArrayNode arr = Json.arrayNode (
			Json.objectNode ("key", "first"),
			Json.objectNode ("KEY", "second"),
			Json.objectNode ("key", "third")
		);
		ArrayNode result = Json.filterObjects (arr, obj -> obj.has ("KEY"));

		assertThat (result.size (), is (1));
		assertThat (result.get (0), is (Json.objectNode ("KEY", "second")));
	}

	@Test
	public void it_should_filter_over_any_json_array_of_objects () {
		ArrayNode arr = Json.arrayNode (
			Json.objectNode ("key", "first"),
			Json.objectNode ("KEY", "second"),
			Json.objectNode ("key", "third")
		);
		ArrayNode result = Json.filterObjects ((JsonNode) arr, obj -> obj.has ("KEY"));

		assertThat (result.size (), is (1));
		assertThat (result.get (0), is (Json.objectNode ("KEY", "second")));
	}

	@Test
	public void it_should_complain_when_filtering_objects_over_non_arrays () {
		JsonException ex = assertThrows (JsonException.class, () -> {
			Json.filterObjects (Json.objectNode (), obj -> false);
		});
		assertThat (ex.getMessage (), containsString ("array"));
	}

	@Test
	public void it_should_complain_when_filtering_over_an_array_with_non_objects () {
		JsonException ex = assertThrows (JsonException.class, () -> {
			Json.filterObjects (Json.arrayNode (true), obj -> false);
		});
		assertThat (ex.getMessage (), containsString ("non object node"));
	}

	@Test
	public void it_should_find_the_first_matching_node_in_an_array () {
		ArrayNode arr = Json.arrayNode (
			Json.objectNode ("key", 1, "value", "first"),
			Json.objectNode ("key", 2, "value", "second"),
			Json.objectNode ("key", 3, "value", "third")
		);

		JsonNode found = Json.find (arr, el -> el.get ("key").intValue () == 2);

		assertThat (found, is (arr.get (1)));
	}

	@Test
	public void it_should_find_the_first_matching_node_in_any_json_array () {
		ArrayNode arr = Json.arrayNode (
			Json.objectNode ("key", 1, "value", "first"),
			Json.objectNode ("key", 2, "value", "second"),
			Json.objectNode ("key", 3, "value", "third")
		);

		JsonNode found = Json.find ((JsonNode) arr, el -> el.get ("key").intValue () == 2);

		assertThat (found, is (arr.get (1)));
	}

	@Test
	public void it_should_give_a_missing_node_when_nothing_is_found_in_array () {
		JsonNode found = Json.find (Json.arrayNode (), obj -> false);
		assertThat (found.isMissingNode (), is (true));
	}

	@Test
	public void it_should_complain_when_finding_anything_in_non_arrays () {
		JsonException ex = assertThrows (JsonException.class, () -> {
			Json.find (Json.objectNode (), obj -> true);
		});
		assertThat (ex.getMessage (), containsString ("array"));
	}

	@Test
	public void it_should_find_the_first_object_in_an_array () {
		ArrayNode arr = Json.arrayNode (
			Json.objectNode ("key", 1, "value", "first"),
			Json.objectNode ("key", 2, "value", "second"),
			Json.objectNode ("key", 3, "value", "third")
		);

		ObjectNode found = Json.findObject (arr, el -> el.get ("key").intValue () == 2);

		assertThat (found, is (arr.get (1)));
	}

	@Test
	public void it_should_find_the_first_object_in_any_json_array () {
		ArrayNode arr = Json.arrayNode (
			Json.objectNode ("key", 1, "value", "first"),
			Json.objectNode ("key", 2, "value", "second"),
			Json.objectNode ("key", 3, "value", "third")
		);

		ObjectNode found = Json.findObject ((JsonNode) arr, el -> el.get ("key").intValue () == 2);

		assertThat (found, is (arr.get (1)));
	}

	@Test
	public void it_should_complain_when_finding_objects_in_non_arrays () {
		JsonException ex = assertThrows (JsonException.class, () -> {
			Json.findObject (Json.objectNode (), obj -> true);
		});
		assertThat (ex.getMessage (), containsString ("array"));
	}

	@Test
	public void it_should_complain_when_finding_objects_in_arrays_with_non_object_elements () {
		JsonException ex = assertThrows (JsonException.class, () -> {
			Json.findObject (Json.arrayNode (true), obj -> true);
		});
		assertThat (ex.getMessage (), containsString ("non object node"));
	}

	@Test
	public void it_should_concat_two_arrays () {
		ArrayNode result = Json.concat (
			Json.arrayNode (1, 2),
			Json.arrayNode (3, 4),
			Json.arrayNode (5, 6)
		);

		assertThat (result.size (), is (6));
		assertThat (result.get (0).intValue (), is (1));
		assertThat (result.get (3).intValue (), is (4));
		assertThat (result.get (5).intValue (), is (6));
	}

	@Test
	public void it_should_reduce_arrays () {
		int sum = Json.reduce (Json.arrayNode (1, 2, 3), 0, (s, el) -> s + el.intValue ());
		assertThat (sum, is (6));
	}

	@Test
	public void it_should_reduce_any_json_arrays () {
		int sum = Json.reduce ((JsonNode) Json.arrayNode (1, 2, 3), 0, (s, el) -> s + el.intValue ());
		assertThat (sum, is (6));
	}

	@Test
	public void it_should_complain_when_reducing_over_non_arrays () {
		JsonException ex = assertThrows (JsonException.class, () -> {
			Json.reduce (Json.objectNode (), 0, (s, el) -> s + el.intValue ());
		});
		assertThat (ex.getMessage (), containsString ("array"));
	}

	@Test
	public void it_should_create_object_copies_with_only_specified_keys () {
		ObjectNode result = Json.keep (
			Json.objectNode ("first", 1, "second", 2, "third", 3),
			"first", "third"
		);

		assertThat (result.has ("first"), is (true));
		assertThat (result.has ("second"), is (false));
		assertThat (result.has ("third"), is (true));
	}

	@Test
	public void it_should_ignore_keys_that_are_not_present_when_keeping () {
		ObjectNode result = Json.keep (
			Json.objectNode ("first", 1, "second", 2, "third", 3),
			"first", "missing"
		);

		assertThat (result.has ("first"), is (true));
		assertThat (result.has ("second"), is (false));
		assertThat (result.has ("third"), is (false));
		assertThat (result.has ("missing"), is (false));
	}

	@Test
	public void it_should_create_any_json_object_copies_with_only_specified_keys () {
		ObjectNode result = Json.keep (
			(JsonNode) Json.objectNode ("first", 1, "second", 2, "third", 3),
			"first", "third"
		);

		assertThat (result.has ("first"), is (true));
		assertThat (result.has ("second"), is (false));
		assertThat (result.has ("third"), is (true));
	}

	@Test
	public void it_should_complain_when_keeping_keys_on_non_object_json () {
		JsonException ex = assertThrows (JsonException.class, () -> {
			Json.keep (Json.arrayNode (), "any");
		});
		assertThat (ex.getMessage (), containsString ("object"));
	}

	@Test
	public void it_should_create_object_copies_with_specified_keys_removed () {
		ObjectNode result = Json.purge (
			Json.objectNode ("first", 1, "second", 2, "third", 3),
			"first", "third"
		);

		assertThat (result.has ("first"), is (false));
		assertThat (result.has ("second"), is (true));
		assertThat (result.has ("third"), is (false));
	}

	@Test
	public void it_should_ignore_keys_that_are_not_present_when_purging () {
		ObjectNode result = Json.purge (
			Json.objectNode ("first", 1, "second", 2, "third", 3),
			"first", "missing"
		);

		assertThat (result.has ("first"), is (false));
		assertThat (result.has ("second"), is (true));
		assertThat (result.has ("third"), is (true));
		assertThat (result.has ("missing"), is (false));
	}

	@Test
	public void it_should_create_any_json_object_copies_with_specified_keys_removed () {
		ObjectNode result = Json.purge (
			(JsonNode) Json.objectNode ("first", 1, "second", 2, "third", 3),
			"first", "third"
		);

		assertThat (result.has ("first"), is (false));
		assertThat (result.has ("second"), is (true));
		assertThat (result.has ("third"), is (false));
	}

	@Test
	public void it_should_complain_when_removing_keys_on_non_object_json () {
		JsonException ex = assertThrows (JsonException.class, () -> {
			Json.purge (Json.arrayNode (), "any");
		});
		assertThat (ex.getMessage (), containsString ("object"));
	}

	@Test
	public void it_should_merge_json_objects () {
		ObjectNode result = Json.merge (
			Json.objectNode ("first", 1),
			Json.objectNode ("second", 2),
			Json.objectNode ("third", 3)
		);

		assertThat (result.has ("first"), is (true));
		assertThat (result.has ("second"), is (true));
		assertThat (result.has ("third"), is (true));
	}

	@Test
	public void it_should_merge_values_into_the_first_object () {
		ObjectNode target = Json.objectNode ("first", 1);
		Json.merge (target, Json.objectNode ("second", 2));

		assertThat (target.has ("first"), is (true));
		assertThat (target.has ("second"), is (true));
	}

	@Test
	public void it_should_skip_merging_empty_things () {
		ObjectNode target = Json.objectNode ("first", 1);
		Json.merge (target, null, null);
		assertThat (target.size (), is (1));
	}

	@Test
	public void it_should_overwrite_values_from_previous_sources () {
		ObjectNode result = Json.merge (
			Json.objectNode ("first", 1),
			Json.objectNode ("first", 2),
			Json.objectNode ("first", 3)
		);

		assertThat (result.get ("first").intValue (), is (3));
	}

	/* -- -- */

	@Test
	public void it_should_merge_absent_keys_in_json_objects (){
		ObjectNode result = Json.mergeAbsent (
			Json.objectNode ("first", 1),
			Json.objectNode ("second", 2),
			Json.objectNode ("third", 3)
		);

		assertThat (result.has ("first"), is (true));
		assertThat (result.has ("second"), is (true));
		assertThat (result.has ("third"), is (true));
	}

	@Test
	public void it_should_merge_absent_values_into_the_first_object () {
		ObjectNode target = Json.objectNode ("first", 1);
		Json.mergeAbsent (target, Json.objectNode ("second", 2));

		assertThat (target.has ("first"), is (true));
		assertThat (target.has ("second"), is (true));
	}

	@Test
	public void it_should_skip_merging_absent_values_of_empty_things () {
		ObjectNode target = Json.objectNode ("first", 1);
		Json.mergeAbsent (target, null, null);
		assertThat (target.size (), is (1));
	}

	@Test
	public void it_should_skip_merging_values_present_on_previous_sources () {
		ObjectNode result = Json.mergeAbsent (
			Json.objectNode ("first", 1),
			Json.objectNode ("first", 2),
			Json.objectNode ("first", 3)
		);

		assertThat (result.get ("first").intValue (), is (1));
	}

	@Test
	public void it_should_compute_values_on_object_with_missing_keys () {
		ObjectNode target = Json.objectNode ("first", 1);
		ObjectNode result = Json.computeIfAbsent (target, "second", (t, k) -> Json.valueOf (2));

		assertThat (result, is (target));
		assertThat (result.get ("second").intValue (), is (2));
	}

	@Test
	public void it_should_not_compute_values_on_object_when_key_is_present () {
		ObjectNode target = Json.objectNode ("first", 1);
		ObjectNode result = Json.computeIfAbsent (target, "first", (t, k) -> {
			throw new RuntimeException ("Fail");
		});

		assertThat (result, is (target));
		assertThat (result.has ("second"), is (false));
	}

	@Test
	public void it_should_remove_empty_values_from_arrays () {
		ArrayNode result = Json.purgeNulls (
			Json.arrayNode ("first", null, 1)
		);

		assertThat (result.size (), is (2));
		assertThat (result.get (0).asText (), is ("first"));
		assertThat (result.get (1).intValue (), is (1));
	}

	@Test
	public void it_should_remove_empty_values_from_any_json_arrays () {
		JsonNode result = Json.purgeNulls (
			(JsonNode) Json.arrayNode ("first", null, 1)
		);

		assertThat (result.size (), is (2));
		assertThat (result.get (0).asText (), is ("first"));
		assertThat (result.get (1).intValue (), is (1));
	}

	@Test
	public void it_should_remove_empty_values_from_objects () {
		ObjectNode result = Json.purgeNulls (
			Json.objectNode ("first", 1, "second", null, "third", 3)
		);

		assertThat (result.size (), is (2));
		assertThat (result.has ("first"), is (true));
		assertThat (result.has ("second"), is (false));
		assertThat (result.has ("third"), is (true));
	}

	@Test
	public void it_should_leave_arrays_without_empty_values_alone () {
		ArrayNode arr = Json.arrayNode ("first", 2, true);
		ArrayNode result = Json.purgeNulls (arr);
		assertThat (result, is (result));
		assertThat (result.size (), is (3));
	}

	@Test
	public void it_should_remove_empty_values_from_any_json_objects () {
		JsonNode result = Json.purgeNulls (
			(JsonNode) Json.objectNode ("first", 1, "second", null, "third", 3)
		);

		assertThat (result.size (), is (2));
		assertThat (result.has ("first"), is (true));
		assertThat (result.has ("second"), is (false));
		assertThat (result.has ("third"), is (true));
	}

	@Test
	public void it_should_leave_the_object_alone_when_not_containing_any_empty_values () {
		JsonNode result = Json.purgeNulls (
			(JsonNode) Json.objectNode ("first", 1, "second", 2, "third", 3)
		);

		assertThat (result.size (), is (3));
		assertThat (result.has ("first"), is (true));
		assertThat (result.has ("second"), is (true));
		assertThat (result.has ("third"), is (true));
	}

	@Test
	public void it_should_complain_when_purging_nulls_from_non_objects_and_non_arrays () {
		JsonException ex = assertThrows (JsonException.class, () -> {
			Json.purgeNulls (Json.valueOf (true));
		});
		assertThat (ex.getMessage ().toLowerCase (), containsString ("can only purge"));
	}

	/* -- -- */

	@Test
	public void it_should_turn_array_nodes_into_lists () {
		ArrayNode arr = Json.arrayNode (1, 2, 3);
		List<Integer> list = Json.toList (arr, JsonNode::intValue);
		assertThat (list, hasSize (3));
		assertThat (list, hasItems (1, 2, 3));
	}

	@Test
	public void it_should_turn_any_json_array_nodes_into_lists () {
		ArrayNode arr = Json.arrayNode (1, 2, 3);
		List<Integer> list = Json.toList ((JsonNode) arr, JsonNode::intValue);
		assertThat (list, hasSize (3));
		assertThat (list, hasItems (1, 2, 3));
	}

	@Test
	public void it_should_complain_when_turning_non_json_array_into_a_list () {
		JsonException ex = assertThrows (JsonException.class, () -> {
			Json.toList (Json.objectNode (), JsonNode::intValue);
		});
		assertThat (ex.getMessage (), containsString ("array"));
	}

	@Test
	public void it_should_turn_objects_into_maps () {
		Map<String, Integer> map = Json.toMap (
			Json.objectNode ("first", 1, "second", 2),
			(k, v) -> v.intValue ()
		);

		assertThat (map, allOf (
			hasEntry ("first", 1),
			hasEntry ("second", 2)
		));
	}

	@Test
	public void it_should_turn_any_json_objects_into_maps () {
		Map<String, Integer> map = Json.toMap (
			(JsonNode) Json.objectNode ("first", 1, "second", 2),
			(k, v) -> v.intValue ()
		);

		assertThat (map, allOf (
			hasEntry ("first", 1),
			hasEntry ("second", 2)
		));
	}

	@Test
	public void it_should_complain_when_turning_non_objects_into_maps () {
		JsonException ex = assertThrows (JsonException.class, () -> {
			Json.toMap (Json.valueOf (true), (k, v) -> true);
		});
		assertThat (ex.getMessage (), containsString ("object"));
	}

}

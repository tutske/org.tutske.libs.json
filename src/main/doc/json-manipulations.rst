==========================================================================================
Json Manipulations
==========================================================================================

We may want to go over the data we have collected in json nodes and modify them in some
way. In javascript there is an api that uses higher order methods. Since java has
functional interfaces and lambda expressions we can simulate this api.

This section lists the supported higher order operations.


Check Containment
==========================================================================================

.. code-block:: java

    Json.contains (array, item);

Returns the ``true`` if the item can be found in the array, ``fales`` otherwise. Keep in
mind that this uses the json node ``equals`` method. Most of the time this will do what
you want. But your requirements may differ.

You can also pass instances of ``JsonNode``, but if they are not really array nodes the
method will throw an exception.


Mapping Over An Array
==========================================================================================

.. code-block:: java

    Json.map (array, node -> { /* ... */ })

Returns a new array node with the result of calling the lambda on very item in the array.
You can also pass instances of ``JsonNode``, but if they are not really array nodes the
method will throw an exception.

There is a variant that allows you to provided a lambda that accepts ``ObjectNode`` s
instead of plain ``JsonNode`` s

.. code-block:: java

    Json.mapObjects (array, obj -> { /* ... */ });

The original array node is not modified, but if your function modifies the json nodes
passed to it, those changes will show up in the original array as well.

.. code-block:: java

    ArrayNode original = Json.arrayNode (Json.objectNode ("key", "value"));
    ArrayNode changed = Json.mapObjects (original, item -> {
        item.put ("key", "replaced")
        return item;
    });

    original.path (0).path ("key").asText ()
    // -> "replaced"


Filtering Entries In An Array Node
==========================================================================================

.. code-block:: java

    Json.filter (array, node -> node.isContainerNode ());

Returns a new array node that only contains those items for which the predicate returns
true. The original array is left untouched. While it would not make sense to modify the
object in a predicate, if you do so those changes will show up in the original array node.

You can also pass instances of ``JsonNode``, but if they are not really array nodes the
method will throw an exception.

There is a variant that allows you to provided a lambda that accepts ``ObjectNode`` s
instead of plain ``JsonNode`` s

.. code-block:: java

    Json.filterObjects (array, node -> node.has ("id"));


Finding The First Match
==========================================================================================

.. code-block:: java

    Json.find (array, node -> node.path ("id").asText ().equals ("abc"));

Returns the first item in an array node for which the predicate returns true. You can
also pass instances of ``JsonNode``, but if they are not really array nodes the method
will throw an exception.

There is a variant that allows you to provided a lambda that accepts ``ObjectNode`` s
instead of plain ``JsonNode`` s. This variant will also return an ``ObjectNode``.

.. code-block:: java

    Json.findObject (array, node -> node.path ("id").asText ().equals ("12"));


Combining Multiple Array Nodes
==========================================================================================

.. code-block:: java

    Json.concat (
        Json.arrayNode ("a", "b"),
        Json.arrayNode ("c", "d"),
        Json.arrayNode ("e", "f")
    );
    // -> [ "a", "b", "c", "d", "e", "f" ]

Returns a new array node that contains all elements of the original array nodes, in order
of appearance. None of the original array nodes are modified.


Reducing Array Node To A Value
==========================================================================================

.. code-block:: java

    long sum = Json.reduce (array, 0, (acc, curr) -> (
        acc + curr.asLong ()
    ));

Computes a value by iterating over the array node and sequentially calling the lambda
expression with the result of the previous step and the current value. The second argument
is the initial value where the reduction begins. It specifies the return type of ``reduce
()`` and is always required.

You can also pass instances of ``JsonNode``, but if they are not really array nodes the
method will throw an exception.

If you modify the current node in your lambda expression the changes you make will be
visible in the original array, even if this method leaves the original untouched.


Keeping Only Specific Keys
==========================================================================================

.. code-block:: java

    ObjectNode data = Json.objectNode (
        "first", "one",
        "second", "two",
        "third", "three",
    );
    Json.keep (data, "first");
    // -> { "first": "one" }

Returns a new object node with only key value pairs where the key is equal to one that is
specified. The original object node is not modified.

You can also pass instances of ``JsonNode``, but if they are not really object nodes the
method will throw an exception.


Purging Specific Keys From An Object
==========================================================================================

.. code-block:: java

    ObjectNode data = Json.objectNode (
        "first", "one",
        "second", "two",
        "third", "three",
    );
    Json.purge (data, "first");
    // -> { "second": "two", "third": "three" }

Returns a new object node that no longer contains key value pairs where the key is equal
to one that is specified. The original object node is not modified.

You can also pass instances of ``JsonNode``, but if they are not really object nodes the
method will throw an exception.


Combining Multiple Object Nodes
==========================================================================================

.. code-block:: java

    Json.merge (
        Json.objectNode ("first", "one"),
        Json.objectNode ("second", "two"),
        Json.objectNode ("third", "three")
        Json.objectNode ("first", "ONE"),
    )
    // -> { "first": "ONE", "second": "two", "third": "three" }

Return the first object. That object is also modified by adding all the key value pairs of
the objects following it. The order of the arguments is important, the last object will
overwrite values in previous objects that have the same key.

If you don't want to change the original you can pass an empty object as the first
argument:

.. code-block:: java

    Json.merge (Json.objectNode (), ...);

If you don't want to overwrite existing values you can use ``Json.mergeAbsent ()``
instead. It behaves in the same way but will not overwrite values for which the key is
already present.

.. code-block:: java

    Json.mergeAbsent (
        Json.objectNode ("first", "one"),
        Json.objectNode ("second", "two"),
        Json.objectNode ("third", "three")
        Json.objectNode ("first", "ONE"),
    )
    // -> { "first": "one", "second": "two", "third": "three" }


Computing Absent Value
==========================================================================================

.. code-block:: java

    ObjectNode data = Json.objectNode ("key", "value");
    Json.computeIfAbcent (data, "extra", (d, k) -> "field");
    // -> { "key": "value", "extra": "field" }

Modifies the original object when the key is not present on the object, it will assign the
result of calling the lambda expression to the key. The lambda will receive the original
json object node and the key for which the value should be computed.

You can also pass instances of ``JsonNode``, but if they are not really object nodes the
method will throw an exception.


Removing null values
==========================================================================================

.. code-block:: java

    Json.purgeNulls (Json.arrayNode ("a", null, "b", null, null));
    // -> [ "a", "b" ]

For array nodes this will return a copy of the array with all null nodes removed. The
original array is not modified.

.. code-block:: java

    Json.purgeNulls (Json.objectNode (
        "key", "value",
        "removed", null,
    ));
    // -> { "key": "value" }

For object nodes this will return a copy of the original object with all key value pairs
where the value is null removed. The original object is not modified.

This will however not remove recursive values. Not for objects nodes and not for array
nodes. If you have an object that contains arrays that have null values, those arrays will
still contain null values. This method only checks direct child nodes.

You can also pass instances of ``JsonNode``, but if the target is not an ``ArrayNode`` or
an ``ObjectNode`` the method will throw an exception.

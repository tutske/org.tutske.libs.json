==========================================================================================
Json Construction Extensions
==========================================================================================


Quick Usages:
==========================================================================================

.. code-block::

    import static org.tutske.lib.json.Json.*;

    ArrayNode node = arrayNode (
        objectNode (
            "id", 1,
            "name", "John Doe"
        ),
        objectNode (
            "id", 2,
            "name", "Jane Doe"
        )
    );


Regular Construction
==========================================================================================

In order to get a ``JsonNode`` from arbitrary data you would have to construct them
manually. For primitive values we would have to do the following:

.. code-block:: java

    TextNode text = new TextNode ("text");
    TextNode text = JsonNodeFactory.instantce.textNode ("text");


Similar constructions are possible for other json primitives. This works as long as we
know what the type is of the thing we try to turn into a ``JsonNode``. When we don't know
what the type is we can use:

.. code-block:: java

    JsonNode node = Json.valueOf (...);

For container nodes such as ``ArrayNode`` and ``ObjectNode`` things are a bit more
complicated. While you can still construct them in a similar way as the primitive times,
you most likely want to add things to them. To do that you will have to write several
statements:

.. code-block:: java

    ArrayNode array = JsonNodeFactory.instance.arrayNode ();
    array.add ("first");
    array.add ("second");

In other languages you would write this a single expression:

.. code-block:: javascript

    const array = [ "first", "second" ];

Java has no such syntax, but we can mimic the above with variable arguments. You can use
``Json.objectNode ()`` and ``Json.arrayNode ()`` to turn data creation into single
statements. These are meant to be nested such that defining a complex structure is
possible and ergonomic.

.. code-block:: java

    ArrayNode node = Json.arrayNode (
        Json.objectNode (
            "id", 1,
            "name", "John Doe"
        ),
        Json.objectNode (
            "id", 2,
            "name", "Jane Doe"
        )
    );

Both methods will take arbitrary arguments and turn them into the json equivalent,
collections are turned into ``ArrayNode`` s, maps are turned into ``ObjectNode`` s. If an
argument is not a primitive, collection or map it will be turned into a ``PojoNode``.

Lists and maps can be turned into array nodes and object nodes directly:

.. code-block:: java

    ArrayNode node = Json.arrayNode (Arrays.asList ("one", "two"));
    ObjectNode node = Json.objectNode (Collections.singletonMap ("key", "value"));

Sometimes we may want to sculpt the entries of a list before we add them as array nodes,
so the constructor method for the array can optionally take a lambda that is called with
every entry of the list.

.. code-block:: java

    Json.arrayNode (Arrays.asList (...), item -> { /* ... */ });

There is also a construction variant that creates json through the use of a lambda
expression. Instead of accessing ``Json.objectNode`` and ``Json.arrayNode`` directly you
can pass a lambda that accepts two producers: the first create a new object node, the
second create a new array node.

.. code-block:: java

    JsonNode node = Json.json ((object, array) -> {
        array.create (
            object.create ("id", 1, "John Doe" ),
            object.create ("id", 2, "Jane Doe" )
        )
    });

The ``object`` and ``array`` producers passed behave the same as ``Json.objectNode ()`` and
``Json.arrayNode ()``, these two will produce ``PojoNode`` s when the type passed is not
known.

.. warning::

    Since unknown types are turned into ``PojoNode`` s, they are not properly accessible
    when using the normal jackson api.

    Assume we have a ``Person`` class with two properties, a numeric id and a name. Such a
    class would serializes just fine:

    .. code-block:: java

        new ObjectMapper ().writeValueAsString (new Person (1, "John Doe"));
        // -> { "id": 1, "name": "John Doe" }

    But trying to get the name from a json tree will fail:

    .. code-block:: java

        JsonNode node = Json.valueOf (new Person (1, "John Doe"));
        node.get ("John Doe").asText (); // -> NullPointerException


Construction with mapper
==========================================================================================

The construction methods all have a variant that accepts an object mapper as the first
argument. This mapper is used to turn unknown types into json trees. When you want to
access the data as json you would have to use one of these variants.  If you only care
about serialization this is not a concern and you can leave out the object mapper from the
constructing methods.

.. code-block:: java

    ObjectMapper mapper = ...;
    ObjectNode node = Json.objectNode (mapper,
        "1", new Person (1, "John Doe")
        "2", new Person (2, "Jane Doe")
    );

Here ``Person`` will not be turned into a ``PojoNode`` but into the structure that the
mapper would create when writing out that object. The same is true for ``Json.arrayNode
()``:

.. code-block:: java

    ObjectMapper mapper = ...;
    ArrayNode node = Json.arrayNode (mapper,
        new Person (1, "John Doe"),
        new Person (2, "Jane Doe")
    );

This mapper is only used for the invocation that it is passed to, so when nesting calls
you would have to pass them to every one of them. Note that we did not pass the mapper to
``Json.arrayNode ()`` because it only contains things that it can already turn into a
``JsonNode``.

.. code-block:: java

    ObjectMapper mapper = ...;
    Json.arrayNode (
        Json.objectNode (mapper,
            "account", "...",
            "owner", new Person (1, "John Doe")
        ),
        Json.objectNode (mapper
            "account", "...",
            "owner", new Person (2, "Jane Doe")
        )
    );

When constructing json through the use of a lambda expression you only need to pass the
mapper once:

.. code-block:: java

    ObjectMapper mapper = ...;
    ArrayNode node = Json.json (mapper, (object, array) -> array.create (
        object.create (
            "account", "",
            "owner", new Person (1, "John Doe")
        ),
        object.create (
            "account", "",
            "owner", new Person (2, "Jane Doe"),
            "followers": array.create (
                new Person (3, "John Smith")
            )
        )
    ));

    node.get (1).get ("followers").get (0).get ("name").asText ().equals ("John Smith");

Finally we can also pass the object mapper to the constructor methods that convert lists
and maps into json nodes, which will use the mapper to turn all entries in the collections
and maps into proper json nodes.

.. code-block:: java

    ObjectMapper mapper = ...;
    ArrayNode array = Json.arrayNode (mapper, Arrays.asList ("one", "two"));
    Objectnode node = Json.objectNode (mapper, Collections.singletonMap ("key", "value"));


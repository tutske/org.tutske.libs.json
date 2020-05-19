==========================================================================================
Java Integration
==========================================================================================


Turn streams into array or object nodes
==========================================================================================

Streams can be collected into lists wit ``Collectors.toList ()``, no such method exists
within jackson to turn the stream into an array node.

Now you can use ``Json.collectToArray ()``:

.. code-block:: java

    Stream.of ("one", "two").collect (Json.collectToArray ());
    // -> [ "one", "two" ]

You can also collect the stream to an object, but you will need to pass two functions, one
that computes the key of the object entry and one to compute the value to assign to that
key.

.. code-block:: java

    Stream.of ("one", "two").collect (Json.collectToObject (
        item -> item.substring (0, 1),
        item -> item.substring (1)
    ));
    // -> { "o": "ne", "t": "wo" }

If the stream contains ``Map.Entry`` items, you don't need to provided any methods.
``Json.collectToObject ()`` will use the key and value of the underlying map entry object.

.. code-block:: java

    Map<String, ?> map = Collections.singletonMap ("key", "value");
    map.entrySet ().stream ().collect (Json.collectToObject ());
    // -> { "key": "value" }


.. rubric:: Use With Mappers

If the result of your value methods do not return normal primitives, collections or maps
you will not be able to properly access the values contained in the json structure. If you
need this functionality you will have to provide an object mapper to do this heavy
lifting:

.. code-block:: java

    ObjectMapper mapper = Mappers.createBaseMapper ();

    Stream.of ("one", "two").collect (Json.collectToArray (mapper));
    Stream.of ("one", "two").collect (Json.collectToObject (mapper, keyFn, valueFn));
    map.entrySet ().stream ().collect (Json.collectToObject (mapper));


Turn Container Nodes Into Java Equivalents
==========================================================================================

We may also want to turn an array node into a list such that it can be passed to other
libraries or methods. Most likely you don't want a list of json nodes but a list of the
values that they represent.

You can use ``Json.toList ()`` to turn an array node into a list. You have to provided
both the array node and a function that computes the desired value. The returned type will
me ``List<T>`` where ``T`` is the type returned by your function.

.. code-block:: java

    List<String> result = Json.toList (array, node -> "Fixed String Value");

Similarly we can turn object nodes into maps. You will still have to transform the values
into the desired java types, but your function will take both a key and a value.

.. code-block:: java

    Map<String, Long> result = Json.toMap (data, (k, v) -> v.asLong ())

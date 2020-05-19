==========================================================================================
Json Convenience Extenions
==========================================================================================

This project builds on top of the jackson json libraries. Jackson allows for very flexible
json manipulation with its node types, bridging some of the distance between more
dynamically typed languages such as javascript.


Motivation
==========================================================================================

The standard way for java based applications to handle json is to deserialize the received
data into a predefined set of pojos and serializing a similar set of pojos into responses.
Very successful projects have been build this way, but they miss out on some of the
benefits of json. It also requires endless definitions of plain java classes with
generated getters and setters.

The javascript community takes a different approach. Historically the language did not
have real classes. What they did have where objects, arrays and literals. Those naturally
map into json documents (since json stands for javascript object notation). No class
definitions are required.

Their take on handling json is different and at times uses more advanced ways of dealing
with json documents. For instance Javascript also allows you to dynamically pick a field
from an object.

.. code-block:: javascript

    const KEY = 'key';
    data[KEY] === 'value';

In java you would have to rely on reflection to do the heavy lifting. But even then, do
you look for a getter, or the property? Do you make the property accessible even though it
is private? Do boolean getters start with ``is`` instead of ``get``?

In javascript you can directly define a nested json structure inline.

.. code-block:: javascript

    JSON.stringify ({
        "key": "value",
        "items": [
            { "id": 1, "name": "John Smith" }
        ]
    });

Java could do this through constructors and ``Arrays.asList ()``. But you lose the context
of the keys, and you have to remember the order of all the constructor arguments. Perhaps
you don't want to create a separate class (because it is to specific) just so your
framework knows how to serialize your data.

.. code-block:: java

    Object data = new Response (
        "value",
        Arrays.asList (
            new Person (1, "John Smith")
        )
    );

You don't have to give up on the traditional way of handling json. You can choose whether
you want to use conversion into pojos, or methods provided in this project.

Having a more flexible attitude to your json documents opens up the same benefits to your
apis that you can find in less `enterprise grade` environments:

- The exact structure can change somewhat (notably in its additions)

- Some values are optional. There may be a difference between being present and set to
  null, and not being present at all. (``data.has ("field")`` vs ``data.path
  ("field").isNull ()``).

- Dynamically find fields.

- Use multiple representations. For instance accepting an id, or an object that has the id.
  (``data.isContainerNode () ? data.path ("id").asLong () : data.asLong ()``)

- ...


.. rubric:: jackson

The jackson json library allows you to approach json in a dynamic way. It has a
``JsonNode`` that represents a json document that you can traverse however you see fit for
instance by accessing your document by some variable property.

.. code-block:: java

    String KEY = "key";
    data.path (KEY).asText ().equals ("value");


Even though this is still more verbose than the javascript equivalent, it does allow you
to approach json data as you would in javascript.

While jackson gets us a long way, there is some improvement possible in terms of
ergonomics. Jackson does not allow easy inline construction of arbitrary json documents,
and does not support some common manipulation methods (such as filtering an
``ArrayNode`` for instance).

The functionality of this extensions can be roughly divided in the following categories:

.. toctree::
    :maxdepth: 1

    ./json-construction
    ./object-mapper-construction
    ./json-manipulations
    ./java-integration.rst
    ./json-exceptions

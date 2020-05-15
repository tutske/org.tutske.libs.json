==========================================================================================
Json Exceptions
==========================================================================================

In some cases you want to provide exceptions that have more context than just a message.
You could define custom classes every time you need to represent such a context, or you
can subclass ``JsonException``. It has a message and a bag of extra properties that you
can add to yourself.

.. code-block:: java

    new JsonException ("Something unexpected occurred",
        Json.objectNode ("key", "value")
    );

These exceptions extend ``RuntimeException`` and do not need to be declared in your method
signatures.

You can later add extra information to the json exception if you want to. Extra
information is always provided as a json object node. Conceptually it is equivalent to
merging the provided object with the existing data, where the new values will overwrite
the existing values.

.. code-block:: java

    JsonException ex = new JsonException ("Something unexpected occurred");
    ex.addExtra (Json.objectNode ("key", "value"));

.. warning::

    Adding data later will not recursively merge the json structures, it will just
    overwrite existing keys:

    .. code-block:: java

        JsonException ex = new JsonException ("Something unexpected occurred"
            Json.objectNode ("arr", Json.arrayNode ("one", "two"))
        );

        ex.addExtra (Json.objectNode ("arr", Json.arrayNode ("three", "four"));
        Json.prettyStringify (ex);

    This will result in an array with only the last two values

    .. code-block:: partial-json

        {
            ...,
            "arr": [ "three", "four" ]
        }


.. rubric:: Default Keys

Keep in mind that ``"status"`` and ``"error"`` will already be populated when turning such
an exception into json. When you put one of these keys in your context object the
resulting json will contain multiple values for those keys.

.. code-block:: java

    Json.stringify (new JsonException (
        "Something went wrong",
        Json.objectNode ("status", "failed")
    ));

Results in the following json:

.. code-block:: partial-json

    {
        "status": "nok",
        "error": "Something went wrong",
        "status": "failed"
    }

The ``status`` will always be set to ``'nok'``, and the message of the exceptions will be
placed in ``error``.


.. rubric:: Configure Serialization

You can directly instantiate a serializer:

.. code-block:: java

    ObjectMapper mapper = Mappers.configure (new ObjectMapper (), m -> {
        m.addSerializer (
            JsonException.class,
            new JsonException.JacksonSerializer ()
        );
    });

Or use the static method provided on ``JsonException``:

.. code-block:: java

    ObjectMapper mapper = Mappers.configure (
        new ObjectMapper ()
        JsonException::configureJacksonMapper
    );

Keep in mind that the default object mappers created via ``Mappers.mapper ()`` already
have the jackson serializer for json exceptions enabled. Only if you manually specify the
object mapper to configure do you possibly need to add this.

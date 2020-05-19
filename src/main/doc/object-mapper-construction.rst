==========================================================================================
Object Mapper Construction
==========================================================================================


Quick Usage
==========================================================================================

.. code-block:: java

    import static org.tutske.lib.json.Mappers.*;

    ObjectMapper mapper = createBaseMapper ();
    ObjectMapper mapper = mapper (mod -> {
        serialize (mod, CustomEntity.class, (value, gen, provider) -> {
            gen.writeString ("Custom Serialization");
        });
    });

Optionally put the addition of serializers in methods that can then be reused as desired:

.. code-block:: java

    public void customs (SimpleModule module) {
        serialize (module, CustomEntity.class, (value, gen, provider) -> {
            gen.writeString ("Custom Serialization");
        });
    }

    public void specials (SimpleModule module) {
        serialize (module, SpecialCase.class, (value, gen, provider) -> {
            gen.writeString ("Special Serialization");
        });
    }

    public static void main (String [] args) {
        ObjectMapper mapper = mapper (Serializers::customs, Serializers::specials);
    }


Creating Mappers
==========================================================================================

Normally you would construct a new ``ObjectMapper`` by calling it's constructor. Such an
object mapper will behave sensibly in most situations, however it will not handle newer
java types such as ``Optional``, and it will do weird things for classes in ``java.time``.

There are standard jackson libraries to add support for serializing and deserializing
these java classes. However these are not configured when calling the default constructor.
In addition instants are serialized as a decimal number (seconds since epoch) while they
are probably better serialized as iso strings.

You would have to configure these modules, and possibly the serialization of
``JsonException`` for every mapper you create. Alternatively you can use the convenience
static constructor method that will return a preconfigured instance.

.. code-block:: java

    ObjectMapper mapper = Mappers.mapper ();

You can directly add some modules to the mapper during it's construction:

.. code-block:: java

    ObjectMapper mapper = ObjectMapper.mapper (
        new CustomModule (),
        new SpecialModule (),
        ...
    );

You can pass a single module, or as many as you like. Keep in mind that the default
mappers created with ``Mappers.mapper ()`` already have both the ``Java8Module`` and
``JavaTimeModule`` enabled by default.

You don't have to create a custom module for simple module configurations, you can use the
variant of ``Mappers.mapper ()`` that accepts simple module consumers. Again you can pass
a single consumer or as many as you want.

.. code-block:: java

    ObjectMapper mapper = ObjectMapper.mapper (
        module -> { /* ... */ },
        module -> { /* ... */ },
        ...
    );

A ``SimpleModule`` will be created for you and will be passed to every lambda in the order
they were provided.

Instead of creating a new object mapper you can use similar methods to configure an
existing one.

.. code-block:: java

    ObjectMapper mapper = ObjectMapper.configure (
        mapper.copy (),
        new CustomModule (),
        new SpecialModule (),
        ...
    );

    ObjectMapper mapper = ObjectMapper.configure (
        mapper.copy (),
        module -> { /* ... */ },
        module -> { /* ... */ },
        ...
    );


Creating Simple Modules
==========================================================================================

When you want to configure custom serializers you will have to add a new module to the
object mapper. This requires that you create a module, configure it and then add the
result to the object mapper.

In the previous section we have seen shortcuts for doing that when we create a mapper. We
can also create just the module, without adding it to an object mapper.

.. code-block:: java

    SimpleModule module = ObjectMapper.module (
        module -> { /* ... */ },
        module -> { /* ... */ },
        ...
    );

The ``SimpleModule`` has a fluent api for configuration, so most of the time you will not
need to do this. But if you want to use the serializer functional interfaces described
below you no longer have the benefit of this fluent api. Which also means that you can no
longer create a fully configured instance in a single expression. With the module creation
method however you still can.


Adding Custom Serializers
==========================================================================================

Once you have a mapper you may want to add special serializes and deserializers for your
custom objects. Adding such a serializer would involve a lot of boilerplate code.

.. code-block:: java

    ObjectMapper mapper = ...;
    SimpleModel module = new SimpleModel ();
    module.addSerializer (Custom.class, new StdSerializer<Custom> (clazz) {
        @Override
        public void serialize (Custom value, JsonGenerator gen, SerializerProvider provider)
        throws IOException {
            /* ... */
        }
    });
    mapper.registerModule (moduel);

While the only pieces of real code in this example is the reference to the class we wish
to serialize and the body of the serialize method. To simplify adding custom serialization
we have added some methods that capture the essence of the serialization and
deseralization with the use of a functional interface.

.. code-block:: java

    Mappers.module (m -> {

        Mappers.serialize (m, CustomEntity.class, (value, gen, provider) -> {
            /* ... */
        });

        Mappers.deserialize (m, CustomEntity.class, (p, context) -> {
            /* ... */
        });

    });

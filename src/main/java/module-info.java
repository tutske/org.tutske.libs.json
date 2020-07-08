module org.tutske.libs.json {
	exports org.tutske.lib.json;

	/* named automatic modules */
	requires transitive com.fasterxml.jackson.core;
	requires transitive com.fasterxml.jackson.databind;
	requires transitive com.fasterxml.jackson.datatype.jdk8;
	requires transitive com.fasterxml.jackson.datatype.jsr310;

}

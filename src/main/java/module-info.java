module org.tutske.libs.json {
	exports org.tutske.lib.json;

	/* named automatic modules */
	requires com.fasterxml.jackson.core;
	requires com.fasterxml.jackson.databind;
	requires com.fasterxml.jackson.datatype.jdk8;
	requires com.fasterxml.jackson.datatype.jsr310;

}

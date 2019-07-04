package it.mytutor.domain.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import it.mytutor.domain.Teacher;

import java.io.IOException;
import java.sql.Date;

public class TeacherDeserializer extends StdDeserializer<Teacher> {
    public TeacherDeserializer(){ this(null); }

    public TeacherDeserializer(Class<?> vc) { super(vc); }

    @Override
    public Teacher deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        Teacher teacher = new Teacher();
        if (node.get("idTeacher") != null) {
            teacher.setIdTeacher(node.get("idTeacher").asInt());
        }
        teacher.setPostCode(node.get("postCode").asInt());
        teacher.setCity(node.get("City").asText());
        teacher.setRegion(node.get("region").asText());
        teacher.setStreet(node.get("street").asText());
        teacher.setStreetNumber(node.get("streetNumber").asText());
        teacher.setByography(node.get("byography").asText());
        JsonNode userNode = node.get("user");
        teacher.setIdUser(userNode.get("idUser").asInt());
        teacher.setEmail(userNode.get("email").asText());
        teacher.setName(userNode.get("name").asText());
        teacher.setSurname(userNode.get("surname").asText());
        Date bDate = new Date(userNode.get("birthday").asLong());
        teacher.setBirtday(bDate);
        teacher.setLanguage(Boolean.getBoolean(userNode.get("language").asText()));
        teacher.setImage(userNode.get("image").asText());

        return teacher;
    }


}

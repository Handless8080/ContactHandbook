package utils;

import dto.AttachmentFile;
import dto.Contact;
import dto.Page;
import dto.Phone;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JSONSerializer {
    private static ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
    }

    public static String toJSON(Page object) throws IOException {
        return objectMapper.writeValueAsString(object);
    }

    public static String toJSON(ArrayList object) throws IOException {
        return objectMapper.writeValueAsString(object);
    }

    public static String contactToJSON(Contact contact) throws IOException {
        return objectMapper.writeValueAsString(contact);
    }

    public static List<Phone> phonesFromJSON(String JSON) {
        try {
            return objectMapper.readValue(JSON, new TypeReference<List<Phone>>() { });
        } catch (IOException e) {
            return null;
        }
    }

    public static List<AttachmentFile> filesInfoFromJSON(String JSON) {
        try {
            return objectMapper.readValue(JSON, new TypeReference<List<AttachmentFile>>() { });
        } catch (IOException e) {
            return null;
        }
    }
}
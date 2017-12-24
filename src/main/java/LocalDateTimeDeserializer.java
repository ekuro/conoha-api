import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ekuro on 2017/12/24.
 */
public class LocalDateTimeDeserializer implements JsonDeserializer<LocalDateTime> {

    private static final List<String> patterns = Arrays.asList("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", "yyyy-MM-dd'T'HH:mm:ss'Z'");

    @Override
    public LocalDateTime deserialize(JsonElement element, Type arg1, JsonDeserializationContext arg2) throws JsonParseException {
        String date = element.getAsString();
        for (String pattern : patterns) {
            try {
                return LocalDateTime.parse(date, DateTimeFormatter.ofPattern(pattern));
            } catch (DateTimeParseException e) {
                System.out.println(e.getMessage());
            }
        }
        return null;
    }
}

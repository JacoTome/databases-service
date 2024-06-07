package musico.services.databases.models.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import musico.services.databases.config.OntEntityField;
import musico.services.databases.config.OntologyModel;
import musico.services.databases.models.Users;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MusicalWorkQueryParams {
    @OntEntityField(pred = "mo:genre", type = OntEntityField.DataType.OBJECT)
    private String genre;
    @OntEntityField(pred = "mo:bpm", type = OntEntityField.DataType.DATA)
    private Integer bpm;
    private Float danceability;

    public static Map<String, String> getFieldsPredicate() {
        Map<String, String> fields = new HashMap<>();
        for (Field field : MusicalWorkQueryParams.class.getDeclaredFields()) {
            OntEntityField annotation = field.getAnnotation(OntEntityField.class);
            if (annotation != null) {
                fields.put(field.getName().toLowerCase(), OntologyModel.getPredicate(annotation.pred()));
            }
        }
        return fields;
    }
}

package musico.services.databases.utils;

import lombok.extern.slf4j.Slf4j;
import musico.services.databases.config.OntEntity;
import musico.services.databases.config.OntEntityField;
import musico.services.databases.config.OntologyModel;
import musico.services.databases.models.Users;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.util.Values;
import org.eclipse.rdf4j.model.vocabulary.RDF;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Slf4j
public class DataGenerator {

    /**
     * Generates a list of Turtle (TTL) triples for a given user.
     * The triples are generated based on the fields of the user object that are annotated with the OntEntityField annotation.
     * If the annotated field is of type OBJECT, the method genObjField is called to generate the triples.
     * If the annotated field is of type DATA, a data triple is generated.
     *
     * @param user the user object for which to generate the triples
     * @return a list of strings, where each string is a Turtle triple
     * @throws IllegalAccessException if the field is inaccessible
     */
    public List<String> genUserData(Users user) throws IllegalAccessException {
        Field[] fields = user.getClass().getDeclaredFields();
        String subject = user.getUserIRI();
        ArrayList<String> data = new ArrayList<>();
        data.add(genUserTTL(user));
        for (Field field : fields) {
            field.setAccessible(true);
            OntEntityField annotation = field.getAnnotation(OntEntityField.class);
            if (annotation != null && field.get(user) != null) {
                try {
                    switch (annotation.type()) {
                        case OBJECT:
                            data.addAll(genObjField(subject, OntologyModel.getPredicate(annotation.pred()), field.get(user)));
                            break;
                        case DATA:
                            data.add(genDataTTL(subject, OntologyModel.getPredicate(annotation.pred()), field.get(user).toString()));
                            break;
                        default:
                            break;
                    }
                } catch (IllegalAccessException e) {
                    log.error("Error User data: {}", e.getMessage());
                    throw e;
                }
            }
        }
        return data;
    }



    private String genUserTTL(Users user) {
        String ns = Objects.requireNonNull(OntologyModel.getNamespace("musicoo")).getName();
        Statement statement = Values.getValueFactory().createStatement(
                Values.iri(user.getUserIRI()),
                RDF.TYPE,
                Values.iri(ns + "HumanMusician")
        );
        return genObjTTL(statement.getSubject().stringValue(),
                statement.getPredicate().stringValue(),
                statement.getObject().stringValue());
    }

    private List<String> genObjField(String subject, String pred, Object object) {
        ArrayList<String> data = new ArrayList<>();
        try {
            if (object instanceof Collection) {
                for (Object o : (Collection<?>) object) {
                    if (o instanceof OntEntity) {
                        data.add(genObjTTL(subject, pred, ((OntEntity) o).getIRI()));
                    }
                }
            } else {
                if (object instanceof OntEntity) {
                    data.add(genObjTTL(subject, pred, ((OntEntity) object).getIRI()));
                }
            }
        } catch (Exception e) {
            log.error("Error generating object ttl: {}", e.getMessage());
        }
        return data;
    }

    private String genObjTTL(String subject, String pred, String object) {
        return "<" + subject + "> <" + pred + "> <" + object + "> .\n";
    }

    private String genDataTTL(String subject, String pred, String object) {
        return "<" + subject + "> <" + pred + "> \"" + object + "\" .\n";
    }
}
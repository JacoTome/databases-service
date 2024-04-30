package musico.services.databases.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import musico.services.databases.config.OntEntityField;
import musico.services.databases.config.OntologyModel;
import org.eclipse.rdf4j.model.Namespace;
import java.lang.reflect.Field;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@Table(name = "users")
public class Users {

    @Id
    @Column(name = "userId", nullable = false)
    private Integer userId;

    @MapsId
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "userId", nullable = false)
    private Artist artist;

    @Column(name = "first_name", length = 50)
    @OntEntityField(type = OntEntityField.DataType.DATA, pred = "foaf:firstName")
    private String firstName;

    @Column(name = "last_name", length = 50)
    @OntEntityField(type = OntEntityField.DataType.DATA, pred = "foaf:surname")
    private String surname;

    @Column(name = "codice_fiscale", length = 16)
    private String codiceFiscale;

    @ManyToOne(fetch = FetchType.LAZY)
    @OntEntityField(type = OntEntityField.DataType.OBJECT, pred = "foaf:based_near")
    @JoinColumn(name = "based_near")
    private Position based_near;

    @Column(name = "professional_level", length = 100)
    private String professionalLevel;

    @Column(name = "expertise_level", length = 100)
    private String expertiseLevel;

    @Column(name = "`multi-instrumentalism_level`", length = 100)
    private String multiInstrumentalismLevel;

    @Column(name = "createdAt")
    private Instant createdAt;

    @Column(name = "updatedAt")
    private Instant updatedAt;

    @OneToMany(mappedBy = "receiver")
    private Set<ChatMessage> chatMessages;

    @OneToMany(mappedBy = "user2")
    private Set<Friendship> friendships;

    @ManyToMany
    @JoinTable(name = "group_user",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id"))
    private Set<MusicianGroup> musicianGroups;

    @ManyToMany
    @JoinTable(name = "user_has_instrument",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "instrument_id"))
    @OntEntityField(type = OntEntityField.DataType.OBJECT, pred = "musicoo:plays_instrument")
    private Set<Instrument> instruments;

    @ManyToMany
    @JoinTable(name = "user_plays_genre",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id"))
    @OntEntityField(type = OntEntityField.DataType.OBJECT, pred = "musicoo:plays_genre")
    private Set<Genre> genres;

    @OneToMany(mappedBy = "user")
    private Set<UserSetting> userSettings;

    @Column(name = "username", nullable = false, length = 50)
    @OntEntityField(type = OntEntityField.DataType.DATA, pred = ":username")
    private String username;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "password", length = 100)
    private String password;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "users", fetch = FetchType.EAGER)
    private Set<UserRole> userRoles;

    public Users() {
    }

    @Override
    public String toString() {
        return "Users{" +
                "firstName='" + firstName + '\'' +
                ", surname='" + surname + '\'' +
                ", codiceFiscale='" + codiceFiscale + '\'' +
                ", based_near=" + based_near +
                ", professionalLevel='" + professionalLevel + '\'' +
                ", expertiseLevel='" + expertiseLevel + '\'' +
                ", multiInstrumentalismLevel='" + multiInstrumentalismLevel + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", chatMessages=" + chatMessages +
                ", friendships=" + friendships +
                ", musicianGroups=" + musicianGroups +
                ", instruments=" + instruments +
                ", genres=" + genres +
                ", userSettings=" + userSettings +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", userId=" + userId +
                ", artist=" + artist +
                ", userRoles=" + userRoles +
                '}';
    }

    public String getUserIRI() {
        Namespace musinco = OntologyModel.getNamespace("");
        assert musinco != null;
        return musinco.getName() +
                "Users/" +
                userId;
    }

    public static Map<String, String> getFieldsPredicate() {
        Map<String, String> fields = new HashMap<>();
        for (Field field : Users.class.getDeclaredFields()) {
            OntEntityField annotation = field.getAnnotation(OntEntityField.class);
            if (annotation != null) {
                fields.put(field.getName().toLowerCase(), OntologyModel.getPredicate(annotation.pred()));
            }
        }
        return fields;
    }

}

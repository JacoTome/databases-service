package musico.services.databases.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import musico.services.databases.config.OntEntity;
import musico.services.databases.config.OntologyModel;

@Getter
@Setter
@Entity
@Table(name = "musical_work")
public class MusicalWork implements OntEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "musical_work_id", nullable = false)
    private Integer id;

    @Size(max = 400)
    @NotNull
    @Column(name = "title", nullable = false, length = 400)
    private String title;

    @Size(max = 20)
    @Column(name = "iswc", length = 20)
    private String iswc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_id")
    private Genre genre;

    @Size(max = 20)
    @Column(name = "release_type", length = 20)
    private String releaseType;

    @Size(max = 10)
    @Column(name = "`key`", length = 10)
    private String key;

    @Column(name = "bpm")
    private Integer bpm;

    @Size(max = 10)
    @Column(name = "meter", length = 10)
    private String meter;

    @Size(max = 4)
    @Column(name = "tempo", length = 4)
    private String tempo;

    @Size(max = 100)
    @Column(name = "album", length = 100)
    private String album;

    @Size(max = 100)
    @Column(name = "artist", length = 100)
    private String artist;

    @Override
    public String getIRI() {
        String namespace = OntologyModel.getNamespaceString("");
        return namespace + "MusicalWork/" + id;
    }

    @Override
    public String getClassIRI() {
        String namespace = OntologyModel.getNamespaceString("mo");
        return namespace + "MusicalWork";
    }
}
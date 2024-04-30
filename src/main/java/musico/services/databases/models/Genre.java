package musico.services.databases.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import musico.services.databases.config.OntologyModel;
import org.eclipse.rdf4j.model.Namespace;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@Table(name = "genre")
public class Genre  {
    @Id
    @Column(name = "genre_id", nullable = false)
    private Integer id;

    public Genre() {
    }

    public String getIRI() {
        Namespace musinco = OntologyModel.getNamespace("");
        assert musinco != null;
        return musinco.getName() +
                "Genre/" +
                id;
    }

    //TODO [JPA Buddy] generate columns from DB
}
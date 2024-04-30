package musico.services.databases.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@Table(name = "instruments")
public class Instrument {
    @Id
    @Column(name = "instrument_id", nullable = false)
    private Integer id;

    public Instrument() {
    }


    //TODO [JPA Buddy] generate columns from DB
}
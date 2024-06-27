package musico.services.databases.services.kafka;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import musico.services.databases.config.OntEntity;
import musico.services.databases.config.OntologyModel;
import musico.services.databases.models.Genre;
import musico.services.databases.models.MWork;
import musico.services.databases.models.kafka.MusicalWorkQueryParams;
import musico.services.databases.repositories.GenreRepository;
import org.eclipse.rdf4j.model.util.Values;
import org.eclipse.rdf4j.sparqlbuilder.core.SparqlBuilder;
import org.eclipse.rdf4j.sparqlbuilder.core.Variable;
import org.eclipse.rdf4j.sparqlbuilder.graphpattern.GraphPatternNotTriples;
import org.eclipse.rdf4j.sparqlbuilder.graphpattern.GraphPatterns;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@AllArgsConstructor
@Slf4j
public class MusicalWorkQueryParamsService {
    private final GenreRepository genreRepository;

    public GraphPatternNotTriples buildQueryGraphPattern(MusicalWorkQueryParams params) {
        GraphPatternNotTriples res = getQueryPreface();
        res.and(getQueryBody(params));
        return res;
    }

    private OntEntity getOntEntity(MusicalWorkQueryParams queryParams) {
        // TODO: Change in order to use more Genres, only one is used for now
        Genre genre = genreRepository.findByGenreNameLike(queryParams.genre()[0]);
        return MWork.builder()
                .genre(genre)
                .bpm(queryParams.bpm())
                .build();
    }

    private GraphPatternNotTriples getQueryPreface() {
        Variable musicalWork = SparqlBuilder.var("musicalWork");
        Variable user = SparqlBuilder.var("user");
        return GraphPatterns.and(user.has(
                p -> p.pred(Values.iri(Objects.requireNonNull(OntologyModel.getNamespace("musicoo")).getName() + "in_participation")).
                        then(Values.iri(Objects.requireNonNull(OntologyModel.getNamespace("musicoo")).getName() + "played_musical_work")),
                musicalWork));
    }

    public GraphPatternNotTriples getQueryBody(MusicalWorkQueryParams params) {
        MWork dataToQuery = (MWork) getOntEntity(params);
        return dataToQuery.buildGenericQueryGraphPattern(dataToQuery);
    }
}

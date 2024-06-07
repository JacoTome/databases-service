package musico.services.databases.utils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import musico.services.databases.config.OntologyModel;
import musico.services.databases.models.Users;
import musico.services.databases.models.kafka.MusicalWorkQueryParams;
import org.eclipse.rdf4j.model.util.Values;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.sparqlbuilder.core.SparqlBuilder;
import org.eclipse.rdf4j.sparqlbuilder.core.Variable;
import org.eclipse.rdf4j.sparqlbuilder.core.query.Queries;
import org.eclipse.rdf4j.sparqlbuilder.graphpattern.GraphPattern;
import org.eclipse.rdf4j.sparqlbuilder.graphpattern.GraphPatternNotTriples;
import org.eclipse.rdf4j.sparqlbuilder.graphpattern.GraphPatterns;
import org.eclipse.rdf4j.sparqlbuilder.rdf.Iri;
import org.eclipse.rdf4j.sparqlbuilder.rdf.Rdf;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@AllArgsConstructor
public class DataRetriever {

    private final RepositoryConnection connection;

    private GraphPatternNotTriples buildUserQueryGraphPattern(Integer id, String... fields) {
        Iri user = Rdf.iri(OntologyModel.getDefaultNamespace() + "Users/" + id);
        Iri schemaName = Rdf.iri("https://schema.org/name");
        GraphPattern graphPatterns = GraphPatterns.tp(
                user,
                RDF.TYPE,
                Rdf.iri(OntologyModel.getNamespace("musicoo").getName() + "HumanMusician"));
        Map<String, String> userPredicates = Users.getFieldsPredicate();
        GraphPatternNotTriples res = GraphPatterns.and(graphPatterns);
        for (String field : fields) {
            if (userPredicates.containsKey(field.toLowerCase())) {
                log.info("Field found: {}", userPredicates.get(field));
                GraphPattern namePattern = user.has(p -> p.pred(Values.iri(userPredicates.get(field))).then(schemaName),
                        SparqlBuilder.var(field + "Name"));
                GraphPattern pt = GraphPatterns.tp(user, Values.iri(userPredicates.get(field)), SparqlBuilder.var(field));

                res.and(GraphPatterns.optional(pt));
                res.and(GraphPatterns.optional(namePattern));
            }
        }
        return res;
    }

    private GraphPatternNotTriples buildMusicalWorkQueryGraphPattern(MusicalWorkQueryParams params) {
        Variable musicalWork = SparqlBuilder.var("musicalWork");
        Variable user = SparqlBuilder.var("user");
        GraphPatternNotTriples userMusicalWork = GraphPatterns.and(user.has(
                p -> p.pred(Values.iri(OntologyModel.getNamespace("musicoo").getName() + "in_participation")).
                        then(Values.iri(OntologyModel.getNamespace("musicoo").getName() + "played_musical_work")),
                musicalWork));
        Map<String, String> paramsList = MusicalWorkQueryParams.getFieldsPredicate();
        for (String field : paramsList.keySet()) {
            if (paramsList.containsKey(field)) {
                log.info("Field found: {}", paramsList.get(field));
                GraphPattern pt = GraphPatterns.tp(musicalWork, Values.iri(paramsList.get(field)), SparqlBuilder.var(field));
                userMusicalWork.and(GraphPatterns.optional(pt));
            }
        }
        log.info("Musical work: {}", userMusicalWork);
        return userMusicalWork;
    }


    public List<BindingSet> getUserData(Integer id, String... fields) {
        log.info("Getting data from graphDB");
        GraphPatternNotTriples graphPatterns = buildUserQueryGraphPattern(id, fields);
        return executeSelectQuery(graphPatterns);
    }


    public List<BindingSet> getMusicalWorkData(MusicalWorkQueryParams params) {
        GraphPatternNotTriples graphPatterns = buildMusicalWorkQueryGraphPattern(params);
        return executeSelectQuery(graphPatterns);
    }

    private void testConnection() {
        String query = Queries.SELECT().where(GraphPatterns.tp(
                SparqlBuilder.var("s"),
                SparqlBuilder.var("p"),
                SparqlBuilder.var("o")
        )).limit(1).getQueryString();
        TupleQuery tupleQuery = connection.prepareTupleQuery(query);
        try (TupleQueryResult result = tupleQuery.evaluate()) {
            while (result.hasNext()) {
                BindingSet bindingSet = result.next();
                log.info("Binding set: {}", bindingSet);
            }
        } catch (Exception e) {
            log.error("Testing connection failed: {}", e.getMessage());
        }
    }

    private List<BindingSet> executeSelectQuery(GraphPatternNotTriples graphPatterns) {
        ArrayList<BindingSet> res = new ArrayList<>();
        String query = Queries.SELECT().where(graphPatterns).getQueryString();
        TupleQuery tupleQuery = connection.prepareTupleQuery(query);
        try (TupleQueryResult result = tupleQuery.evaluate()) {
            while (result.hasNext()) {
                BindingSet bindingSet = result.next();
                res.add(bindingSet);
            }
        } catch (Exception e) {
            log.error("Error executing query: {}", e.getMessage());
        }
        return res;
    }
}

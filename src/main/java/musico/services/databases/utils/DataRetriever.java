package musico.services.databases.utils;

import lombok.extern.slf4j.Slf4j;
import musico.services.databases.config.OntologyModel;
import musico.services.databases.models.Users;
import org.eclipse.rdf4j.model.util.Values;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.sparqlbuilder.core.SparqlBuilder;
import org.eclipse.rdf4j.sparqlbuilder.core.query.Queries;
import org.eclipse.rdf4j.sparqlbuilder.graphpattern.GraphPattern;
import org.eclipse.rdf4j.sparqlbuilder.graphpattern.GraphPatternNotTriples;
import org.eclipse.rdf4j.sparqlbuilder.graphpattern.GraphPatterns;
import org.eclipse.rdf4j.sparqlbuilder.rdf.Iri;
import org.eclipse.rdf4j.sparqlbuilder.rdf.Rdf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//@Component
@Slf4j
public class DataRetriever {

    private final RepositoryConnection connection = null;

//    @Autowired
//    public DataRetriever(RepositoryConnection connection) {
//        this.connection = connection;
//    }


    private GraphPatternNotTriples buildGraphPattern(Integer id, String... fields) {

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

    public List<BindingSet> getUserData(Integer id, String... fields) {
        log.info("Getting data from graphDB");
        GraphPatternNotTriples graphPatterns = buildGraphPattern(id, fields);
        ArrayList<BindingSet> res = new ArrayList<>();
        try {
            String query = Queries.SELECT().where(graphPatterns).getQueryString();
            log.info("Query: {}", query);
            TupleQuery tupleQuery = connection.prepareTupleQuery(query);
            try (TupleQueryResult result = tupleQuery.evaluate()) {
                while (result.hasNext()) {
                    BindingSet bindingSet = result.next();
                    res.add(bindingSet);
                }
            } catch (Exception e) {
                log.error("Error executing query: {}", e.getMessage());
            }
        } catch (Exception e) {
            log.error("Error preparing query: {}", e.getMessage());
        }
        return res;
    }
}

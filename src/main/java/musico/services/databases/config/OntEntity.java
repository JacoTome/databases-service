package musico.services.databases.config;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.util.Values;
import org.eclipse.rdf4j.sparqlbuilder.core.SparqlBuilder;
import org.eclipse.rdf4j.sparqlbuilder.core.Variable;
import org.eclipse.rdf4j.sparqlbuilder.graphpattern.GraphPatternNotTriples;
import org.eclipse.rdf4j.sparqlbuilder.graphpattern.GraphPatterns;
import org.eclipse.rdf4j.sparqlbuilder.graphpattern.TriplePattern;

import java.lang.reflect.Field;
import java.util.*;

public interface OntEntity {

    IRI getIRI();

    Variable getVar();

    default List<TriplePattern> buildInsertQueryGraphPattern(OntEntity clazz) {
        Object entity;
        if (clazz.getIRI().toString().contains("null")) {
            entity = clazz.getVar();
        } else {
            entity = clazz.getIRI();
        }
        List<TriplePattern> res = new ArrayList<>();
        for (Field field : clazz.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            OntEntityField annotation = field.getAnnotation(OntEntityField.class);
            try {
                if (annotation != null && field.get(clazz) != null) {
                    switch (annotation.type()) {
                        case OBJECT:
                            IRI[] objs = getOntEntityIRIsArray(field.get(clazz));
                            if (entity instanceof Variable) {
                                res.add(
                                        GraphPatterns.tp(
                                                (Variable) entity,
                                                Values.iri(Objects.requireNonNull(OntologyModel.getPredicate(annotation.pred()))),
                                                (objs
                                                )
                                        )
                                );

                            } else {
                                res.add(
                                        GraphPatterns.tp(
                                                (Resource) entity,
                                                Values.iri(Objects.requireNonNull(OntologyModel.getPredicate(annotation.pred()))),
                                                (objs
                                                )
                                        )
                                );
                            }
                            break;
                        case DATA:
                            Object objValue = field.get(clazz);
                            if (entity instanceof Variable) {
                                res.add(
                                        GraphPatterns.tp(
                                                (Variable) entity,
                                                Values.iri(Objects.requireNonNull(OntologyModel.getPredicate(annotation.pred()))),
                                                Values.literal(objValue))
                                );
                            } else {
                                res.add(
                                        GraphPatterns.tp(
                                                (Resource) entity,
                                                Values.iri(Objects.requireNonNull(OntologyModel.getPredicate(annotation.pred()))),
                                                Values.literal(objValue))
                                );
                            }
                            break;
                    }
                }
            } catch (IllegalAccessException e) {
                System.out.println("Error while accessing field: " + e.getMessage());
            }
        }
        return res;
    }

    default GraphPatternNotTriples buildGenericQueryGraphPattern(OntEntity clazz) {
        Object entity;
        if (clazz.getIRI().toString().contains("null")) {
            entity = clazz.getVar();
        } else {
            entity = clazz.getIRI();
        }
        GraphPatternNotTriples res = GraphPatterns.and();
        List<GraphPatternNotTriples> patterns = new ArrayList<>();
        for (Field field : clazz.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            OntEntityField annotation = field.getAnnotation(OntEntityField.class);

            try {
                if (annotation != null && field.get(clazz) != null) {
                    if (!((Set<?>) field.get(clazz)).isEmpty()) {
                        switch (Objects.requireNonNull(annotation).type()) {
                            case OBJECT:
                                IRI[] objs = getOntEntityIRIsArray(field.get(clazz));
                                if (entity instanceof Variable) {
                                    patterns.add(
                                            GraphPatterns.optional(
                                                    GraphPatterns.tp(
                                                            (Variable) entity,
                                                            Values.iri(Objects.requireNonNull(OntologyModel.getPredicate(annotation.pred()))),
                                                            objs
                                                    )
                                            )
                                    );

                                } else {
                                    patterns.add(
                                            GraphPatterns.optional(
                                                    GraphPatterns.tp(
                                                            (Resource) entity,
                                                            Values.iri(Objects.requireNonNull(OntologyModel.getPredicate(annotation.pred()))),
                                                            objs
                                                    )
                                            )
                                    );
                                }
                                break;
                            case DATA:
                                Object objValue = field.get(clazz);
                                if (entity instanceof Variable) {
                                    patterns.add(
                                            GraphPatterns.optional(
                                                    GraphPatterns.tp(
                                                            (Variable) entity,
                                                            Values.iri(Objects.requireNonNull(OntologyModel.getPredicate(annotation.pred()))),
                                                            Values.literal(objValue))
                                            )
                                    );
                                } else {
                                    patterns.add(
                                            GraphPatterns.optional(
                                                    GraphPatterns.tp(
                                                            (Resource) entity,
                                                            Values.iri(Objects.requireNonNull(OntologyModel.getPredicate(annotation.pred()))),
                                                            Values.literal(objValue))
                                            )
                                    );
                                }
                                break;
                        }
                    } else {
                        if (entity instanceof Variable) {
                            patterns.add(
                                    GraphPatterns.optional(
                                            GraphPatterns.tp(
                                                    (Variable) entity,
                                                    Values.iri(Objects.requireNonNull(OntologyModel.getPredicate(annotation.pred()))),
                                                    SparqlBuilder.var(field.getName())
                                            )
                                    )
                            );
                        } else {
                            patterns.add(
                                    GraphPatterns.optional(
                                            GraphPatterns.tp(
                                                    (Resource) entity,
                                                    Values.iri(Objects.requireNonNull(OntologyModel.getPredicate(annotation.pred()))),
                                                    SparqlBuilder.var(field.getName())
                                            )
                                    )
                            );
                        }
                    }
                } else if (annotation != null) {
                    if (entity instanceof Variable) {
                        patterns.add(
                                GraphPatterns.optional(
                                        GraphPatterns.tp(
                                                (Variable) entity,
                                                Values.iri(Objects.requireNonNull(OntologyModel.getPredicate(annotation.pred()))),
                                                SparqlBuilder.var(field.getName())
                                        )
                                )
                        );
                    } else {
                        patterns.add(
                                GraphPatterns.optional(
                                        GraphPatterns.tp(
                                                (Resource) entity,
                                                Values.iri(Objects.requireNonNull(OntologyModel.getPredicate(annotation.pred()))),
                                                SparqlBuilder.var(field.getName())
                                        )
                                )
                        );
                    }
                }
            } catch (IllegalAccessException e) {
                System.out.println("Error while accessing field: " + e.getMessage());
            }
        }
        res.and(patterns.toArray(new GraphPatternNotTriples[0]));
        return res;
    }

    private IRI[] getOntEntityIRIsArray(Object entitySet) {
        if (entitySet instanceof Set) {
            Set<OntEntity> entities = (Set<OntEntity>) entitySet;
            IRI[] iriArray = new IRI[entities.size()];
            int i = 0;
            for (OntEntity entity : entities) {
                if (entity == null) {
                    continue;
                }
                iriArray[i] = entity.getIRI();
                i++;
            }
            return iriArray;
        } else {
            return new IRI[]{((OntEntity) entitySet).getIRI()};
        }
    }
}

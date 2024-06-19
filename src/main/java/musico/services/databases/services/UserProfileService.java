package musico.services.databases.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
//import musico.services.user.dto.AuthProcessDTO;
import musico.services.databases.enums.REGISTRATION_ENUMS;
import musico.services.databases.models.kafka.UsersQueryParams;
import musico.services.databases.services.kafka.UsersQueryParamsService;
import musico.services.databases.utils.DataGenerator;
import musico.services.databases.utils.DataRetriever;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.sparqlbuilder.graphpattern.GraphPatternNotTriples;
import org.eclipse.rdf4j.sparqlbuilder.graphpattern.TriplePattern;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class UserProfileService {
    private final ArtistService artistService;
    private final DataRetriever dataRetriever;
    private final DataGenerator dataGenerator;
    private final UsersQueryParamsService usersQueryParamsService;

    public REGISTRATION_ENUMS checkProfileAlreadyExists(UsersQueryParams userID) {
        // Check if user already exists in graphDB
        // If user exists, return REGISTRATION_ENUMS.USER_ALREADY_EXISTS
        GraphPatternNotTriples query = usersQueryParamsService.checkUserExists(userID);
        List<BindingSet> results = dataRetriever.createAndExecuteSelectQuery(query);
        if (!results.isEmpty()) {
            return REGISTRATION_ENUMS.CHECK_USER_EXISTS;
        }
        return REGISTRATION_ENUMS.CHECK_VALID;
    }

    public void createUserProfile(UsersQueryParams signupData) throws IllegalAccessException {
        List<TriplePattern> query = usersQueryParamsService.buildInsertQueryGraphPattern(signupData);
        dataRetriever.createAndExecuteInsertQuery(query);
    }

    public UsersQueryParams getUserProfile(UsersQueryParams userSignup) {
        GraphPatternNotTriples userQuery = usersQueryParamsService.buildQueryGraphPattern(userSignup);
        log.info("User Query: {}", userQuery.getQueryString());
        List<BindingSet> results = dataRetriever.createAndExecuteSelectQuery(userQuery);
        if (results.isEmpty()) {
            log.error("No results found for user: {}", userSignup);
            return null;
        }
        return usersQueryParamsService.getResponseMessageFromQueryResults(results).build();
    }
}

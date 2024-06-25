package musico.services.databases.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import musico.services.databases.enums.REGISTRATION_ENUMS;
import musico.services.databases.models.Users;
import musico.services.databases.models.kafka.UsersQueryParams;
import musico.services.databases.services.kafka.UsersQueryParamsService;
import musico.services.databases.utils.DataRetriever;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.sparqlbuilder.graphpattern.GraphPatternNotTriples;
import org.eclipse.rdf4j.sparqlbuilder.graphpattern.TriplePattern;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class UserProfileService {
    private final DataRetriever dataRetriever;
    private final UsersQueryParamsService usersQueryParamsService;
    private final UserService userService;

    public REGISTRATION_ENUMS checkProfileAlreadyExists(UsersQueryParams userID) {
        GraphPatternNotTriples query = usersQueryParamsService.checkUserExists(userID);
        List<BindingSet> results = dataRetriever.createAndExecuteSelectQuery(query);
        if (!results.isEmpty()) {
            return REGISTRATION_ENUMS.CHECK_USER_EXISTS;
        }
        return REGISTRATION_ENUMS.CHECK_VALID;
    }

    public void createUserProfile(UsersQueryParams signupData) {
        List<TriplePattern> query = usersQueryParamsService.buildInsertQueryGraphPattern(signupData);
        userService.createUserProfile(signupData);
        dataRetriever.createAndExecuteInsertQuery(query);
    }

    public UsersQueryParams getUserProfile(UsersQueryParams userSignup) {
        Users user = userService.getUserProfile(userSignup.userId());
        GraphPatternNotTriples userQuery = usersQueryParamsService.buildQueryGraphPattern(userSignup);
        log.info("User Query: {}", userQuery.getQueryString());
        List<BindingSet> results = dataRetriever.createAndExecuteSelectQuery(userQuery);
        if (results.isEmpty()) {
            log.error("No results found for user: {}", userSignup);
            return null;
        }
        UsersQueryParams.UsersQueryParamsBuilder builder = usersQueryParamsService.getResponseMessageFromQueryResults(results);
        builder.userId(userSignup.userId())
                .firstName(user.getFirstName())
                .surname(user.getSurname())
                .amazonMusic(user.getAmazonMusic())
                .appleMusic(user.getAppleMusic())
                .birthdate(user.getBirthdate())
                .description(user.getDescription())
                .profilePicturePath(user.getProfilePicturePath())
                .soundcloud(user.getSoundcloud())
                .spotify(user.getSpotify())
                .tidal(user.getTidal())
                .youtube(user.getYoutube());
        return builder.build();

    }
}

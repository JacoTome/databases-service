package musico.services.databases.models.kafka;

import lombok.Builder;

@Builder
public record UsersQueryParams (String requestID, String userId, String firstName, String surname, String[] genres, String[] instruments) {}

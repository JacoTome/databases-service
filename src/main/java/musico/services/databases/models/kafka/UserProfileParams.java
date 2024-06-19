package musico.services.databases.models.kafka;

public record UserProfileParams (String userId, String username, String[] genres, String[] instruments) { }

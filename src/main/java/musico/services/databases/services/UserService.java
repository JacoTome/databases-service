package musico.services.databases.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import musico.services.databases.models.Users;
import musico.services.databases.models.kafka.UsersQueryParams;
import musico.services.databases.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {

    private UserRepository userRepository;

    public Users getUserProfile(String userId) {
        return userRepository.findByUserId(userId);
    }

    public void createUserProfile(UsersQueryParams user) {
        Users userToSave = Users.builder()
                .userId(user.userId())
                .firstName(user.firstName())
                .surname(user.surname())
                .birthdate(user.birthdate())
                .description(user.description())
                .profilePicturePath(user.profilePicturePath())
                .spotify(user.spotify())
                .youtube(user.youtube())
                .soundcloud(user.soundcloud())
                .appleMusic(user.appleMusic())
                .tidal(user.tidal())
                .amazonMusic(user.amazonMusic())
                .build();
        userRepository.save(userToSave);
    }
}
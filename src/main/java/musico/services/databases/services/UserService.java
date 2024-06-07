package musico.services.databases.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import musico.services.databases.config.ERole;
import musico.services.databases.enums.REGISTRATION_ENUMS;
import musico.services.databases.models.Artist;
import musico.services.databases.models.UserRole;
import musico.services.databases.models.UserRoleId;
import musico.services.databases.models.Users;
import musico.services.databases.repositories.RoleRepository;
import musico.services.databases.repositories.UserRepository;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private DataSenderService dataSenderService;
    private ArtistService artistService;

    /**
     * Register a new user.
     *
     * @param artist   the artist object associated with the user
     * @param username the username of the user
     * @param password the password of the user
     * @param email    the email
     *                 1. Create a new Artist
     *                 2. Create a new User based on the Artist
     *                 3. Send to GraphDB
     *                 4. Query roles from req body and assign to user
     *                 4.1 Set default if not found
     *                 5. Send Response of the user
     */
    public REGISTRATION_ENUMS registerUser(Artist artist, String username, String password, String email) {

        log.info("Registering user: {}", username);
        Users user = Users.builder()
                .userId(artist.getArtistId())
                .artist(artist)
                .username(username)
                .password(password)
                .email(email)
                .build();
        // Create default user role
        UserRoleId userRoleId = UserRoleId.builder()
                .userId(user.getUserId())
                .roleId(1)
                .build();
        UserRole newUserRole = UserRole.builder()
                .id(userRoleId)
                .users(user)
                .roles(roleRepository.findFirstByName(ERole.user))
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        user.setUserRoles(Set.of(newUserRole));
        try {
            userRepository.save(user);
        } catch (Exception e) {
            log.error("Error registering user: {}", e.getMessage());
            log.error("DELETE ARTIST: {}", artist.getArtistId());
            artistService.deleteArtist(artist);
            return REGISTRATION_ENUMS.REGISTRATION_FAILED_USER_SAVE;
        }
        dataSenderService.sendUser(user);
        return REGISTRATION_ENUMS.REGISTRATION_SUCCESS;
    }

    public REGISTRATION_ENUMS login(String username, String password) {
        Users user = userRepository.findFirstByUsername(username);
        if (user == null) {
            return REGISTRATION_ENUMS.LOGIN_FAILED_USER_NOT_FOUND;
        }
        if (!BCrypt.checkpw(password, user.getPassword())) {
            log.info("User password is incorrect : {} {}", password, user.getPassword());
            return REGISTRATION_ENUMS.LOGIN_FAILED_WRONG_PASSWORD;
        }
        return REGISTRATION_ENUMS.LOGIN_SUCCESS;
    }

    /**
     * Check if a user or email already exists
     *
     * @param username username of the user
     * @param email    email of the user
     * @return true if user or email already exists
     */
    public boolean checkDuplicateUserOrEmail(String username, String email) {
        return userRepository.existsByUsername(username) || userRepository.existsByEmail(email);
    }

}
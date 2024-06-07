package musico.services.databases.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
//import musico.services.user.dto.AuthProcessDTO;
import musico.services.databases.enums.REGISTRATION_ENUMS;
import musico.services.databases.models.Artist;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class AuthProcessService {
    private final UserService userService;
    private final ArtistService artistService;

    public REGISTRATION_ENUMS checkSignup(String username, String email) {
        if (artistService.checkArtistExists(username)) {
            log.info("Artist already exists : {}", username);
            return REGISTRATION_ENUMS.CHECK_ARTIST_EXISTS;
        }
        if (userService.checkDuplicateUserOrEmail(username, email)) {
            return REGISTRATION_ENUMS.CHECK_USERNAME_EXISTS;
        }
        return REGISTRATION_ENUMS.CHECK_VALID;
    }
//    public REGISTRATION_ENUMS registerUser(AuthProcessDTO userSignup) {
//        Artist artist = artistService.registerArtist(userSignup.getUsername());
//        if (artist == null) {
//            return REGISTRATION_ENUMS.REGISTRATION_FAILED_ARTIST_SAVE;
//        }
//        return userService.registerUser(artist, userSignup.getUsername(), userSignup.getPassword(), userSignup.getEmail());
//    }

    public REGISTRATION_ENUMS login(String username, String password) {
        return userService.login(username, password);
    }
    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(10));
    }
}

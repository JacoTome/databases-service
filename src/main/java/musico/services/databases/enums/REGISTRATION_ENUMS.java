package musico.services.databases.enums;

import lombok.Getter;

@Getter
public enum REGISTRATION_ENUMS {
    /*
     * CHECK OPERATION PREFIX = 1
     * REGISTER OPERATION PREFIX = 2
     * */
    CHECK_VALID(10),
    CHECK_ARTIST_EXISTS(11),
    CHECK_USERNAME_EXISTS(12),
    CHECK_EMAIL_EXISTS(13),

    REGISTRATION_SUCCESS(20),
    REGISTRATION_FAILED(21),
    REGISTRATION_FAILED_ARTIST_SAVE(22),
    REGISTRATION_FAILED_USER_SAVE(23);
    final int value;

    REGISTRATION_ENUMS(int i) {
        this.value = i;
    }
}

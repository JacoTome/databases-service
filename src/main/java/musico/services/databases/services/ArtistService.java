package musico.services.databases.services;

import jakarta.transaction.Transactional;
import musico.services.databases.models.Artist;
import musico.services.databases.repositories.ArtistRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Transactional
public class ArtistService {

    private static final Logger log = LoggerFactory.getLogger(ArtistService.class);
    private final ArtistRepository artistRepository;

    @Autowired
    public ArtistService(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    public Artist registerArtist(String artist_name) {
        log.info("Registering artist: {}", artist_name);
        Artist artist = Artist.builder()
                .artistName(artist_name)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        try {
            return artistRepository.save(artist);
        } catch (Exception e) {
            log.error("Error registering artist: {}", e.getMessage());
            return null ;

        }
    }

    public void deleteArtist(Artist artist) {
        artistRepository.delete(artist);
    }

    public Boolean checkArtistExists(String artist_name) {
        return artistRepository.existsByArtistName(artist_name);
    }
}

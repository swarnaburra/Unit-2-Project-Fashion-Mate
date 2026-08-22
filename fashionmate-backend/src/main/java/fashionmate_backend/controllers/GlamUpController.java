package fashionmate_backend.controllers;

import fashionmate_backend.models.GlamUp;
import fashionmate_backend.repositories.GlamUpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Exposes the "GlamUp" trending style content used by the frontend's
 * glam-up feature.
 */
@RestController
@RequestMapping("/api/glamup")
@CrossOrigin()
public class GlamUpController {

    @Autowired
    private GlamUpRepository glamUpRepository;

    /**
     * Handles {@code GET /api/glamup}.
     * <p>
     * Returns the first {@link GlamUp} record found in the repository.
     *
     * @return the first {@link GlamUp} entity in the data store
     * @throws java.util.NoSuchElementException if no {@link GlamUp} records exist,
     *         since the underlying list will be empty and {@code getFirst()} will fail
     */
    @GetMapping()
    public GlamUp getGlamUpBy() {
        final var glamUps = glamUpRepository.findAll();
        return glamUps.getFirst();
    }

}

package fashionmate_backend.controllers;

import fashionmate_backend.models.GlamUp;
import fashionmate_backend.repositories.GlamUpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/glamup")
@CrossOrigin()
public class GlamUpController {

    @Autowired
    private GlamUpRepository glamUpRepository;

    @GetMapping()
    public GlamUp getGlamUpBy() {
        final var glamUps = glamUpRepository.findAll();
        return glamUps.getFirst();
    }

}

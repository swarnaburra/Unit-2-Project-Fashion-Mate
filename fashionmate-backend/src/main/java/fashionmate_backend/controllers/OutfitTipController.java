package fashionmate_backend.controllers;


import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Random;

@RestController
@CrossOrigin()
@RequestMapping("/api/outfit-tip")
public class OutfitTipController {

    private final List<String> tips = List.of(
            "Pair your belt with your shoes for a clean look.",
            "Add a statement accessory to elevate a simple outfit.",
            "Mix textures to add depth to your outfit.",
            "Roll up your sleeves for a relaxed vibe.",
            "Neutral colors are versatile for any occasion.",
            "Experiment with bold colors for a fun weekend.",
            "Keep it comfy but stylish for a relaxed day."
    );

    private final Random random = new Random();

    @GetMapping()
    public String getRandomTip(){
        return tips.get(random.nextInt(tips.size()));
    }

}

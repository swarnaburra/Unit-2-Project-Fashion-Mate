package fashionmate_backend.controllers;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OutfitTipControllerTest {

    private static final List<String> KNOWN_TIPS = List.of(
            "Pair your belt with your shoes for a clean look.",
            "Add a statement accessory to elevate a simple outfit.",
            "Mix textures to add depth to your outfit.",
            "Roll up your sleeves for a relaxed vibe.",
            "Neutral colors are versatile for any occasion.",
            "Experiment with bold colors for a fun weekend.",
            "Keep it comfy but stylish for a relaxed day."
    );

    private final OutfitTipController controller = new OutfitTipController();

    @Test
    void getRandomTipReturnsOneOfTheKnownTips() {
        for (int i = 0; i < 25; i++) {
            assertThat(controller.getRandomTip()).isIn(KNOWN_TIPS);
        }
    }
}

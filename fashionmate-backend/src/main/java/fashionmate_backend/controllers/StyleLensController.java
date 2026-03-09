package fashionmate_backend.controllers;

import com.google.genai.Client;
import com.google.genai.types.*;
import fashionmate_backend.models.ImageRequest;
import fashionmate_backend.models.StyleLens;
import fashionmate_backend.models.User;
import fashionmate_backend.repositories.StyleLensRepository;
import fashionmate_backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.Optional;

@RestController
@RequestMapping("/api/stylelens")
@CrossOrigin
public class StyleLensController {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Autowired
    private StyleLensRepository styleLensRepository;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/processImage/{userId}")
    public String processImage(@PathVariable String userId,
                               @RequestBody ImageRequest imageRequest) throws Exception {


        //Lookup user using user id
        final Optional<User> user = userRepository.findById(Long.valueOf(userId));

        if(user.isEmpty()) {
            throw new Exception("Wrong user");
        }

        //create prompt
        String prompt = "You are a fashion expert. Analyze the outfit in the image and provide feedback based on the" +
                "        Step 1: Determine if the image contains an outfit. Make sure it's an outfit and not an object like building, car etc. " +
                " Make sure the outfit is appropriate for occasion: " + imageRequest.getOccasion() +  ", age: " + imageRequest.getAge()  + " and preference: " + imageRequest.getPreference() +
                "        Step 2: If yes, say either 'YAY' or 'NAY' about the outfit quality." +
                "        Step 3: If NAY, give 2–3 specific improvement suggestions." +
                "                Keep response short, friendly, and easy to read. ";
        //generate content
        final String geminiResponse = generateContent(prompt, imageRequest);

        final boolean isYay = geminiResponse.contains("YAY");
        StyleLens styleLens = new StyleLens();
        styleLens.setImage(imageRequest.getContents());
        styleLens.setDecision(isYay ? "YAY" : "NAY");
        styleLens.setUser(user.get());
        styleLensRepository.save(styleLens);
        return geminiResponse;
    }

    public String generateContent(String prompt, ImageRequest imageRequest) {
        Client client = Client.builder().apiKey(apiKey).build();

        String modelName = "gemini-2.5-flash";


// 2. Decode the base64 contents sent from your JS request
        byte[] imageBytes = Base64.getDecoder().decode(imageRequest.getContents());

// 3. Combine text and image into one Content object
        Content multimodalContent = Content.fromParts(
                Part.fromText(prompt),
                Part.fromBytes(imageBytes, imageRequest.getMimetype())
        );

// 4. Send to Gemini
        GenerateContentResponse response = client.models.generateContent(
                modelName,
                multimodalContent,
                null
        );

        return response.text();
    }


}



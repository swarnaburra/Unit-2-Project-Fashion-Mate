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

/**
 * Analyzes outfit photos using the Gemini API and records the resulting
 * verdict as a {@link StyleLens} entry.
 */
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

    /**
     * Handles {@code POST /api/stylelens/processImage/{userId}}.
     * <p>
     * Looks up the given user, builds a fashion-review prompt from the
     * request's occasion/age/preference fields, sends the prompt and image
     * to Gemini via {@link #generateContent(String, ImageRequest)}, and
     * persists a {@link StyleLens} record capturing the image and whether
     * the outfit was judged "YAY" or "NAY".
     *
     * @param userId the id of the user submitting the image, as a string
     * @param imageRequest the request body containing the base64 image
     *        contents, its mimetype, and the occasion/age/preference context
     * @return the raw text response returned by the Gemini model
     * @throws Exception if no user exists with the given {@code userId}, or if
     *         the underlying Gemini request fails
     */
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

    /**
     * Sends a text prompt together with an image to the Gemini model and
     * returns its text response.
     * <p>
     * Not a REST endpoint; this is a helper used by
     * {@link #processImage(String, ImageRequest)} to decode the base64 image
     * contents, combine them with the prompt, and invoke the Gemini client.
     *
     * @param prompt the text prompt describing what analysis to perform
     * @param imageRequest the request containing the base64-encoded image
     *        contents and its mimetype
     * @return the text of the Gemini model's response
     */
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



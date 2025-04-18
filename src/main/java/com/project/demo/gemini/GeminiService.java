package com.project.demo.gemini;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;

import com.project.demo.gemini.GeminiRecords.*;

@Service
public class GeminiService {
    public static final String GEMINI_FLASH = "gemini-2.0-flash";


    private final GeminiInterface geminiInterface;

    @Autowired
    public GeminiService(GeminiInterface geminiInterface) {
        this.geminiInterface = geminiInterface;
    }

    public GeminiRecords.ModelList getModels() {
        return geminiInterface.getModels();
    }

    public GeminiCountResponse countTokens(String model, GeminiRequest request) {
        return geminiInterface.countTokens(model, request);
    }
//
//    public int countTokens(String text) {
//        GeminiCountResponse response = countTokens(GEMINI_PRO, new GeminiRequest(
//                List.of(new Content(List.of(new TextPart(text))))));
//        return response.totalTokens();
//    }

    public GeminiResponse getCompletion(GeminiRequest request) {
        return geminiInterface.getCompletion(GEMINI_FLASH, request);
    }

    public GeminiResponse getCompletionWithModel(String model, GeminiRequest request) {
        return geminiInterface.getCompletion(model, request);
    }


//    public GeminiResponse getCompletionWithImage(GeminiRequest request) {
//        return geminiInterface.getCompletion(GEMINI_PRO_VISION, request);
//    }
//
//    public GeminiResponse analyzeImage(GeminiRequest request) {
//        return geminiInterface.getCompletion(GEMINI_1_5_PRO, request);
//    }

    public String getCompletion(String text) {
        GeminiResponse response = getCompletion(new GeminiRequest(
                List.of(new Content(List.of(new TextPart(text))))));
        return response.candidates().get(0).content().parts().get(0).text();
    }

//    public String getCompletionWithImage(String text, String imageFileName) throws IOException {
//        GeminiResponse response = getCompletionWithImage(
//                new GeminiRequest(List.of(new Content(List.of(
//                        new TextPart(text),
//                        new InlineDataPart(new InlineData("image/png",
//                                Base64.getEncoder().encodeToString(Files.readAllBytes(
//                                        Path.of("src/main/resources/", imageFileName))))))
//                ))));
//        System.out.println(response);
//        return response.candidates().getFirst().content().parts().getFirst().text();
//    }
//
//    public String analyzeImage(String text, String imageFileName) throws IOException {
//        GeminiResponse response = analyzeImage(
//                new GeminiRequest(List.of(new Content(List.of(
//                        new TextPart(text),
//                        new InlineDataPart(new InlineData("image/png",
//                                Base64.getEncoder().encodeToString(Files.readAllBytes(
//                                        Path.of("src/main/resources/", imageFileName))))))
//                ))));
//        System.out.println(response);
//        return response.candidates().getFirst().content().parts().getFirst().text();
//    }
}

package com.project.demo;

import com.project.demo.gemini.GeminiInterface;
import com.project.demo.gemini.GeminiRecords.*;
import com.project.demo.service.GeminiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class GeminiServiceTest {

    @Mock
    private GeminiInterface geminiInterface;

    @InjectMocks
    private GeminiService geminiService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetCompletionString() {

        String inputText = "Hello Gemini!";
        String expectedText = "Hello from Gemini!";


        TextPart textPart = new TextPart(expectedText);
        GeminiResponse.Candidate.Content content =
                new GeminiResponse.Candidate.Content(List.of(textPart), "model");
        GeminiResponse.Candidate candidate =
                new GeminiResponse.Candidate(content, "finish", 0, List.of());

        GeminiResponse mockResponse = new GeminiResponse(List.of(candidate), null);

        when(geminiInterface.getCompletion(eq(GeminiService.GEMINI_FLASH), any(GeminiRequest.class)))
                .thenReturn(mockResponse);


        String actual = geminiService.getCompletion(inputText);
        
        assertEquals(expectedText, actual);
    }
}

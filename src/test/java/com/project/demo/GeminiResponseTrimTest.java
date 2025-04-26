package com.project.demo;

import com.project.demo.service.GeminiService;
import com.project.demo.service.TypingService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GeminiResponseTrimTest {

    @InjectMocks
    private TypingService service;

    @Test
    public void testCleanGeminiResponse_withJsonBlock() {

        String input = "```json\n{\n  \"key\": \"value\"\n}\n```";
        String expected = "{\n  \"key\": \"value\"\n}";

        String result = service.cleanGeminiResponse(input);

        assertEquals(expected, result);
    }

    @Test
    public void testCleanGeminiResponse_withExtraWhitespace() {

        String input = "   ```\n   {\"key\":\"value\"}   \n```   ";
        String expected = "{\"key\":\"value\"}";

        String result = service.cleanGeminiResponse(input);

        assertEquals(expected, result);
    }

    @Test
    public void testCleanGeminiResponse_withoutJsonLabel() {


        String input = "```{\n\"question\": \"What is AI?\"}```";
        String expected = "{\n\"question\": \"What is AI?\"}";

        String result = service.cleanGeminiResponse(input);

        assertEquals(expected, result);
    }

    @Test
    public void testCleanGeminiResponse_withUpperCaseJsonLabel() {
        String input = "```JSON\n{\"test\":true}```";
        String expected = "{\"test\":true}";

        String result = service.cleanGeminiResponse(input);

        assertEquals(expected, result);
    }
}

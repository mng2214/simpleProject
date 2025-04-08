package com.tss;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class OpenAIManager {

    private final String OPENAI_API_KEY = "API_KEY";

    public List<String> translateNamesWithChatGPT(List<String> ukrainianNames) {

        OpenAiService service = new OpenAiService(OPENAI_API_KEY);

        String prompt = "Translate these Ukrainian filenames to English (only the base name, no extensions) and keep number but exclude special characters. If you do not understand a word because its some name then just replace that letters with english letters:\n" +
                ukrainianNames.stream().map(n -> "- " + n).collect(Collectors.joining("\n"));

        ChatMessage systemMessage = new ChatMessage("system", "You are a helpful translator.");
        ChatMessage userMessage = new ChatMessage("user", prompt);

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model("gpt-3.5-turbo")
                .messages(List.of(systemMessage, userMessage))
                .temperature(0.3)
                .maxTokens(300)
                .build();

        String response = service.createChatCompletion(request)
                .getChoices()
                .get(0)
                .getMessage()
                .getContent()
                .trim();

        return Arrays.stream(response.split("\n"))
                .map(String::trim)
                .collect(Collectors.toList());
    }
}

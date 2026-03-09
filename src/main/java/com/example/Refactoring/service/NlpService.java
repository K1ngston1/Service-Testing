package com.example.Refactoring.service;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Service
public class NlpService {

    private final Tokenizer tokenizer;
    private final NameFinderME locationFinder;
    private final NameFinderME dateFinder;
    private final NameFinderME personFinder;
    private final NameFinderME moneyFinder;

    // Словник українських міст
    private final Set<String> cities = new HashSet<>(Arrays.asList(
            "Kyiv", "Lviv", "Odesa", "Dnipro", "Kharkiv", "Zaporizhzhia"
    ));

    public NlpService() {
        try {
            InputStream tokenModelStream = getClass().getResourceAsStream("/models/en-token.bin");
            if (tokenModelStream == null) throw new RuntimeException("Cannot find en-token.bin");
            tokenizer = new TokenizerME(new TokenizerModel(tokenModelStream));

            locationFinder = new NameFinderME(loadModel("/models/en-ner-location.bin"));
            dateFinder = new NameFinderME(loadModel("/models/en-ner-date.bin"));
            personFinder = new NameFinderME(loadModel("/models/en-ner-person.bin"));
            moneyFinder = new NameFinderME(loadModel("/models/en-ner-money.bin"));
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize NLP Service: " + e.getMessage(), e);
        }
    }

    private TokenNameFinderModel loadModel(String path) throws Exception {
        InputStream modelStream = getClass().getResourceAsStream(path);
        if (modelStream == null) throw new RuntimeException("Cannot find model file: " + path);
        return new TokenNameFinderModel(modelStream);
    }

    // --- Визначення наміру ---
    public String detectIntent(String text) {
        if (text == null || text.isEmpty()) return "unknown";

        String[] tokensLower = tokenizer.tokenize(text.toLowerCase());
        String[] tokensOriginal = tokenizer.tokenize(text);

        // 1️⃣ Погода: ключові слова
        if (Arrays.stream(tokensLower).anyMatch(t ->
                t.equals("weather") || t.equals("temperature") || t.equals("forecast")))
            return "weather";

        // 2️⃣ Погода: місто зі словника
        for (String city : cities) {
            if (text.toLowerCase().contains(city.toLowerCase())) return "weather";
        }

        // 3️⃣ Валюта
        if (Arrays.stream(tokensLower).anyMatch(t ->
                t.equals("currency") || t.equals("rate") || t.equals("exchange")
                        || t.equals("usd") || t.equals("uah") || t.matches("\\$")))
            return "currency";

        // 4️⃣ NER для грошей
        if (moneyFinder.find(tokensOriginal).length > 0) return "currency";

        // 5️⃣ Просте число → валюта
        for (String token : tokensLower) {
            if (token.matches("\\d+(\\.\\d+)?")) return "currency";
        }

        return "unknown";
    }

    // --- Витяг параметрів ---
    public String extractParameter(String text, String intent) {
        if (text == null || text.isEmpty()) return "";

        switch (intent) {
            case "weather":
                return extractCity(text);

            case "currency":
                return extractMoney(text);

            default:
                return "";
        }
    }

    // --- Допоміжні методи ---
    private String extractCity(String text) {
        // 1️⃣ Перевірка словника міст
        for (String city : cities) {
            if (text.toLowerCase().contains(city.toLowerCase())) return city;
        }

        // 2️⃣ Після слова "in": "weather in Kyiv"
        String[] tokens = tokenizer.tokenize(text);
        for (int i = 0; i < tokens.length - 1; i++) {
            if (tokens[i].equalsIgnoreCase("in")) return tokens[i + 1];
        }

        // 3️⃣ NER як резерв
        Span[] spans = locationFinder.find(tokens);
        if (spans.length > 0) return concatTokens(tokens, spans[0]);

        return ""; // не знайдено
    }

    private String extractMoney(String text) {
        String[] tokens = tokenizer.tokenize(text);

        for (String token : tokens) {
            // "$100", "50 USD" тощо
            if (token.matches("\\$\\d+(\\.\\d+)?")) return "USD " + token.replace("$", "");
            if (token.matches("\\d+(\\.\\d+)?")) return "USD " + token;
        }

        // NER для грошей
        Span[] spans = moneyFinder.find(tokens);
        if (spans.length > 0) return concatTokens(tokens, spans[0]);

        return text.trim();
    }

    private String concatTokens(String[] tokens, Span span) {
        StringBuilder sb = new StringBuilder();
        for (int i = span.getStart(); i < span.getEnd(); i++) {
            sb.append(tokens[i]);
            if (i < span.getEnd() - 1) sb.append(" ");
        }
        return sb.toString();
    }

    // --- Дебаг: вивід усіх сутностей ---
    public String debugEntities(String text) {
        String[] tokens = tokenizer.tokenize(text);
        StringBuilder sb = new StringBuilder();
        sb.append("🔹 Cities: ").append(Arrays.toString(locationFinder.find(tokens))).append("\n");
        sb.append("🔹 Dates: ").append(Arrays.toString(dateFinder.find(tokens))).append("\n");
        sb.append("🔹 Persons: ").append(Arrays.toString(personFinder.find(tokens))).append("\n");
        sb.append("🔹 Money: ").append(Arrays.toString(moneyFinder.find(tokens))).append("\n");
        return sb.toString();
    }
}
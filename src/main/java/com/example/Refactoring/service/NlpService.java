package com.example.Refactoring.service;

import com.example.Refactoring.model.Reminder;
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
    private final NameFinderME moneyFinder;

    // словник міст
    private final Set<String> cities = new HashSet<>(Arrays.asList(
            "Kyiv","Lviv","Odesa","Dnipro","Kharkiv","Zaporizhzhia",
            "Київ","Львів","Одеса","Дніпро","Харків"
    ));

    public NlpService() {

        try {

            InputStream tokenModelStream =
                    getClass().getResourceAsStream("/models/en-token.bin");

            if(tokenModelStream == null)
                throw new RuntimeException("Cannot find en-token.bin");

            tokenizer = new TokenizerME(new TokenizerModel(tokenModelStream));

            locationFinder =
                    new NameFinderME(loadModel("/models/en-ner-location.bin"));

            moneyFinder =
                    new NameFinderME(loadModel("/models/en-ner-money.bin"));

        } catch (Exception e) {

            throw new RuntimeException("Failed to initialize NLP Service", e);

        }

    }

    private TokenNameFinderModel loadModel(String path) throws Exception {

        InputStream modelStream = getClass().getResourceAsStream(path);

        if(modelStream == null)
            throw new RuntimeException("Cannot find model file: " + path);

        return new TokenNameFinderModel(modelStream);

    }

    // ---------------- INTENT ----------------

    public String detectIntent(String text) {

        if(text == null || text.isEmpty())
            return "unknown";

        String lower = text.toLowerCase();

        String[] tokensLower = tokenizer.tokenize(lower);
        String[] tokensOriginal = tokenizer.tokenize(text);

        // ---- ПОГОДА ----

        if(Arrays.stream(tokensLower).anyMatch(t ->

                t.equals("weather") ||
                        t.equals("temperature") ||
                        t.equals("forecast") ||

                        t.equals("погода") ||
                        t.equals("температура")

        ))
            return "weather";

        // якщо є місто
        for(String city : cities){

            if(lower.contains(city.toLowerCase()))
                return "weather";

        }


        if (text.toLowerCase().contains("нагадай") || text.toLowerCase().contains("remind")) {
            return "reminder";
        }

        // ---- ВАЛЮТА ----

        if(Arrays.stream(tokensLower).anyMatch(t ->

                t.equals("currency") ||
                        t.equals("rate") ||
                        t.equals("exchange") ||

                        t.equals("usd") ||
                        t.equals("uah") ||

                        t.equals("курс") ||
                        t.equals("долар")

        ))
            return "currency";

        // NER грошей

        if(moneyFinder.find(tokensOriginal).length > 0)
            return "currency";

        // число

        for(String token : tokensLower){

            if(token.matches("\\d+(\\.\\d+)?"))
                return "currency";

        }

        return "unknown";

    }

    // ---------------- PARAMETERS ----------------

    public String extractParameter(String text, String intent){

        if(text == null || text.isEmpty())
            return "";

        switch(intent){

            case "weather":
                return extractCity(text);

            case "currency":
                return extractMoney(text);

            default:
                return "";
        }

    }

    public Reminder extractReminder(String text) {
        // Початкові значення
        String date = "";
        String time = "";
        String message = "";

        // regex для часу: 9:00, 09:30 тощо
        java.util.regex.Pattern timePattern = java.util.regex.Pattern.compile("(\\d{1,2}:\\d{2})");
        java.util.regex.Matcher timeMatcher = timePattern.matcher(text);
        if (timeMatcher.find()) time = timeMatcher.group(1);

        // Просте визначення дати
        String lowerText = text.toLowerCase();
        if (lowerText.contains("завтра")) date = "tomorrow";
        else if (lowerText.contains("сьогодні")) date = "today";

        // Текст нагадування після ключових слів "про"
        int idx = lowerText.indexOf("про");
        if (idx != -1) message = text.substring(idx + 3).trim();

        return new Reminder(date, time, message);
    }

    // ---------------- CITY ----------------

    private String extractCity(String text){

        String lower = text.toLowerCase();

        for(String city : cities){

            if(lower.contains(city.toLowerCase()))
                return city;

        }

        String[] tokens = tokenizer.tokenize(text);

        for(int i=0;i<tokens.length-1;i++){

            if(tokens[i].equalsIgnoreCase("in") ||
                    tokens[i].equalsIgnoreCase("в"))

                return tokens[i+1];

        }

        Span[] spans = locationFinder.find(tokens);

        if(spans.length>0)
            return concatTokens(tokens,spans[0]);

        return "";

    }

    // ---------------- MONEY ----------------

    private String extractMoney(String text){

        String[] tokens = tokenizer.tokenize(text);

        for(String token : tokens){

            if(token.matches("\\$\\d+(\\.\\d+)?"))
                return "USD " + token.replace("$","");

            if(token.matches("\\d+(\\.\\d+)?"))
                return "USD " + token;

        }

        Span[] spans = moneyFinder.find(tokens);

        if(spans.length>0)
            return concatTokens(tokens,spans[0]);

        return text.trim();

    }

    private String concatTokens(String[] tokens, Span span){

        StringBuilder sb = new StringBuilder();

        for(int i = span.getStart(); i < span.getEnd(); i++){

            sb.append(tokens[i]);

            if(i < span.getEnd()-1)
                sb.append(" ");

        }

        return sb.toString();

    }


}

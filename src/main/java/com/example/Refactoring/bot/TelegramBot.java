package com.example.Refactoring.bot;


import com.example.Refactoring.controller.UserProfile;
import com.example.Refactoring.model.Weather;
import com.example.Refactoring.service.CurrencyService;
import com.example.Refactoring.service.NlpService;
import com.example.Refactoring.service.UserService;
import com.example.Refactoring.service.WeatherService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final WeatherService weatherService;
    private final CurrencyService currencyService;
    private final NlpService nlpService;
    private final UserService userService;

    @Value("${telegram.bot.username}")
    private String username;

    @Value("${telegram.bot.token}")
    private String token;

    public TelegramBot(
            WeatherService weatherService,
            CurrencyService currencyService,
            NlpService nlpService,
            UserService userService
    ) {
        this.weatherService = weatherService;
        this.currencyService = currencyService;
        this.nlpService = nlpService;
        this.userService = userService;
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (!update.hasMessage() || !update.getMessage().hasText()) return;

        String text = update.getMessage().getText().trim();
        Long chatId = update.getMessage().getChatId();
        String name = update.getMessage().getFrom().getFirstName();

        UserProfile user = userService.getOrCreateUser(chatId, name);

        String response;

        // ---------------- REGISTER ----------------

        if (text.equals("/register")) {

            userService.register(chatId);

            response = """
                    ✅ Registration completed

                    Configure profile:
                    /setcity Kyiv
                    /setlanguage en
                    /settime 24h

                    View profile:
/profile
                    """;

            sendMessage(chatId, response);
            return;
        }

        // ---------------- PROFILE ----------------

        if (text.equals("/profile")) {

            response =
                    "👤 Profile\n\n" +
                            "Name: " + user.getName() +
                            "\nCity: " + user.getFavoriteCity() +
                            "\nLanguage: " + user.getLanguage() +
                            "\nTime format: " + user.getTimeFormat();

            sendMessage(chatId, response);
            return;
        }

        // ---------------- CITY ----------------

        if (text.startsWith("/setcity")) {

            String city = text.replace("/setcity", "").trim();

            if (city.isEmpty()) {
                response = "❗ Usage:\n/setcity Kyiv";
            } else {
                userService.setCity(chatId, city);
                response = "✅ City updated: " + city;
            }

            sendMessage(chatId, response);
            return;
        }

        // ---------------- LANGUAGE ----------------

        if (text.startsWith("/setlanguage")) {

            String lang = text.replace("/setlanguage", "").trim();

            if (lang.isEmpty()) {
                response = "❗ Usage:\n/setlanguage en";
            } else {
                userService.setLanguage(chatId, lang);
                response = "🌍 Language updated: " + lang;
            }

            sendMessage(chatId, response);
            return;
        }

        // ---------------- TIME FORMAT ----------------

        if (text.startsWith("/settime")) {

            String format = text.replace("/settime", "").trim();

            if (format.isEmpty()) {
                response = "❗ Usage:\n/settime 24h";
            } else {
                userService.setTimeFormat(chatId, format);
                response = "⏰ Time format updated: " + format;
            }

            sendMessage(chatId, response);
            return;
        }

        // ---------------- NLP ----------------

        String intent = nlpService.detectIntent(text);
        String param = nlpService.extractParameter(text, intent);

        switch (intent) {

            case "weather" -> {

                String city = param.isEmpty()
                        ? user.getFavoriteCity()
                        : param;

                try {

                    Weather weather = weatherService.getWeatherByCity(city);

                    response = "🌤 Weather in " + city +
                            "\nTemperature: " + weather.getTemperature() +
                            "\nDescription: " + weather.getDescription();

                } catch (Exception e) {

                    response = "⚠️ Could not fetch weather for " + city;

                }
            }

            case "currency" -> {

                try {

                    response = currencyService.getUahRates();

                } catch (Exception e) {

                    response = "⚠️ Could not fetch currency rates";

                }

            }

            default -> response =
                    "🤖 I didn't understand.\n\nTry:\n" +
                            "/register\n" +
                            "/profile\n" +
                            "weather in Kyiv\n" +
                            "Скажи погоду в Львові\n" +
                            "50 USD\n" +
                            "$100";
        }

        sendMessage(chatId, response);
    }

    private void sendMessage(Long chatId, String text) {

        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }
}
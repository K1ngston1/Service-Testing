package com.example.Refactoring.bot;

import com.example.Refactoring.model.Weather;
import com.example.Refactoring.service.CurrencyService;
import com.example.Refactoring.service.WeatherService;
import com.example.Refactoring.service.NlpService;
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

    @Value("${telegram.bot.username}")
    private String username;

    @Value("${telegram.bot.token}")
    private String token;

    public TelegramBot(WeatherService weatherService, CurrencyService currencyService, NlpService nlpService) {
        this.weatherService = weatherService;
        this.currencyService = currencyService;
        this.nlpService = nlpService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) return;

        String message = update.getMessage().getText().trim();
        Long chatId = update.getMessage().getChatId();
        String response;

        String intent = nlpService.detectIntent(message);
        String param = nlpService.extractParameter(message, intent);

        switch (intent) {
            case "weather" -> {
                String city = param.isEmpty() ? "Kyiv" : param;
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
                    response = "⚠️ Could not fetch currency rates at the moment";
                }
            }

            default -> response = "🤖 I didn't understand.\n\nTry typing:\nweather in Kyiv\n50 USD\n$100";
        }

        sendMessage(chatId, response);
    }

    private void sendMessage(Long chatId, String text) {
        if (text == null || text.isEmpty()) text = "⚠️ Empty response";

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
    public String getBotUsername() { return username; }

    @Override
    public String getBotToken() { return token; }
}
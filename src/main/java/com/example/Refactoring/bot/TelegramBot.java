package com.example.Refactoring.bot;

import com.example.Refactoring.model.UserProfile;
import com.example.Refactoring.model.Reminder;
import com.example.Refactoring.model.Weather;
import com.example.Refactoring.service.CurrencyService;
import com.example.Refactoring.service.NlpService;
import com.example.Refactoring.service.UserService;
import com.example.Refactoring.service.WeatherService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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

        // ================= COMMANDS FIRST =================

        if (text.equals("/register")) {
            userService.register(chatId);
            sendMessage(chatId, "✅ Registration completed");
            return;
        }

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

        if (text.startsWith("/setcity")) {
            String city = text.replace("/setcity", "").trim();
            if (city.isEmpty()) response = "❗ /setcity Kyiv";
            else {
                userService.setCity(chatId, city);
                response = "✅ City updated";
            }
            sendMessage(chatId, response);
            return;
        }

        if (text.startsWith("/setlanguage")) {
            String lang = text.replace("/setlanguage", "").trim();
            if (lang.isEmpty()) response = "❗ /setlanguage en";
            else {
                userService.setLanguage(chatId, lang);
                response = "🌍 Language updated";
            }
            sendMessage(chatId, response);
            return;
        }

        if (text.startsWith("/settime")) {
            String format = text.replace("/settime", "").trim();
            if (format.isEmpty()) response = "❗ /settime 24h";
            else {
                userService.setTimeFormat(chatId, format);
                response = "⏰ Time updated";
            }
            sendMessage(chatId, response);
            return;
        }

        // ---------- SHOW ----------
        if (text.equals("/reminders")) {

            List<Reminder> reminders = userService.getReminders(chatId);

            if (reminders.isEmpty()) {
                response = "📭 No reminders";
            } else {
                StringBuilder sb = new StringBuilder("📋 Your reminders:\n\n");

                for (int i = 0; i < reminders.size(); i++) {
                    Reminder r = reminders.get(i);
                    sb.append(i).append(") ")
                            .append(r.getDate()).append(" ")
                            .append(r.getTime())
                            .append(" - ")
                            .append(r.getMessage())
                            .append("\n");
                }
                response = sb.toString();
            }

            sendMessage(chatId, response);
            return;
        }

        // ---------- DELETE ----------
        if (text.startsWith("/deletereminder")) {
            try {
                int index = Integer.parseInt(text.split(" ")[1]);
                userService.deleteReminder(chatId, index);
                response = "🗑 Deleted";
            } catch (Exception e) {
                response = "❗ /deletereminder 0";
            }
            sendMessage(chatId, response);
            return;
        }

        // ---------- UPDATE ----------
        if (text.startsWith("/editreminder")) {

            try {
                String[] parts = text.split(" ", 5);

                int index = Integer.parseInt(parts[1]);
                String dateRaw = parts[2];
                String time = parts[3];
                String message = parts[4];

                String date;

                if (dateRaw.equalsIgnoreCase("завтра")) date = "tomorrow";
                else if (dateRaw.equalsIgnoreCase("сьогодні")) date = "today";
                else date = dateRaw;

                Reminder updated = new Reminder(date, time, message);

                userService.updateReminder(chatId, index, updated);

                response = "✏️ Updated";

            } catch (Exception e) {
                response = "❗ /editreminder 0 tomorrow 10:00 text";
            }

            sendMessage(chatId, response);
            return;
        }

        // ================= NLP AFTER =================

        String intent = nlpService.detectIntent(text);
        String param = nlpService.extractParameter(text, intent);

        // ---------- CREATE ----------
        if (intent.equals("reminder")) {

            Reminder reminder = nlpService.extractReminder(text);

            if (reminder.getMessage().isEmpty()) {
                response = "❗ Example:\nНагадай завтра о 9:00 про зустріч";
            } else {
                userService.addReminder(chatId, reminder);
                response = "✅ Reminder saved";
            }

            sendMessage(chatId, response);
            return;
        }

        // ---------- OTHER ----------
        switch (intent) {

            case "weather" -> {
                String city = param.isEmpty() ? user.getFavoriteCity() : param;

                try {
                    Weather weather = weatherService.getWeatherByCity(city);
                    response = "🌤 " + city +
                            "\nTemp: " + weather.getTemperature();
                } catch (Exception e) {
                    response = "⚠️ Weather error";
                }
            }

            case "currency" -> {
                try {
                    response = currencyService.getUahRates();
                } catch (Exception e) {
                    response = "⚠️ Currency error";
                }
            }

            default -> response = "🤖 Unknown command";
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
    public String getBotUsername() { return username; }

    @Override
    public String getBotToken() { return token; }

    // ---------- SCHEDULER ----------
    @Scheduled(fixedRate = 60000)
    public void sendReminders() {

        LocalDateTime now = LocalDateTime.now();

        for (UserProfile user : userService.getAllUsers()) {

            List<Reminder> toRemove = new ArrayList<>();

            for (Reminder r : user.getReminders()) {

                if (r.getTime() == null || r.getTime().isEmpty()) continue;

                LocalTime time =
                        LocalTime.parse(r.getTime(), DateTimeFormatter.ofPattern("H:mm"));

                LocalDate date;

                if ("today".equals(r.getDate()))
                    date = LocalDate.now();
                else if ("tomorrow".equals(r.getDate()))
                    date = LocalDate.now().plusDays(1);
                else continue;

                if (date.equals(now.toLocalDate()) &&
                        time.getHour() == now.getHour() &&
                        time.getMinute() == now.getMinute()) {

                    sendMessage(user.getChatId(),
                            "⏰ Reminder: " + r.getMessage());

                    toRemove.add(r);
                }
            }

            user.getReminders().removeAll(toRemove);
            userService.saveUser(user);
        }
    }
}
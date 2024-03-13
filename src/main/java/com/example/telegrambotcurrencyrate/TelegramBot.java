package com.example.telegrambotcurrencyrate;

import com.example.telegrambotcurrencyrate.config.BotConfig;
import com.example.telegrambotcurrencyrate.model.CurrencyModel;
import com.example.telegrambotcurrencyrate.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.text.ParseException;

@Component
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {
    private final BotConfig botConfig;

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        CurrencyModel currencyModel = new CurrencyModel();
        String currencyInfo = "";

        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (messageText) {
                case "/start":
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;
                default:
                    try {
                        currencyInfo = CurrencyService.getCurrencyRate(messageText, currencyModel);
                    } catch (IOException e) {
                        sendMessage(chatId, "We haven't found such a currency." + "\n" +
                                        "Enter an abbreviation of currency. For example: USD.");
                    } catch (ParseException e) {
                        throw new RuntimeException("Unable to parse date");
                    }
                    sendMessage(chatId, currencyInfo);
            }
        }
    }
    private void startCommandReceived(Long chatId, String name) {
        String answer = "Hello, " + name + " , nice to meet you! " + "\n" +
                "Enter an abbreviation of currency. For example: USD.";
        sendMessage(chatId, answer);
    }

    private void sendMessage(Long chatId, String textToSend) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {

        }

    }

}

package ru.itmo.telecom.notification.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.itmo.telecom.notification.bot.TariffBot;
import ru.itmo.telecom.notification.service.BotService;

@Configuration
@ConfigurationProperties(prefix = "telegram.bot")
@Data
public class BotConfig {

    private String username;
    private String token;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public DefaultBotOptions botOptions() {
        return new DefaultBotOptions();
    }

    @Bean
    public TelegramBotsApi telegramBotsApi(TariffBot tariffBot) throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(tariffBot);
        return botsApi;
    }

    @Bean
    public TariffBot tariffBot(DefaultBotOptions botOptions, BotService botService) {
        // Передаем токен напрямую из этого класса
        return new TariffBot(botOptions, this.token, botService);
    }
}

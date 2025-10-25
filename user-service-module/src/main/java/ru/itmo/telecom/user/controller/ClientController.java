package ru.itmo.telecom.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import ru.itmo.telecom.user.dto.ClientDto;
import ru.itmo.telecom.user.dto.ClientRegistrationDto;
import ru.itmo.telecom.user.dto.ClientUpdateDto;
import ru.itmo.telecom.user.service.ClientService;

@RestController
@RequestMapping("/api/v1/users") // Используем v1 для версионирования
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    /**
     * API для регистрации нового клиента.
     * Сценарий /start для нового пользователя .
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ClientDto registerClient(@Valid @RequestBody ClientRegistrationDto dto) {
        return clientService.registerClient(dto);
    }

    /**
     * API для получения данных клиента по Telegram ID.
     * Сценарий /start для существующего пользователя [cite: 401] или /profile[cite: 380].
     */
    @GetMapping("/telegram/{telegramId}")
    public ClientDto getClientByTelegramId(@PathVariable Long telegramId) {
        return clientService.getClientByTelegramId(telegramId);
    }

    /**
     * API для обновления профиля клиента.
     * Сценарий /profile [cite: 440-452].
     */
    @PutMapping("/telegram/{telegramId}")
    public ClientDto updateClientProfile(@PathVariable Long telegramId,
                                         @Valid @RequestBody ClientUpdateDto dto) {
        return clientService.updateClientProfile(telegramId, dto);
    }
}

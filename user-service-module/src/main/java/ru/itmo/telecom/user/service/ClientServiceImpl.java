package ru.itmo.telecom.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itmo.telecom.shared.exceptions.ResourceNotFoundException;
import ru.itmo.telecom.shared.utils.TelecomConstants;
import ru.itmo.telecom.user.dto.ClientDto;
import ru.itmo.telecom.user.dto.ClientRegistrationDto;
import ru.itmo.telecom.user.dto.ClientUpdateDto;
import ru.itmo.telecom.user.entity.Client;
import ru.itmo.telecom.user.entity.ClientStatus;
import ru.itmo.telecom.user.exception.ClientAlreadyExistsException;
import ru.itmo.telecom.user.mapper.ClientMapper;
import ru.itmo.telecom.user.repository.ClientRepository;
import ru.itmo.telecom.user.repository.ClientStatusRepository;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final ClientStatusRepository clientStatusRepository;
    private final ClientMapper clientMapper;

    @Override
    @Transactional
    public ClientDto registerClient(ClientRegistrationDto dto) {
        // 1. Проверка на дубликат
        if (clientRepository.existsByTelegramId(dto.getTelegramId())) {
            throw new ClientAlreadyExistsException("Клиент с таким Telegram ID уже зарегистрирован.");
        }
        if (clientRepository.existsByPhoneNumber(dto.getPhoneNumber())) {
            throw new ClientAlreadyExistsException("Клиент с таким номером телефона уже зарегистрирован.");
        }

        // 2. Получение статуса по умолчанию
        ClientStatus newStatus = clientStatusRepository.findByName(TelecomConstants.CLIENT_STATUS_NEW) // [cite: 37]
                .orElseThrow(() -> new ResourceNotFoundException("Статус клиента", TelecomConstants.CLIENT_STATUS_NEW)); // [cite: 33]

        // 3. Создание и сохранение
        Client client = clientMapper.toEntity(dto);
        client.setStatus(newStatus);
        // registrationDate устанавливается автоматически [cite: 125]

        Client savedClient = clientRepository.save(client);

        return clientMapper.toDto(savedClient);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientDto getClientByTelegramId(Long telegramId) {
        Client client = clientRepository.findByTelegramId(telegramId)
                .orElseThrow(() -> new ResourceNotFoundException("Клиент", telegramId)); // [cite: 33]

        return clientMapper.toDto(client);
    }

    @Override
    @Transactional
    public ClientDto updateClientProfile(Long telegramId, ClientUpdateDto dto) {
        Client client = clientRepository.findByTelegramId(telegramId)
                .orElseThrow(() -> new ResourceNotFoundException("Клиент", telegramId));

        // Проверка, что новый номер телефона не занят ДРУГИМ клиентом
        if (!client.getPhoneNumber().equals(dto.getPhoneNumber()) &&
                clientRepository.existsByPhoneNumber(dto.getPhoneNumber())) {
            throw new ClientAlreadyExistsException("Этот номер телефона уже используется другим клиентом.");
        }

        // 4. Обновление полей
        clientMapper.updateClientFromDto(dto, client);
        Client updatedClient = clientRepository.save(client);

        return clientMapper.toDto(updatedClient);
    }
}

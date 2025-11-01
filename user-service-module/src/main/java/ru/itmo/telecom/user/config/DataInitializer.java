package ru.itmo.telecom.user.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.itmo.telecom.user.entity.ClientStatus;
import ru.itmo.telecom.user.repository.ClientStatusRepository;
import ru.itmo.telecom.shared.utils.TelecomConstants;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ClientStatusRepository clientStatusRepository;

    @Override
    public void run(String... args) throws Exception {
        // Инициализация статусов клиентов
        if (clientStatusRepository.count() == 0) {
            ClientStatus active = new ClientStatus();
            active.setName(TelecomConstants.CLIENT_STATUS_ACTIVE);
            active.setDescription("Клиент, успешно пользующийся услугами");

            ClientStatus newStatus = new ClientStatus();
            newStatus.setName(TelecomConstants.CLIENT_STATUS_NEW);
            newStatus.setDescription("Клиент зарегистрировался, но еще не подключил тариф");

            ClientStatus suspended = new ClientStatus();
            suspended.setName(TelecomConstants.CLIENT_STATUS_SUSPENDED);
            suspended.setDescription("Клиент временно заблокирован");

            clientStatusRepository.save(active);
            clientStatusRepository.save(newStatus);
            clientStatusRepository.save(suspended);

            System.out.println("Initialized client statuses");
        }
    }
}

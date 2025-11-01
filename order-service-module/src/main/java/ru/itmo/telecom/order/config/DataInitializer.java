package ru.itmo.telecom.order.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.itmo.telecom.order.entity.ApplicationStatus;
import ru.itmo.telecom.order.repository.ApplicationStatusRepository;
import ru.itmo.telecom.shared.utils.TelecomConstants;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ApplicationStatusRepository statusRepository;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== Starting Order Service Data Initialization ===");

        // Инициализация статусов заявок
        if (statusRepository.count() == 0) {
            System.out.println("Initializing application statuses...");

            List<ApplicationStatus> statuses = Arrays.asList(
                    createStatus(TelecomConstants.APP_STATUS_CREATED, "Заявка создана пользователем, ожидает обработки"),
                    createStatus(TelecomConstants.APP_STATUS_IN_PROGRESS, "Заявка принята в работу оператором"),
                    createStatus(TelecomConstants.APP_STATUS_REQUIRES_CLARIFICATION, "Требуется уточнение данных"),
                    createStatus(TelecomConstants.APP_STATUS_CONFIRMED, "Данные клиента проверены и одобрены"),
                    createStatus(TelecomConstants.APP_STATUS_CONNECTING, "Технический отдел производит настройку"),
                    createStatus(TelecomConstants.APP_STATUS_COMPLETED, "Услуга активирована, заявка выполнена"),
                    createStatus(TelecomConstants.APP_STATUS_REJECTED, "Заявка отклонена")
            );

            statusRepository.saveAll(statuses);
            System.out.println("Application statuses initialized: " + statuses.size());
        } else {
            System.out.println("Application statuses already exist: " + statusRepository.count());
        }

        System.out.println("=== Order Service Data Initialization Complete ===");
    }

    private ApplicationStatus createStatus(String name, String description) {
        ApplicationStatus status = new ApplicationStatus();
        status.setName(name);
        status.setDescription(description);
        status.setIsActive(true);
        return status;
    }
}

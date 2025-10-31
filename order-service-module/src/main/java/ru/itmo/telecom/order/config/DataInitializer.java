package ru.itmo.telecom.order.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.itmo.telecom.order.entity.ApplicationStatus;
import ru.itmo.telecom.order.repository.ApplicationStatusRepository;
import ru.itmo.telecom.shared.utils.TelecomConstants;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ApplicationStatusRepository statusRepository;

    @Override
    public void run(String... args) throws Exception {
        // --- Инициализация Статусов Заявок ---
        if (statusRepository.count() == 0) {
            ApplicationStatus created = new ApplicationStatus();
            created.setName(TelecomConstants.APP_STATUS_CREATED);
            created.setDescription("Заявка создана пользователем, ожидает обработки.");

            ApplicationStatus inProgress = new ApplicationStatus();
            inProgress.setName(TelecomConstants.APP_STATUS_IN_PROGRESS);
            inProgress.setDescription("Заявка принята в работу оператором.");

            ApplicationStatus completed = new ApplicationStatus();
            completed.setName(TelecomConstants.APP_STATUS_COMPLETED);
            completed.setDescription("Услуга успешно подключена.");

            ApplicationStatus cancelled = new ApplicationStatus();
            cancelled.setName(TelecomConstants.APP_STATUS_REJECTED);
            cancelled.setDescription("Заявка отменена клиентом или оператором.");

            List<ApplicationStatus> statuses = List.of(created, inProgress, completed, cancelled);
            statusRepository.saveAll(statuses);
        }
    }
}

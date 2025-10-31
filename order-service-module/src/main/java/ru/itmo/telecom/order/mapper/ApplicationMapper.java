package ru.itmo.telecom.order.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.itmo.telecom.order.entity.Application;
import ru.itmo.telecom.order.entity.ApplicationStatus;
import ru.itmo.telecom.shared.order.dto.ApplicationDto;
import ru.itmo.telecom.shared.order.dto.ApplicationStatusDto;
import ru.itmo.telecom.shared.order.dto.ApplicationCreateRequest;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ApplicationMapper {

    ApplicationStatusDto toStatusDto(ApplicationStatus status);

    // Маппинг сущности в DTO для ответа
    @Mapping(target = "tariffId", source = "tariffApplication.tariffId")
    @Mapping(target = "details", ignore = true) // Детали будем маппить вручную в сервисе
    @Mapping(target = "isActive", expression = "java(isApplicationActive(application))")
    ApplicationDto toDto(Application application);

    List<ApplicationDto> toDto(List<Application> applications);

    // Маппинг запроса в сущность (только основные поля)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "totalCost", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "customApplication", ignore = true)
    @Mapping(target = "tariffApplication", ignore = true)
    Application toEntity(ApplicationCreateRequest request);

    // Вспомогательный метод для определения активности заявки
    default Boolean isApplicationActive(Application application) {
        if (application.getStatus() == null) {
            return false;
        }
        String statusName = application.getStatus().getName();
        // Считаем заявку активной, если она не завершена и не отменена
        return !"ВЫПОЛНЕНА".equals(statusName) && !"ОТКЛОНЕНА".equals(statusName);
    }
}
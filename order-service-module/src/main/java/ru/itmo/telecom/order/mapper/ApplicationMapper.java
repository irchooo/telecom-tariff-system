package ru.itmo.telecom.order.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.itmo.telecom.order.entity.Application;
import ru.itmo.telecom.shared.order.dto.ApplicationDto;
import ru.itmo.telecom.shared.order.dto.ApplicationCreateRequest;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ApplicationDetailMapper.class, ApplicationStatusMapper.class})
public interface ApplicationMapper {

    // УБИРАЕМ автоматический маппинг details, так как он больше не работает
    @Mapping(target = "tariffId", source = "tariffApplication.tariffId")
    @Mapping(target = "details", ignore = true) // Будем устанавливать вручную в сервисе
    @Mapping(target = "status", source = "status")
    @Mapping(target = "isActive", expression = "java(isApplicationActive(application))")
    ApplicationDto toDto(Application application);

    List<ApplicationDto> toDto(List<Application> applications);

    // Остальные методы без изменений
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "totalCost", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "customApplication", ignore = true)
    @Mapping(target = "tariffApplication", ignore = true)
    Application toEntity(ApplicationCreateRequest request);

    default Boolean isApplicationActive(Application application) {
        if (application.getStatus() == null) {
            return false;
        }
        String statusName = application.getStatus().getName();
        return !"ВЫПОЛНЕНА".equals(statusName) && !"ОТКЛОНЕНА".equals(statusName);
    }
}
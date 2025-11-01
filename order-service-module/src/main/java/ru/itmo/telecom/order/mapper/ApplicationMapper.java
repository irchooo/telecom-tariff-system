package ru.itmo.telecom.order.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.itmo.telecom.order.entity.Application;
import ru.itmo.telecom.shared.order.dto.ApplicationDto;
import ru.itmo.telecom.shared.order.dto.ApplicationCreateRequest;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ApplicationDetailMapper.class, ApplicationStatusMapper.class})
public interface ApplicationMapper {

    @Mapping(target = "tariffId", source = "tariffApplication.tariffId")
    @Mapping(target = "details", ignore = true) // Будем устанавливать вручную в сервисе
    @Mapping(target = "status", source = "status")
    @Mapping(target = "isActive", expression = "java(isApplicationActive(application))")
    ApplicationDto toDto(Application application);

    List<ApplicationDto> toDto(List<Application> applications);

    // Упрощенный метод создания entity
    default Application toEntity(ApplicationCreateRequest request) {
        Application application = new Application();
        application.setClientId(request.getClientId());
        // Остальные поля устанавливаются в сервисе
        return application;
    }

    default Boolean isApplicationActive(Application application) {
        if (application.getStatus() == null) {
            return false;
        }
        String statusName = application.getStatus().getName();
        return !"ВЫПОЛНЕНА".equals(statusName) && !"ОТКЛОНЕНА".equals(statusName);
    }
}
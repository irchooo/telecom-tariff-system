package ru.itmo.telecom.order.mapper;

import org.mapstruct.Mapper;
import ru.itmo.telecom.order.entity.ApplicationStatus;
import ru.itmo.telecom.shared.order.dto.ApplicationStatusDto;

@Mapper(componentModel = "spring")
public interface ApplicationStatusMapper {

    // Этот метод будет использоваться для маппинга status в ApplicationDto
    ApplicationStatusDto toDto(ApplicationStatus status);
}
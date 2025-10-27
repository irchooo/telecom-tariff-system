package ru.itmo.telecom.tariff.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.itmo.telecom.tariff.entity.ServiceParameter;
import ru.itmo.telecom.shared.tariff.dto.ServiceParameterDto;
import ru.itmo.telecom.shared.tariff.dto.ServiceParameterCreateRequest;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ServiceParameterMapper {

    ServiceParameterMapper INSTANCE = Mappers.getMapper(ServiceParameterMapper.class);

    ServiceParameterDto toDto(ServiceParameter parameter);

    List<ServiceParameterDto> toDto(List<ServiceParameter> parameters);

    // Метод для создания Entity из Request DTO
    // Игнорируем ID, который отсутствует в Request
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true) // Будет заполнен @CreationTimestamp
    ServiceParameter toEntity(ServiceParameterCreateRequest request);
}

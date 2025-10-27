package ru.itmo.telecom.tariff.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.itmo.telecom.shared.tariff.dto.TariffDetailCreateRequest;
import ru.itmo.telecom.tariff.entity.TariffDetail;
import ru.itmo.telecom.shared.tariff.dto.TariffDetailDto;

@Mapper(componentModel = "spring", uses = {ServiceParameterMapper.class})
public interface TariffDetailMapper {

    TariffDetailMapper INSTANCE = Mappers.getMapper(TariffDetailMapper.class);

    @Mapping(target = "tariffId", source = "id.tariffId")
    @Mapping(target = "parameterId", source = "id.parameterId")
    @Mapping(target = "parameter", source = "parameter") // Вложенный маппинг ServiceParameter
    TariffDetailDto toDto(TariffDetail detail);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tariff", ignore = true) // Будет установлен в сервисе
    @Mapping(target = "parameter", ignore = true) // Будет установлен в сервисе
    @Mapping(target = "isActive", constant = "true")
    @Mapping(target = "createdAt", ignore = true)
    TariffDetail toEntity(TariffDetailCreateRequest request); // <--- НОВЫЙ МЕТОД
}

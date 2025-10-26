package ru.itmo.telecom.tariff.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.itmo.telecom.tariff.entity.Tariff;
import ru.itmo.telecom.shared.tariff.dto.TariffDto;

import java.util.List;

@Mapper(componentModel = "spring", uses = {TariffDetailMapper.class})
public interface TariffMapper {

    TariffMapper INSTANCE = Mappers.getMapper(TariffMapper.class);

    // Маппинг из Entity в DTO
    @Mapping(target = "details", source = "details") // Предполагаем, что в сущности Tariff будет поле List<TariffDetail> details
    TariffDto toDto(Tariff tariff);

    List<TariffDto> toDto(List<Tariff> tariffs);
}

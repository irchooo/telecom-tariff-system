package ru.itmo.telecom.user.mapper;


import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.MappingTarget;
import ru.itmo.telecom.user.dto.ClientDto;
import ru.itmo.telecom.user.dto.ClientRegistrationDto;
import ru.itmo.telecom.user.dto.ClientUpdateDto;
import ru.itmo.telecom.user.entity.Client;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    /**
     * Преобразует Client (Entity) в ClientDto (DTO).
     * @param client Сущность клиента
     * @return DTO клиента
     */
    @Mapping(source = "status.name", target = "status")
    ClientDto toDto(Client client);

    /**
     * Преобразует ClientRegistrationDto (DTO) в Client (Entity).
     * @param dto DTO регистрации
     * @return Сущность клиента
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "registrationDate", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "email", ignore = true)
    Client toEntity(ClientRegistrationDto dto);

    /**
     * Обновляет существующую сущность Client данными из ClientUpdateDto.
     * @param dto DTO с обновлениями
     * @param client Сущность, которую нужно обновить
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "telegramId", ignore = true)
    @Mapping(target = "telegramUsername", ignore = true)
    @Mapping(target = "registrationDate", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "email", ignore = true)
    void updateClientFromDto(ClientUpdateDto dto, @MappingTarget Client client);
}

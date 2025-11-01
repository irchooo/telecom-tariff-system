package ru.itmo.telecom.order.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.itmo.telecom.order.entity.ApplicationDetail;
import ru.itmo.telecom.shared.order.dto.ApplicationDetailDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ApplicationDetailMapper {

    @Mapping(target = "parameterId", source = "id.parameterId")
    ApplicationDetailDto toDto(ApplicationDetail detail);

    List<ApplicationDetailDto> toDto(List<ApplicationDetail> details);
}
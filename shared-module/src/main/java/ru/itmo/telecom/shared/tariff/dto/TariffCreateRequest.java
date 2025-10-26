package ru.itmo.telecom.shared.tariff.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Value;
import java.util.List;

@Value
public class TariffCreateRequest {

    @NotBlank(message = "Название тарифа не может быть пустым")
    @Size(max = 100, message = "Название должно быть короче 100 символов")
    String name;

    @Size(max = 250, message = "Описание должно быть короче 250 символов")
    String description;

    @Valid // ВАЖНО: включает валидацию для каждого элемента в списке
    @NotEmpty(message = "Тариф должен содержать хотя бы одну деталь")
    List<TariffDetailCreateRequest> details;
}

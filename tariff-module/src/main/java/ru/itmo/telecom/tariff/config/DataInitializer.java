package ru.itmo.telecom.tariff.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.itmo.telecom.tariff.entity.ServiceParameter;
import ru.itmo.telecom.tariff.entity.Tariff;
import ru.itmo.telecom.tariff.entity.TariffDetail;
import ru.itmo.telecom.tariff.entity.TariffDetailId;
import ru.itmo.telecom.tariff.repository.ServiceParameterRepository;
import ru.itmo.telecom.tariff.repository.TariffDetailRepository;
import ru.itmo.telecom.tariff.repository.TariffRepository;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ServiceParameterRepository serviceParameterRepository;
    private final TariffRepository tariffRepository;
    private final TariffDetailRepository tariffDetailRepository;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== Starting Tariff Service Data Initialization ===");

        // Инициализация параметров услуг
        if (serviceParameterRepository.count() == 0) {
            System.out.println("Initializing service parameters...");

            List<ServiceParameter> parameters = Arrays.asList(
                    createParameter("Интернет", "Гб", new BigDecimal("5.00"), "Доступ в интернет"),
                    createParameter("Минуты", "мин", new BigDecimal("1.00"), "Звонки на все номера"),
                    createParameter("SMS", "шт", new BigDecimal("0.50"), "SMS сообщения")
            );

            serviceParameterRepository.saveAll(parameters);
            System.out.println("Service parameters initialized: " + parameters.size());
        }

        // Инициализация готовых тарифов
        if (tariffRepository.count() == 0) {
            System.out.println("Initializing predefined tariffs...");
            initializePredefinedTariffs();
        }

        System.out.println("=== Tariff Service Data Initialization Complete ===");
    }

    private void initializePredefinedTariffs() {
        // Создаем основные тарифы
        Tariff basicTariff = createTariff("БАЗОВЫЙ", "Идеальный тариф для экономных пользователей");
        Tariff standardTariff = createTariff("СТАНДАРТ", "Сбалансированный тариф для повседневного использования");
        Tariff premiumTariff = createTariff("ПРЕМИУМ", "Максимальные возможности для требовательных пользователей");

        // Сохраняем тарифы чтобы получить их ID
        Tariff savedBasic = tariffRepository.save(basicTariff);
        Tariff savedStandard = tariffRepository.save(standardTariff);
        Tariff savedPremium = tariffRepository.save(premiumTariff);

        // Получаем параметры услуг (должны существовать после инициализации)
        ServiceParameter internet = serviceParameterRepository.findByName("Интернет")
                .orElseThrow(() -> new RuntimeException("Internet parameter not found"));
        ServiceParameter minutes = serviceParameterRepository.findByName("Минуты")
                .orElseThrow(() -> new RuntimeException("Minutes parameter not found"));
        ServiceParameter sms = serviceParameterRepository.findByName("SMS")
                .orElseThrow(() -> new RuntimeException("SMS parameter not found"));

        // Создаем детали для БАЗОВОГО тарифа
        List<TariffDetail> basicDetails = Arrays.asList(
                createTariffDetail(savedBasic, internet, 5, new BigDecimal("0.8"), "5 ГБ интернета"),
                createTariffDetail(savedBasic, minutes, 100, new BigDecimal("0.7"), "100 минут"),
                createTariffDetail(savedBasic, sms, 50, new BigDecimal("0.6"), "50 SMS")
        );

        // Создаем детали для СТАНДАРТНОГО тарифа
        List<TariffDetail> standardDetails = Arrays.asList(
                createTariffDetail(savedStandard, internet, 15, new BigDecimal("0.7"), "15 ГБ интернета"),
                createTariffDetail(savedStandard, minutes, 300, new BigDecimal("0.6"), "300 минут"),
                createTariffDetail(savedStandard, sms, 100, new BigDecimal("0.5"), "100 SMS")
        );

        // Создаем детали для ПРЕМИУМ тарифа
        List<TariffDetail> premiumDetails = Arrays.asList(
                createTariffDetail(savedPremium, internet, 30, new BigDecimal("0.6"), "30 ГБ интернета"),
                createTariffDetail(savedPremium, minutes, 600, new BigDecimal("0.5"), "600 минут"),
                createTariffDetail(savedPremium, sms, 200, new BigDecimal("0.4"), "200 SMS")
        );

        // Сохраняем все детали
        tariffDetailRepository.saveAll(basicDetails);
        tariffDetailRepository.saveAll(standardDetails);
        tariffDetailRepository.saveAll(premiumDetails);

        // Устанавливаем детали тарифам и сохраняем
        savedBasic.setDetails(basicDetails);
        savedStandard.setDetails(standardDetails);
        savedPremium.setDetails(premiumDetails);

        tariffRepository.save(savedBasic);
        tariffRepository.save(savedStandard);
        tariffRepository.save(savedPremium);

        System.out.println("Predefined tariffs initialized: 3 tariffs created");
    }

    private ServiceParameter createParameter(String name, String unit, BigDecimal price, String description) {
        ServiceParameter parameter = new ServiceParameter();
        parameter.setName(name);
        parameter.setUnit(unit);
        parameter.setMaxPricePerUnit(price);
        parameter.setDescription(description);
        parameter.setIsActive(true);
        return parameter;
    }

    private Tariff createTariff(String name, String description) {
        Tariff tariff = new Tariff();
        tariff.setName(name);
        tariff.setDescription(description);
        tariff.setIsActive(true);
        return tariff;
    }

    private TariffDetail createTariffDetail(Tariff tariff, ServiceParameter parameter, Integer volume,
                                            BigDecimal priceCoefficient, String description) {
        TariffDetail detail = new TariffDetail();
        detail.setId(new TariffDetailId(tariff.getId(), parameter.getId()));
        detail.setTariff(tariff);
        detail.setParameter(parameter);
        detail.setVolume(volume);
        detail.setPriceCoefficient(priceCoefficient);
        detail.setDescription(description);
        detail.setIsActive(true);
        return detail;
    }
}
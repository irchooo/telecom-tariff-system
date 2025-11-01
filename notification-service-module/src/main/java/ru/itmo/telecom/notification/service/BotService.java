package ru.itmo.telecom.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.itmo.telecom.shared.order.dto.ApplicationCreateRequest;
import ru.itmo.telecom.shared.order.dto.ApplicationDetailRequest;
import ru.itmo.telecom.shared.order.dto.ApplicationDto;
import ru.itmo.telecom.shared.tariff.dto.ServiceParameterDto;
import ru.itmo.telecom.shared.tariff.dto.TariffDto;
import ru.itmo.telecom.shared.user.dto.ClientDto;
import ru.itmo.telecom.shared.user.dto.ClientRegistrationDto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class BotService {

    private final RestTemplate restTemplate;
    private final Map<Long, UserSession> userSessions = new HashMap<>();

    // Конфигурация URL сервисов
    private final String USER_SERVICE_URL = "http://localhost:8080/api/v1/users";
    private final String TARIFF_SERVICE_URL = "http://localhost:8081/api/v1/tariffs";
    private final String ORDER_SERVICE_URL = "http://localhost:8082/api/v1/orders";

    public BotService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String handleMessage(Long chatId, String message, String userFirstName) {
        UserSession session = userSessions.getOrDefault(chatId, new UserSession());

        // Обработка команд
        if (message.startsWith("/")) {
            return handleCommand(chatId, message, userFirstName, session);
        } else {
            return handleDialog(chatId, message, session);
        }
    }

    private String handleCommand(Long chatId, String command, String userFirstName, UserSession session) {
        userSessions.put(chatId, new UserSession()); // Сбрасываем сессию для новой команды

        switch (command) {
            case "/start":
                return handleStartCommand(chatId, userFirstName);
            case "/tariffs":
                return getAvailableTariffs();
            case "/constructor":
                userSessions.put(chatId, new UserSession("CONSTRUCTOR", 0));
                return "Давайте создадим ваш идеальный тариф! 🚀\n\nСколько ГБ интернета вам нужно? (1-100)";
            case "/profile":
                return getUserProfile(chatId);
            case "/my_orders":
                return getUserOrders(chatId);
            case "/help":
                return getHelpMessage();
            case "/cancel":
                return "Текущее действие отменено. Выберите команду из меню.";
            default:
                return "Неизвестная команда. Используйте /help для списка команд.";
        }
    }

    private String handleStartCommand(Long chatId, String userFirstName) {
        try {
            // Проверяем, зарегистрирован ли пользователь
            String url = USER_SERVICE_URL + "/telegram/" + chatId;
            ResponseEntity<ClientDto> response = restTemplate.getForEntity(url, ClientDto.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                // Пользователь уже зарегистрирован
                return String.format("Привет, %s! 👋 Я помогу рассчитать стоимость тарифного плана.\n\nДоступные команды:\n/tariffs - Готовые тарифы\n/constructor - Создать свой тариф\n/cancel - Отмена текущего действия",
                        userFirstName);
            }
        } catch (Exception e) {
            // Пользователь не найден - начинаем регистрацию
            UserSession session = new UserSession("REGISTRATION", 0);
            userSessions.put(chatId, session);
            return String.format("Привет, %s! 👋 Я помогу рассчитать стоимость тарифного плана, но сначала давай зарегистрируемся!\n\nВаше имя:", userFirstName);
        }

        return "Произошла ошибка при проверке регистрации. Пожалуйста, попробуйте позже.";
    }

    private String handleDialog(Long chatId, String message, UserSession session) {
        if (session.getState() == null) {
            return "Пожалуйста, выберите команду из меню.";
        }

        switch (session.getState()) {
            case "REGISTRATION":
                return handleRegistration(chatId, message, session);
            case "CONSTRUCTOR":
                return handleConstructor(chatId, message, session);
            default:
                return "Неизвестное состояние. Используйте /cancel для отмены.";
        }
    }

    private String handleRegistration(Long chatId, String message, UserSession session) {
        switch (session.getStep()) {
            case 0: // Имя
                session.setFirstName(message);
                session.setStep(1);
                return "Ваша фамилия:";
            case 1: // Фамилия
                session.setLastName(message);
                session.setStep(2);
                return "Номер телефона (в формате 79161234567):";
            case 2: // Телефон
                if (!isValidPhone(message)) {
                    return "Неверный формат номера телефона. Пожалуйста, введите номер в формате 79161234567:";
                }
                session.setPhoneNumber(message);

                // Регистрируем пользователя
                return completeRegistration(chatId, session);
            default:
                return "Ошибка в процессе регистрации. Используйте /cancel для отмены.";
        }
    }

    private String completeRegistration(Long chatId, UserSession session) {
        try {
            ClientRegistrationDto registrationDto = new ClientRegistrationDto();
            registrationDto.setTelegramId(chatId);
            registrationDto.setFirstName(session.getFirstName());
            registrationDto.setLastName(session.getLastName());
            registrationDto.setPhoneNumber(session.getPhoneNumber());

            // Добавляем логирование
            log.debug("Attempting to register user: {}", registrationDto);

            String url = USER_SERVICE_URL + "/register";
            log.debug("Calling user service: {}", url);

            ResponseEntity<ClientDto> response = restTemplate.postForEntity(url, registrationDto, ClientDto.class);

            log.debug("Registration response: Status {}, Body {}", response.getStatusCode(), response.getBody());

            if (response.getStatusCode().is2xxSuccessful()) {
                userSessions.remove(chatId); // Очищаем сессию
                return "Вы успешно зарегистрированы!\\n\\nДоступные команды:\\n/tariffs - Готовые тарифы\\n/constructor - Создать свой тариф\\n/cancel - Отмена текущего действия";
            } else {
                log.warn("Registration failed with status: {}", response.getStatusCode());
                return "Ошибка при регистрации. Сервер вернул статус: " + response.getStatusCode();
            }
        } catch (Exception e) {
            log.error("Registration error for chatId: {}", chatId, e);
            return "Ошибка при регистрации. Пожалуйста, попробуйте позже. Ошибка: " + e.getMessage();
        }
    }

    private String handleConstructor(Long chatId, String message, UserSession session) {
        try {
            int value = Integer.parseInt(message);

            switch (session.getStep()) {
                case 0: // Интернет
                    if (value < 1 || value > 100) {
                        return "Пожалуйста, введите число от 1 до 100 для ГБ интернета:";
                    }
                    session.setInternetGb(value);
                    session.setStep(1);
                    return "Сколько минут для звонков? (0-1000)";
                case 1: // Минуты
                    if (value < 0 || value > 1000) {
                        return "Пожалуйста, введите число от 0 до 1000 для минут:";
                    }
                    session.setMinutes(value);
                    session.setStep(2);
                    return "Сколько SMS сообщений? (0-500)";
                case 2: // SMS
                    if (value < 0 || value > 500) {
                        return "Пожалуйста, введите число от 0 до 500 для SMS:";
                    }
                    session.setSms(value);

                    // Рассчитываем стоимость и создаем заявку
                    return completeCustomTariff(chatId, session);
                default:
                    return "Ошибка в конструкторе. Используйте /cancel для отмены.";
            }
        } catch (NumberFormatException e) {
            return "Пожалуйста, введите число:";
        }
    }

    private String completeCustomTariff(Long chatId, UserSession session) {
        try {
            // Получаем ID параметров услуг
            Integer internetParamId = 1; // ID для интернета
            Integer minutesParamId = 2;  // ID для минут
            Integer smsParamId = 3;      // ID для SMS

            List<ApplicationDetailRequest> details = new ArrayList<>();

            if (session.getInternetGb() > 0) {
                details.add(new ApplicationDetailRequest(internetParamId, session.getInternetGb()));
            }
            if (session.getMinutes() > 0) {
                details.add(new ApplicationDetailRequest(minutesParamId, session.getMinutes()));
            }
            if (session.getSms() > 0) {
                details.add(new ApplicationDetailRequest(smsParamId, session.getSms()));
            }

            // Получаем ID клиента
            Integer clientId = getClientIdByTelegramId(chatId);
            if (clientId == null) {
                return "Сначала зарегистрируйтесь с помощью команды /start";
            }

            ApplicationCreateRequest createRequest = new ApplicationCreateRequest();
            createRequest.setClientId(clientId);
            createRequest.setDetails(details);

            String url = ORDER_SERVICE_URL + "/applications";
            ResponseEntity<ApplicationDto> response = restTemplate.postForEntity(url, createRequest, ApplicationDto.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                ApplicationDto application = response.getBody();
                userSessions.remove(chatId);

                return String.format("✅ Ваш тариф: %dГБ + %d минут + %d SMS\n\n💰 Стоимость: %.2f руб/мес\n\n📋 Заявка №%d создана!\n\nНаш менеджер свяжется с вами в течение 24 часов для подтверждения заказа.",
                        session.getInternetGb(), session.getMinutes(), session.getSms(),
                        application.getTotalCost(), application.getId());
            }
        } catch (Exception e) {
            log.error("Error creating custom tariff", e);
        }

        return "Ошибка при создании тарифа. Пожалуйста, попробуйте позже.";
    }

    private String getAvailableTariffs() {
        try {
            String url = TARIFF_SERVICE_URL;
            ResponseEntity<TariffDto[]> response = restTemplate.getForEntity(url, TariffDto[].class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                StringBuilder sb = new StringBuilder("📊 Доступные тарифы:\n\n");

                for (TariffDto tariff : response.getBody()) {
                    sb.append(String.format("🏷️ %s\n%s\n\n",
                            tariff.getName(),
                            tariff.getDescription() != null ? tariff.getDescription() : ""));
                }

                sb.append("Для заказа тарифа используйте /constructor");
                return sb.toString();
            }
        } catch (Exception e) {
            log.error("Error getting tariffs", e);
        }

        return "Не удалось загрузить список тарифов. Пожалуйста, попробуйте позже.";
    }

    private String getUserProfile(Long chatId) {
        try {
            String url = USER_SERVICE_URL + "/telegram/" + chatId;
            ResponseEntity<ClientDto> response = restTemplate.getForEntity(url, ClientDto.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                ClientDto client = response.getBody();
                return String.format("👤 Ваш профиль:\n\nИмя: %s\nФамилия: %s\nТелефон: %s\nСтатус: %s",
                        client.getFirstName(), client.getLastName(),
                        client.getPhoneNumber(), client.getStatus());
            }
        } catch (Exception e) {
            // Пользователь не найден
        }

        return "Профиль не найден. Используйте /start для регистрации.";
    }

    private String getUserOrders(Long chatId) {
        try {
            Integer clientId = getClientIdByTelegramId(chatId);
            if (clientId == null) {
                return "Сначала зарегистрируйтесь с помощью команды /start";
            }

            String url = ORDER_SERVICE_URL + "/applications/client/" + clientId;
            ResponseEntity<ApplicationDto[]> response = restTemplate.getForEntity(url, ApplicationDto[].class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                ApplicationDto[] applications = response.getBody();

                if (applications.length == 0) {
                    return "У вас пока нет заявок.";
                }

                StringBuilder sb = new StringBuilder("📋 Ваши заявки:\n\n");
                for (ApplicationDto app : applications) {
                    sb.append(String.format("Заявка №%d\nСтатус: %s\nСтоимость: %.2f руб\n\n",
                            app.getId(), app.getStatus().getName(), app.getTotalCost()));
                }

                return sb.toString();
            }
        } catch (Exception e) {
            log.error("Error getting user orders", e);
        }

        return "Не удалось загрузить список заявок. Пожалуйста, попробуйте позже.";
    }

    private String getHelpMessage() {
        return """
                🤖 Помощь по командам:
                
                /start - Начало работы и регистрация
                /tariffs - Просмотр готовых тарифов
                /constructor - Создать индивидуальный тариф
                /profile - Просмотр профиля
                /my_orders - Мои заявки
                /cancel - Отмена текущего действия
                /help - Эта справка
                
                Для отмены любого действия используйте /cancel
                """;
    }

    private Integer getClientIdByTelegramId(Long telegramId) {
        try {
            String url = USER_SERVICE_URL + "/telegram/" + telegramId;
            ResponseEntity<ClientDto> response = restTemplate.getForEntity(url, ClientDto.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody().getId();
            }
        } catch (Exception e) {
            log.error("Error getting client ID", e);
        }
        return null;
    }

    private boolean isValidPhone(String phone) {
        return phone.matches("^(7|8)\\d{10}$");
    }

    // Вспомогательный класс для хранения состояния пользователя
    private static class UserSession {
        private String state;
        private int step;
        private String firstName;
        private String lastName;
        private String phoneNumber;
        private Integer internetGb;
        private Integer minutes;
        private Integer sms;

        public UserSession() {}

        public UserSession(String state, int step) {
            this.state = state;
            this.step = step;
        }

        // Геттеры и сеттеры
        public String getState() { return state; }
        public void setState(String state) { this.state = state; }
        public int getStep() { return step; }
        public void setStep(int step) { this.step = step; }
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        public Integer getInternetGb() { return internetGb; }
        public void setInternetGb(Integer internetGb) { this.internetGb = internetGb; }
        public Integer getMinutes() { return minutes; }
        public void setMinutes(Integer minutes) { this.minutes = minutes; }
        public Integer getSms() { return sms; }
        public void setSms(Integer sms) { this.sms = sms; }
    }
}
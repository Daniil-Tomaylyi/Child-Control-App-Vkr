## Настройка проекта

Для работы приложения необходим API-ключ Yandex Mapkit SDK. 

1. Получите свой API-ключ на сайте Yandex.
2. Откройте файл `local.properties` в корне вашего проекта Android.
3. Добавьте следующую строку, заменив `YOUR_API_KEY` на ваш ключ:
MAPKIT_API_KEY=YOUR_API_KEY

## Структура классов и папок

- **Service**: Сервисы блокировки устройства и приложений, а также геолокации.
- **addchild**: Классы для добавления ребенка.
- **admin**: Классы для административных прав.
- **applist**: Классы для списка приложений.
- **auth**: Классы для авторизации.
- **db**: Классы для локальной базы данных Room.
- **device time settings**: Классы для настройки ограничений по времени.
- **forgot password**: Классы для сброса пароля.
- **head child**: Классы для главной страницы ребенка.
- **head parent**: Классы для главной страницы родителя.
- **info app**: Классы с информацией о приложении.
- **parentmap**: Классы для карты.
- **reg**: Классы для регистрации.
- **role**: Классы для выбора роли.
- **settings child**: Классы для настроек ребенка.
- **settings parent**: Классы для настроек родителя.
- **start**: Классы для приветствия.
- **title**: Классы для создания аккаунта.

## Основные активности и классы

- **LockScreenActivity.kt**: Активность экрана блокировки приложения.
- **LockScreen Device Activity.kt**: Активность экрана блокировки устройства.
- **MainActivity.kt**: Основная активность приложения.
- **Main Application.kt**: Основной класс приложения, который инициализирует глобальное состояние приложения и управляет его жизненным циклом.
<div align="center">
  <!-- Логотип -->
  <img src="images/logo.png" alt="TourGuide Logo" width="200"/>

  <h1>TourGuide</h1>

  <p>
    <strong>Приложение для управления турами, расчета цен и системы вознаграждений.</strong>
  </p>

  <!-- Бейджи (опционально, можно удалить, если не нужны) -->
  <p>
    <img src="https://img.shields.io/badge/Java-17-blue" alt="Java 17"/>
    <img src="https://img.shields.io/badge/Spring_Boot-3.0-green" alt="Spring Boot"/>
    <img src="https://img.shields.io/badge/License-MIT-yellow" alt="License"/>
  </p>
</div>

---

## 📖 О проекте

**TourGuide** — это микросервисное приложение, которое помогает пользователям находить интересные места, планировать поездки и получать вознаграждения за активность. Проект демонстрирует использование современных подходов к разработке на Java, включая работу с внешними API и внутренними сервисами.

### Основные возможности:
*   **GPS Утилиты:** Расчет расстояния и поиск ближайших достопримечательностей (`gpsUtil`).
*   **Ценообразование:** Динамический расчет стоимости туров (`tripPricer`).
*   **Система лояльности:** Начисление и отслеживание баллов пользователя (`RewardsCentral`).

---

## 🏗 Архитектура

Приложение построено по принципу многоуровневой архитектуры (Layered Architecture). Ниже представлена диаграмма взаимодействия компонентов:

<!-- UML Диаграмма -->
![UML Architecture Diagram](images/uml-diagram.png)

### Слои приложения:
1.  **Couche présentation (Presentation Layer):** `TourGuideController` — обрабатывает входящие HTTP-запросы.
2.  **Couche métier (Business Layer):** `TourGuideService` и `RewardsService` — содержат основную логику приложения.
3.  **Services externes (External Services):** Внешние библиотеки/сервисы (`gpsUtil`, `RewardsCentral`, `tripPricer`), которые используются для получения данных.

---

## 🚀 Запуск проекта

### Требования
*   JDK 17 или выше
*   Maven 3.8+
*   Git

### Инструкция по установке

1.  **Клонируйте репозиторий:**
    ```bash
    git clone https://github.com/ваш-логин/TourGuide.git
    cd TourGuide
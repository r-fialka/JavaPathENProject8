<div align="center">
  <!-- Logo -->
  <img src="images/logo.png" alt="TourGuide Logo" width="200"/>

  <h1>TourGuide</h1>

  <p>
    <strong>An application for managing tours, price calculation, and rewards system.</strong>
  </p>

  <!-- Badges (optional, remove if not needed) -->
  <p>
    <img src="https://img.shields.io/badge/Java-17-blue" alt="Java 17"/>
    <img src="https://img.shields.io/badge/Spring_Boot-3.0-green" alt="Spring Boot"/>
    <img src="https://img.shields.io/badge/License-MIT-yellow" alt="License"/>
  </p>
</div>

---

## 📖 About the Project

**TourGuide** is a microservice-based application that helps users discover interesting places, plan trips, and earn rewards for their activity. The project demonstrates modern Java development practices, including integration with external APIs and internal services.

### Key Features:
*   **GPS Utilities:** Distance calculation and finding nearby attractions (`gpsUtil`).
*   **Pricing:** Dynamic tour price calculation (`tripPricer`).
*   **Loyalty System:** Earning and tracking user reward points (`RewardsCentral`).

---

## 🏗 Architecture

The application follows a layered architecture. Below is a diagram showing the interaction between components:

<!-- UML Diagram -->
![UML Architecture Diagram](images/uml-diagram.png)

### Application Layers:
1.  **Presentation Layer:** `TourGuideController` — handles incoming HTTP requests.
2.  **Business Layer:** `TourGuideService` and `RewardsService` — contain the core application logic.
3.  **External Services:** External libraries/services (`gpsUtil`, `RewardsCentral`, `tripPricer`) used for data retrieval.

---

## 🚀 Getting Started

### Prerequisites
*   JDK 17 or higher
*   Maven 3.8+
*   Git

### Installation

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/r-fialka/JavaPathENProject8.git
    cd JavaPathENProject8
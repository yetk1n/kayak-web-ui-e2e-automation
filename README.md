# 🚀 Kayak E2E Automation

![Java](https://img.shields.io/badge/Java-17-blue?style=flat-square&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen?style=flat-square&logo=springboot)
![Selenium](https://img.shields.io/badge/Selenium-4.25.0-green?style=flat-square&logo=selenium)
![JUnit 5](https://img.shields.io/badge/JUnit-5.10.1-orange?style=flat-square&logo=junit5)
![Docker](https://img.shields.io/badge/Docker-Enabled-blue?style=flat-square&logo=docker)
![Allure](https://img.shields.io/badge/Allure-Reporting-purple?style=flat-square&logo=allure)

This project is an end-to-end (E2E) test automation framework for the **Kayak** website. It supports running tests locally, with Docker, and in a Jenkins CI/CD pipeline. The framework also includes infrastructure for running tests in both web and mobile views in parallel.

---

## 📖 Table of Contents
- [📋 Prerequisites](#-prerequisites)
- [⚙️ Setup](#️-setup)
- [▶️ Running Tests](#️-running-tests)
    - [Run Locally](#run-locally)
    - [Run with Docker](#run-with-docker)
    - [Run with Jenkins](#run-with-jenkins)
- [🔧 Configuration](#-configuration)
    - [Application Properties](#application-properties)
    - [Mobile View Support](#mobile-view-support)
- [📊 Reporting](#-reporting)
- [📌 Notes](#-notes)

---

## 📋 Prerequisites

Ensure the following tools are installed on your system:

- **Java 17**
- **Maven 3.8+**
- **Docker** (for running with Selenoid)
- **Jenkins** (for CI/CD pipeline)

---

## ⚙️ Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/your-repo/kayak-e2e-automation.git
   cd kayak-e2e-automation
    ```
2. Install dependencies:
   ```bash
   mvn clean install
    ```

## ▶️ Running Tests
### Run Locally

To run tests locally without Docker or Jenkins:


1. Open the application.properties file (src/test/resources/application.properties).
2. Set remote.url to an empty value:
    ```ini
   remote.url=
    ```

3. Run the tests using Maven:
    ```bash
   mvn clean test
    ```

### Run with Docker
To run tests locally using Docker and Selenoid:

1. Start the Selenoid and Selenoid UI services using Docker Compose:
   ```bash
   docker-compose up -d
    ```
2. Open the application.properties file and set remote.url to http://localhost:4444/wd/hub/:

    ```
    remote.url=http://localhost:4444/wd/hub/
    ```
3. Run the tests using Maven:
    ```bash
   mvn clean test
    ```

---

### Run with Jenkins
To run tests in a Jenkins pipeline:

1. Ensure Jenkins is running and configured with Docker.

2. Open the `application.properties` file and set `remote.url` to:
   ```properties
   remote.url=http://selenoid:4444/wd/hub/
   ```

3. Use the provided `Jenkinsfile` to configure the pipeline. The pipeline will:
    - Build the project.
    - Run the tests.
    - Generate an Allure report.


---

## 🔧 Configuration

### Application Properties
The `application.properties` file (`src/test/resources/application.properties`) contains the following configurable parameters:

| Property     | Description                                                 | Default Value               |
|--------------|-------------------------------------------------------------|-----------------------------|
| base.url     | The base URL of the Kayak website.                          | https://www.kayak.com.tr/   |
| browser      | The browser to use for tests (chrome, firefox, edge).       | chrome                      |
| timeout      | The implicit wait timeout in seconds.                       | 30                          |
| headless     | Whether to run tests in headless mode (true or false).      | false                       |
| mobile.view  | Whether to enable mobile view testing (true or false).      | false                       |
| remote.url   | The URL of the remote WebDriver. Leave empty for local execution. | http://localhost:4444/wd/hub/ |

---

## 📱 Mobile View Support
The framework includes infrastructure for running tests in mobile view.

1. Set `mobile.view` to `true` in the `application.properties` file:
   ```properties
   mobile.view=true
   ```

2. The framework will automatically configure the browser for mobile emulation.

---

## 📊 Reporting
The framework generates test reports using Allure.

1. After running the tests, generate the Allure report:
   ```bash
   mvn allure:serve
   ```

2. Open the report in your browser to view detailed test results.

---

## 📌 Notes

- The framework supports parallel execution of tests using the `maven-surefire-plugin`.
- Ensure the `docker-compose.yml` file is correctly configured for your environment when running with Docker.
- The `Jenkinsfile` is pre-configured for running tests in a CI/CD pipeline with Allure reporting.
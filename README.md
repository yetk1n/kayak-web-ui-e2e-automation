# 🚀 Sahibinden.com E2E Automation Framework

A robust end-to-end test automation framework for [Sahibinden.com](https://www.sahibinden.com) featuring parallel execution of desktop and mobile tests.

## 🛠️ Technology Stack

- ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white) Spring Boot 3.2.5
- ![Selenium](https://img.shields.io/badge/Selenium-43B02A?style=for-the-badge&logo=selenium&logoColor=white) Selenium WebDriver 4.25.0
- ![Selenoid](https://img.shields.io/badge/Selenoid-00B4FF?style=for-the-badge&logo=selenium&logoColor=white) Selenoid (Grid)
- ![JUnit5](https://img.shields.io/badge/JUnit-25A162?style=for-the-badge&logo=junit5&logoColor=white) JUnit 5
- ![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white) Maven
- ![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white) Docker

## 📋 Features

- ✅ **Parallel Test Execution**: Run desktop and mobile tests concurrently
- 🌐 **Cross-browser Testing**: Support for Chrome, Firefox and Edge
- 📱 **Responsive Testing**: Dedicated mobile view tests with device emulation
- 🔄 **CI/CD Integration**: Ready-to-use Jenkins pipeline
- 📊 **Reporting**: Integrated Allure reporting
- 🐳 **Containerization**: Complete Docker setup with Selenoid

## 🏗️ Architecture

- **Page Object Model**: Clean separation of test logic and page interactions
- **Test Annotations**: Custom `@MobileTest` annotation for mobile-specific tests
- **Spring Boot Integration**: Configuration management and dependency injection
- **Thread-safe Design**: Safe parallel execution with ThreadLocal driver management

## 🚀 Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+
- Docker and Docker Compose

### Setup

1. **Clone the repository**:
   ```bash
   git clone <repository-url>
   cd sahibinden-e2e-automation
   ```

2. **Start Selenoid with Docker**:
   ```bash
   docker-compose up -d
   ```

3. **Verify Selenoid is running**:
    - Access Selenoid UI: http://localhost:8080
    - Check Selenoid status: http://localhost:4444/status

## ▶️ Running Tests

### Via Maven

Run all tests with Maven:

```bash
mvn clean test
```

Run specific test class:

```bash
mvn test -Dtest=AracDegerlemeTest
```

Specify browser and remote URL:

```bash
mvn test -Dbrowser=chrome -DremoteUrl=http://localhost:4444/wd/hub
```

### Via Jenkins

#### Setup Jenkins Pipeline

1. **Access Jenkins**:
    - Open http://localhost:8081
    - If accessing for the first time, retrieve the initial admin password:
      ```bash
      docker exec -it <jenkins-container-id> cat /var/jenkins_home/secrets/initialAdminPassword
      ```

2. **Install Suggested Plugins** during first-time setup

3. **Configure Maven**:
    - Go to Jenkins > Manage Jenkins > Tools
    - Add Maven Installation:
        - Name: `maven3`
        - Install automatically: ✓
        - Version: Select latest version

4. **Create a Pipeline Job**:
    - Click "New Item"
    - Enter a name for the job (e.g., "Sahibinden-E2E-Tests")
    - Select "Pipeline" and click OK
    - In Configuration:
        - Under Pipeline section:
            - Definition: "Pipeline script from SCM"
            - SCM: Git
            - Repository URL: `https://github.com/yetk1n/e2e-ui-automation-with-docker-selenium-jenkins.git`
            - Branch Specifier: `*/main`
            - Script Path: `Jenkinsfile`
    - Click Save

#### Run Tests in Jenkins

1. **Start a Build**:
    - Navigate to your pipeline job
    - Click "Build Now"

2. **With Parameters** (optional configuration):
    - Enable parameterized builds in job configuration
    - Add parameters:
        - `BROWSER`: Choice parameter (chrome, firefox, edge)
        - `REMOTE_URL`: String parameter (http://selenoid:4444/wd/hub/)
        - `TEST_CLASSES`: String parameter (specific test class to run)

3. **View Test Results**:
    - After the build completes, click on the build number
    - View "Test Result" for JUnit results
    - View "Allure Report" for detailed test reporting

4. **Debug Failed Tests**:
    - Check console output for errors
    - Review screenshots in build artifacts

## 📂 Project Structure

```
├── src/test/java/com/sahibinden
│   ├── config              # Configuration classes
│   ├── driver              # WebDriver management
│   ├── pages               # Page Object classes
│   ├── tests               # Test classes
│   └── utils               # Utility classes
├── src/test/resources      # Test resources and properties
├── docker-compose.yml      # Docker services configuration
├── selenoid               # Selenoid configuration
│   └── browsers.json      # Browser images configuration
└── Jenkinsfile            # CI/CD pipeline definition
```

## 📊 Reports

Generate Allure reports after test execution:

```bash
mvn allure:report
```

View the report:

```bash
mvn allure:serve
```

## 🔍 Testing Features

The framework includes tests for:

- Vehicle valuation (Araç Değerleme)
- Listing search and filtering
- Price sorting validation
- Mobile-responsive behavior

## 🌟 Key Implementation Details

- **Parallel Execution**: Configured in `pom.xml` with `parallel=classes` and `threadCount=2`
- **Mobile Testing**: Custom `MobileTest` annotation and `MobileTestExtension` class
- **Browser Configuration**: Stealth browser options to avoid detection
- **Selenoid Integration**: Container configuration in `browsers.json`

## 🔧 Configuration

Core settings in `application.properties`:

```properties
base.url=https://www.sahibinden.com
browser=chrome
timeout=30
headless=true
mobile.view=false
remote.url=http://localhost:4444/wd/hub/
```

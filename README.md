# TPA insurance (assignment)

A brief description of the main purpose and functionality of this project.

## Technologies Used

This project is built with the following technologies and versions:

*   **Java:** 17
*   **Spring Boot:** 3.5.10
*   **Maven** 
*   **Log4j2** for logging
*   **MariaDb** database

## Project Architecture

The project follows a traditional Spring Boot layered architecture (Controller, Service, Repository) and has a specific folder structure as follows:

src/main/
├── java/
│ └── com/insurance/assignment/
│ ├── controller/ # Handles HTTP requests
│ ├── service/ # Contains the core business logic
│ ├── repository/ # Interacts with the database (using Spring Data JPA)
│ ├── model/ # Object classes (Entities, DTOs)
│ └── TpaInsuranceAssignmentApplication.java # The application launch class
└── resources/
├── conf/ # Custom configuration directory
│ ├── application.yaml # Application configuration
│ └── log4j2.xml # Log4j2 configuration

## Setup and Running the Project

Follow these steps to run the project on your local environment:

### System Requirements

Make sure you have the following software installed:

*   **JDK 17** or later
*   **Maven**
*   An IDE like IntelliJ IDEA, VS Code, or Eclipse

### Steps

1.  **Clone** the repository to your computer:
    ```bash
    git clone https://github.com
    ```

2.  **Navigate** to the project directory:
    ```bash
    cd <path>/tpa-insurance
    ```

3.  **Build** the project using Maven:
    ```bash
    ./mvnw clean install
    ```

4.  **Run** the Spring Boot application:
    ```bash
    ./mvnw spring-boot:run
    ```

The application will run at `http://localhost:8584` (default).

## Configuration

The main configuration file `application.yaml` and the log configuration `log4j2.xml` are located in the `conf/` directory.

*   **`application.yaml`**: Contains settings for the port, database, etc.
*   **`log4j2.xml`**: Defines how and where application logs are stored.

## API Endpoints (Example)

Here are some example API endpoints (if applicable):

*   `POST /api/claim`: Create a claim.
*   `GET /api/claims/{claimId}`: Retrieve a claim.
*   `GET /api/claims?limit={limit}&offset={offset}&claimStatus={claimStatus}&policyId={policyId}`: Retrive a list of claims with filter
*   `PATCH /api/claims/{claimId}`: Update status a claim

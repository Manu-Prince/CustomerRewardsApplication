🎁 Customer Rewards Application

A Spring Boot application to manage customer transactions and calculate monthly reward points based on their spending.

📌 Features

- Calculate rewards earned within a specific date range
- REST API endpoints with proper validation and exception handling
- Integration tests for the controller and service layers
- Logs application activity into the console and file (logback)

🧰 Tech Stack

Java 21
Spring Boot 3
Spring Data JPA
Hibernate
MySQL
JUnit 5
Mockito

🎯 Reward Calculation Rules

 1. $50–$100 → 1 point per dollar over $50      
 2. $100+ → 2 points per dollar over $100 + 1 point per dollar between $50–$100

📦 Project Structure

Customer-Rewards-App/
<pre>
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/rewardapp/
│   │   │       ├── rewardcontroller/
│   │   │       │   └── RewardController.java
│   │   │       ├── rewardprogrammodel/
│   │   │       │   ├── TransactionModel.java
│   │   │       │   └── RewardSummary.java
│   │   │       ├── rewardexception/
│   │   │       │   ├── CustomerNotFoundException.java
│   │   │       │   ├── ErrorDetails.java
│   │   │       │   ├── APIExceptionHandler.java
│   │   │       │   ├── InvalidDateFormatException.java
│   │   │       │   ├── InvalidDateRangeException.java
│   │   │       │   └── MissingServletRequestParameterException
│   │   │       │   ├── RewardCalculationException  
│   │   │       ├── rewardentity/
│   │   │       │   └── Transaction.java
│   │   │       ├── rewardrepository/
│   │   │       │   └── TransactionRepository.java
│   │   │       ├── rewardservice/
│   │   │       │   └── RewardsService.java
│   │   │       ├── rewardutil/
│   │   │       │   └── RewardCalculator.java
│   │   │       └── RewardProgramApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       │──logback-spring.xml
│   └── test/
│        └── java/
│        │   └── com/rewardapp/
│        │       ├── rewardControllertest.java
│        │       ├── RewardServiceTest.java
│        │       └── RewardIntegrationTest.java
│        └──resources/
│             └── application.properties
├── logs/
│    └── CustomerRewards.log      
├── pom.xml
└── README.md
</pre>pre>

🏋️️ Layers Breakdown

1. Model/Entity Layer
      JPA entities: Transaction    
      Mapped to relational database tables using Hibernate

2. DTO Layer 
      RewardSummary and TransactionModel encapsulate input data 
      Help decouple internal entity structure from API contracts 
      Validated using jakarta.validation annotations

3. Validation Layer
      Enforces constraints using annotations like:@NotNull, @NotBlank   
      Validation errors are automatically handled via @Valid

4. Repository Layer    
      Uses Spring Data JPA to persist entities    
      Interface-based approach with JpaRepository

5. Service Layer    
      Business logic for calculating rewards    
      Maps DTOs to JPA entities    
      Delegates data access to the repository layer

6. Controller Layer    
      Handles HTTP requests/responses    
      Accepts validated TransactionModel via @RequestBody @Valid    
      Routes requests to RewardsService

7. Exception Handling Layer    
      Centralized error handling via @RestControllerAdvice in GlobalExceptionHandler    
      Catches custom exceptions like CustomerNotFoundException, InvalidRequestException, etc.    
      Provides consistent error responses with appropriate HTTP status codes    
      Handles validation errors and unhandled exceptions gracefully
📡 REST API Endpoints

Add Customer
POST: /api/rewards/transaction

Request Body-


     {
          
            "amount": 101,
            "customerName": "Manu",
            "date": "2025-04-12"
            

    }

Response:
201 Transaction received successfully.

Get All Customer
GET: /api/rewards?startDate=2024-04-01&endDate=2024-06-30

Response:

    [
    {
       
        "customerName": "Satyam",
        "totalPoints": 270,
        "monthlyPoints": {
            "2024-04": 150,
            "2024-06": 30,
            "2024-05": 90
        },
      
        "transactions": [
            {
                "date": "2024-05-15",
                "amount": 120.0,
                "points": 90
            },
            {
                "date": "2024-04-10",
                "amount": 150.0,
                "points": 150
            },
            {
                "date": "2024-06-15",
                "amount": 80.0,
                "points": 30
            }
        ]
    },
    {
        "customerName": "ManuTiwari",
        "totalPoints": 110,
        "monthlyPoints": {
            "2024-04": 110
        },
        "transactions": [
     {
           
                "date": "2024-04-12",
                "amount": 130.0,
                "points": 110
            }
        ]
    },
   
    {
        "customerName": "TanuTiwari",
        "totalPoints": 70,
        "monthlyPoints": {
            "2024-06": 70
        },
       
        "transactions": [
            {
                "date": "2024-06-12",
                "amount": 110.0,
                "points": 70
            }
        ]
    }]
    

🚀 Setup Instructions

1. Clone the Repository

git clone https://github.com/Manu-Prince/CustomerRewardApplication.git
cd CustomerRewardApplication

2. Configure MySQL Database

Create a database named rewardprogram and update src/main/resources/application.properties 
if needed:

      spring.datasource.url=jdbc:mysql://localhost:3306/rewardprogram
      spring.datasource.username=root
      spring.datasource.password=admin

3. Build and Run

mvn clean install
mvn spring-boot:run
Application will be accessible at: http://localhost:8083

📂 Log Configuration

Logs are written to both the console. Only application logs are enabled (others suppressed).
🧪 Run Tests

mvn test

Includes integration tests for:
      - Validating transactions
      - Calculating rewards

👩‍💻 Author

           Manu Tiwari
    (Senior Associate Consultant)

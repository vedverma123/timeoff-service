# Time Off Service

The Time Off Service is responsible for managing employee time-off requests. It ensures that new requests do not conflict with existing ones by leveraging a rule engine and handles all date-time operations with UTC for consistency. The service also supports retrieval of time-off requests in the employee's local time zone.


## Features
#### Add Time-Off Request: Validates and adds new time-off requests.
#### Conflict Detection: Prevents overlapping time-off requests using the rule engine.
#### Time Zone Handling: Converts all dates to UTC for storage, then converts back to local time on retrieval.


## Technologies
#### Java 17
#### Spring Boot
#### JPA (Hibernate)
#### Lombok

### Methods
#### addTimeOffRequest(TimeOffRequestDto request): Validates and stores the time-off request.
#### getRequestsForEmployee(UUID employeeId, ZoneId employeeZoneId): Retrieves time-off requests for a specific employee in their local time zone.

## In Memory Database
Currently, application is configured to run with H2 database accessible at url [http://localhost:8551/h2-console/login.jsp]()
with following properties.

```
   JDBC URL : jdbc:h2:mem:timeoff_service
   User name : sa
   Password : password
```   



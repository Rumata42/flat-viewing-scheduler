### Build and run

Use gradle commands:

```./gradlew build```

```./gradlew run```

### Endpoints

The server listen 8080 http port. There are four endpoints:

GET ```/flat/{id}``` - to get all slots for a flat

POST ```/flat/{id}/reserve?tenantId={}&dateTime={}``` - to reserve some slot for a flat

POST ```/flat/{id}/accept?tenantId={}&dateTime={}``` - to accept the reservation

POST ```/flat/{id}/reject?tenantId={}&dateTime={}``` - to reject the reservation

Where:
* ```id``` - identifier of the flat
* ```tenantId``` - identifier of the active tenant (instead of authorization)
* ```dateTime``` - date and time of the slot (in format ```yyyy-MM-dd'T'HH:mm```)

### Testing

While there are no CRUD operations, only flat with id = 1 and owner tenant = 10 is present.

To check notifications see log.

Here an example of curl commands to check the positive scenario of the program:
```
curl -X POST "http://localhost:8080/flat/1/reserve?tenantId=20&dateTime=2020-11-18T12:20"
curl -X POST "http://localhost:8080/flat/1/reserve?tenantId=20&dateTime=2020-11-18T12:00"
curl -X POST "http://localhost:8080/flat/1/accept?tenantId=10&dateTime=2020-11-18T12:00"
curl -X POST "http://localhost:8080/flat/1/reserve?tenantId=20&dateTime=2020-11-17T12:20"
curl -X POST "http://localhost:8080/flat/1/reserve?tenantId=20&dateTime=2020-11-17T12:00"
curl -X POST "http://localhost:8080/flat/1/accept?tenantId=10&dateTime=2020-11-17T12:00"
curl -X POST "http://localhost:8080/flat/1/reject?tenantId=10&dateTime=2020-11-17T12:20"
curl -X POST "http://localhost:8080/flat/1/reserve?tenantId=20&dateTime=2020-11-18T14:00"
curl -X POST "http://localhost:8080/flat/1/reject?tenantId=20&dateTime=2020-11-17T12:00"
curl "http://localhost:8080/flat/1/slots"
```
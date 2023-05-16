# Solution

## To Run

```bash
$ mvn spring-boot:run
```

## To Test

```bash
# Register

$ curl -X POST \
  http://localhost:8080/register \
  -H 'content-type: application/json' \
  -d '{
	"email": "test@test.com",
	"password": "test1234"
}'

# Login

$ curl -X POST \
  http://localhost:8080/login \
  -H 'content-type: application/json' \
  -d '{
	"email": "test@test.com",
	"password": "test1234"
}'


# Test API

$ curl -X GET \
  http://localhost:8080/api/notes \
  -H 'authorization: Basic dGVzdEB0ZXN0LmNvbTp0ZXN0MTIzNA==' 
```

## To Run Junit

```bash
$ mvn test
```

[![CircleCI](https://circleci.com/gh/vision-i40/customer_registry_service.svg?style=svg)](https://circleci.com/gh/vision-i40/customer_registry_service)

# Customer Registry Service

Vision i4.0 is a opensource project which aims to help customer from industry to have a simple and efficiente way to store, analyse and improve the daily work in their proccess.

The customer registry service (CRS) is part of Vision i4.0 and is a micro service of his ecosystem which has the responsibility of 

## Sign Up

The signup endpoint should be used to add a new company and root user. It works as follows:

| **Method**            | /auth/signup          |
|-----------------------|:---------------------:|
| **Resources**         | POST                  |
| **Request Payload**   | ` {"company_name": "Awesome Company", "company_slug": "awesome-co","user_email": "email@email.com.br","user_name": "An Awesome Name","user_password": "awesome-hard-pwd"}` |
| **Request Response**  | `{"token": "a.jwt.string"}` |
| **Error Response**    | `{"message": "error message"}` |

## Sign In
The singin endpoint should be use in order to retrieve a token to be used in backend/front-end authentication. It works with the following parameters:

| **Method**            | /auth/signin          |
|-----------------------|:---------------------:|
| **Resources**         | POST                  |
| **Request Payload**   | ` {"email": "email@email.com", "passsword": "awesome-hard-pwd"}` |
| **Request Response**  | `{"token": "a.jwt.string"}` |
| **Error Response**    | `{"message": "error message"}` |


## Authenticated endpoints:

All authenticated endpoints expects the [JWT](https://jwt.io/) token in request header with the following structure:
```
   Authentication: Bearer <THE.JWT.TOKEN>
```

So it is going to authenticate and give access only to the resource which the user has permission. If the request succeeds, the response will be on the 2xx HTTP family depending on the endpoint funcionality, but if it fails due to authentication error it is going to respond with 401 (Unauthorized) HTTP with a simple message depending on the error:

```
{
  "message": "<AUTHENTICATION ERROR MESSAGE>"
}
```

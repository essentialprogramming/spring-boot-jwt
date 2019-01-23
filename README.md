# Spring Boot JWT application

Spring Boot JWT sample application is designed to incorporate a Resource Server and an Auth Server in one application, with separated logic and flows.

## Overview

The authorization flow is implemented by following the OAuth 2.0 specification. As the client is represented by a browser in our scenario which cannot store the users's credentials in a secure way, the Implicit grant is used to return the token directly. 
More about OAuth2 specification and flows [here](https://github.com/digital-consulting/oauth2) 

![oauth-diagram-implicit-spring-jwt](doc/oauth-diagram-implicit-spring-jwt.png) 


## Resource (Web) server

The Resource Server's endpoints are protected from external request and are only accessible with valid JWT token emitted by the Auth Server.
The verification process consists in a filter chain containing the following two filters:
* **Cookie verification filter**: for every incoming request for the protected resources Cookie is verified first. If it exists and it contain a valid JWT token, authorization is successful, else the filter chain jumps to the following filter: 
* **JWT token verification filter**: if no successful Cookie authorization happened before the token verification filter checks if a valid JWT token is present as URL parameter. If not, the filter redirects to the Auth server, otherwise authentication is considered successful and a JWT token is set on the Cookie.
 
## Auth server

The Auth server uses a Basic authentication with an implicit full access authorization for every successfully authenticated user. With the obtained token all of the protected resources of the Resource Server are accessible.   


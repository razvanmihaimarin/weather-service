#Weather Service
---
Welcome to the Weather Service

### Introduction

This is a simple application that requests its data from [OpenWeather](https://openweathermap.org/), 
persists it in a PostgreSQL database and caches the information in Redis for 24 hours.
In order to avoid calling the API multiple time, the endpoint will persist the weather information hourly(at UTC timezone),
meaning the first API request in that hour will store and cache the information. Subsequent requests will either return
the data from the cache(if not expired) or the database. This was a premeditated decision, as the temperature information
returned by the WeatherApi is not likely to change in dramatic fashion in the time span of an hour.

**Prerequisites:**
[Java 11](https://adoptopenjdk.net/),
[Docker](https://www.docker.com/),
[Maven](https://maven.apache.org/),
[Git](https://github.com/git-guides/install-git).

* [Getting Started](#getting-started)
* [Help](#help)
* [Links](#links)

## Getting Started

To run this application, run the following commands:

```bash
git clone https://github.com/razvanmihaimarin/weather-service.git
cd weather-service
mvn clean install
docker-compose up
```

## Links

This example uses the following open source projects:
* [Spring Boot](https://spring.io/projects/spring-boot)

## Help
For any questions you might have regarding this project, please email them at: razvanmihaimarin@gmail.com .
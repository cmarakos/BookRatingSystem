# Book Rating System

## Overview

This project is a simple REST API for searching and rating bookDtos using the Gutendex API and a local H2 database for
storing ratings and reviewEntities.

## Running the Application

1. **Build and Run Locally**
   ```bash
   mvn clean install
   java -jar target/BookRatingSystem-0.0.1-SNAPSHOT.jar

2. **Run with Docker-Compose**
   ```bash
   docker-compose up --build

## Test calls

At folder src/main/resources/ Test-Calls.http can be found with some example calls to test basic functionality
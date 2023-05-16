# DISYS_Projekt
Fuel Station Data Collector

Implement a distributed system with a REST-based API, a RabbitMQ message queue and a JavaFX UI.
Scenario

You work in a company, that manages charging stations for electric cars. Every charging station keeps track of the customer that used the charger and the amount of kWh the customer used.

Your task is to build an application that generates an invoice PDF for a given customer.

Use JavaFX to create the UI for the application.

Use Java Spring Boot to create the REST-based API.

Use RabbitMQ to manage the message queue.

The workflow is as follows:

    You can input a customer id into the UI and click “Generate Invoice”
    A HTTP Request calls the REST-based API
    The application starts a new data gathering job
    When the data is gathered, it gets send to the PDF generator
    The PDF generator generates the invoice and saves it on the file system
    The UI checks every couple seconds if the invoice is available

Setup

You have a docker-compose project that sets up five databases and a queue. One database stores user information, one database stores the access information of the other three databases, which itself store the charging information. 

We have a customer database, a station database and every charging station has its own database. The data is distributed! Read the README.md in the project.zip to get more information.
Specifications

There are five services that work on the message queue:
Spring Boot App

    Starts the process by sending a start message with the customer ID to the Data Collection Dispatcher

Data Collection Dispatcher

    Starts the data gathering job
    Has knowledge about the available stations
    Sends a message for every charging station to the Station Data Collector
    Sends a message to the Data Collection Receiver, that a new job started

Station Data Collector

    Gathers data for a specific customer from a specific charging station
    Sends data to the Data Collection Reciever

Data Collection Receiver

    Receives all collected data
    Sort the data to the according gathering job
    Sends data to the PDF Generator when the data is complete

PDF Generator

    Generates the invoice from data
    Saves PDF to the file system

There are two API routes:

/invoices/<customer-id> [POST]

    Starts data gathering job

/invoices/<customer-id> [GET]

    Returns invoice PDF with download link and creation time
    Returns 404 Not Found, if it’s not available

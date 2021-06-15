# Dalma FEATS Broker

- This project contains the API to interact with FIWARE context broker - Orion.
- All communication between the backend and Orion is through this API, this means that all the other components (LATTE, Fi-BREW, COFFEE) access this API
to perform some action in the Broker. This action can be anything: create/get/update/delete an entity, or manipulate subscriptions for example.

## Table of Contents

- [Requirements](#requirements)
- [Installation](#installation)
- [Run](#run)
- [Endpoints](#endpoints)
- [Notes](#notes)
- [Troubleshooting](#troubleshooting)
- [Postman Collection](#postman)


## Requirements
Install the following tools:
- [mvn](https://maven.apache.org/install.html)
- [jdk11](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html)

## Installation
### API
- Clone this repo to your local machine using `https://gitlab.com/bright-technologies/dalma/broker.git`
- Compile API

```shell
$ cd <project_cloned>
$ mvn clean install
```

### Broker
- Broker requires mongo database, thus install the following two docker images:

#### Mongo image
```shell
$ docker run --name mongodb -d mongo:3.4
```

#### Orion image
```shell
$ docker run -d --name orion1 --link mongodb:mongodb -p 1026:1026 fiware/orion -dbhost mongodb
```

## Run
- Enter in project cloned folder and execute api: `java -jar broker-api-web/target/dalma-broker-api.jar`

## Endpoints
- All endpoints are exposed in Swagger at: `http://localhost:8090/swagger-ui.html`.
- All controllers expose the CRUD actions for the related entities: `Idle Station`, `Robot`, `Subscription`, `Warehouse`, `Warehouse Material`, `Work Order`, `Work Station`.
- Critical endpoints at Robot Controller:
  1. `/notification/status`: This is the endpoint used in Orion callback to notify about Robot attributes change: `battery`, `status`, `location` and `heartbeat`.
- Critical endpoints at WorkOrder Controller:
  1. `/integrate`: This is the endpoint called by COFFEE to integrate work orders from SAP in Orion.
  2. `/notification/integrate/latte`: This is the endpoint used in Orion callback to notify that Work Order integration has finished. This Broker API will call LATTE to schedule the work orders execution.
  3. `/notification/integrate/fibrew`: This is the endpoint used in Orion callback to notify that Work Order integration has finished. This Broker API will call Fi-BREW to integrate work orders in Modula's database.

This notion of "work order integration finished" was created because the integration is not just insert the work order in Orion, then it is associated with `references` the warehouse and the workstation. Only when everything is updated in Orion, it is created the attribute `integrated` that will trigger these callbacks.

## Notes
### Idle Stations
- The system was prepared to support multiple robots, and therefore multiple idle stations. But the idle station is not reserved forever to a specific robot.
- Every time that a robot ends a work order, the system search for a idle station that is not occupied by any other robot, picks the first one, mark the idle station as occupied and insert in Orion a `reference` attribute to the Robot that is using the idle station.
- Following the same logic, every time that the robot starts a new work order, the idle station is marked as available to be usable by any robot.

### Coordinates
- The attribute location in all entities is a `geo:json` field, with the type `Point`.
- This type, by default, contains an array of `coordinates` where the first value is `latitude` and the second `longitude`.
- However, the robot is not expecting this order, thus, it is being used the order `longitude` and then `latitude`.

## Troubleshooting
- If for some reason the work order did not start with the log exception `NoneRobotAvailableException` check the following in Orion's robot entity:
  1. Attribute `available`: This attribute is defined with the addition of the `battery level`, `status`, and if is executing a `work order`
  2. Check if the minimum battery level was changed

## Postman
- The postman collection for all FEATS system is available in LATTE repository.

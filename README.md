# Cumulocity / DevEdge IoT Framework

------------

Cumulocity DevEdge is Java lightweight  Internet of Things (IoT) framework, useful to build and manage modern Cumulocity Edge gateway. 
It provides functionality to create, connect, process, store the data at the edge and in the Cumulocity IoT platform instance. 
DevEdge can run also on Raspberry Pi and other platforms will be supported in future versions.

## Technical Details
The framework is organized as Maven multi-module project which is consisted of a mandatory component core. The core provides all functionality needed.
to create an edge gateway. For the implementation of a given case, it is necessary to create a new module which depends on the core module.
I have provided an example to you how to easily and quickly create a simple edge gateway.

## Requirements:
- Java Open JDK version 8 or Oracle jdk
- Pi4J libraly
- Maven version 2 or 3
- Cumulocity Java SDK

## Development status:
Current released version: **0.0.1(pre-alpha)**

## Features:
The following functionalities are currently available in Pi4 Dev:
- creating/registering an edge device in the Cumulocity IoT platform(basic information (id, type, and name) about devices).
- creating/registering child devices in the Cumulocity IoT platform.
- listening/handling operations.
- monitoring devices.
- Cumulocity Events Retriever: retrieves events from Cumulocity IoT platform.
- Cumulocity Events Creator: write events back to Cumulocity IoT platform.
- Cumulocity Measurements Creator: writes measurements back to Cumulocity IoT platform.

## Quick Start

You'll need the following Maven dependency to start writing a new edge:

```xml
<dependency>
	<groupId>c8y.pi4.dev.agent</groupId>
	<artifactId>c8y-pi4-dev-core</artifactId>
	<version>0.0.1-SNAPSHOT</version>
</dependency>
```

You'll have to create a directory (META-INF/service) on your classpath. That directory contains a file with the fully qualified name of your abstract service class, like` c8y.pi4.agent.core.driver.Driver`
This files contains the details of its implementation: `c8y.rpi.driver.GatewayRpiHardwareDriver`.

## Demo, examples
I have provided to you a demo implementation of an edge which sent the system information of Rasbery Pi SoC  to Comulosity. 
A brief description of implementation aspects: I have created the `GatewayRpiHardwareDriver` class which implements the following core interface  `Driver, OperationExecutor` and `HardwareProvider`. Also there is the RpiTemperatureSensor class which is responsible for processing CPU Temperature from the Raspberry Pi and send it to Cumulocity Cloud instance.
Please look at the project with a name `c8y-dev-rpi-demo`.

[![Cumulocity child device](https://github.com/iqnev/resources/blob/main/4.JPG "Cumulocity child device")](https://github.com/iqnev/resources/blob/main/4.JPG "Cumulocity child device")

## Source code
This is an alpha version of the framework, and i am going to continue with the addition of new functionality and improving the architecture.

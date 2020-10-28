# Cumulocity / Java lightweight framework with support for Raspberry Pi

------------

This framework provides functionality to create, connect, process, store the data at the edge and in the Cumulocity IoT platform instance. 
It consists of scalable, fully managed Cumulocity services: an integrated software stack for the edge with capabilities for all your IoT needs.

## Technical Details
The framework is organized as Maven multi-module project which is consisted of a mandatory component core. The core provides all functionality needed.
to create an edge gateway. For the implementation of a given case, it is necessary to create a new module which depends on the core module.
I have provided an example to you how to easily and quickly create a simple edge gateway.


## Features:
The following functionalities are currently available in Pi4 Dev:
- creating/registering an edge device in the Cumulocity IoT platform(basic information (id, type, and name) about devices).
- creating/registering child devices in the Cumulocity IoT platform.
- listening/handling operations.
- monitoring devices.
- Cumulocity Events Retriever: retrieves events from Cumulocity IoT platform.
- Cumulocity Events Creator: write events back to Cumulocity IoT platform.
- Cumulocity Measurements Creator: writes measurements back to Cumulocity IoT platform.

## How to create a new edge

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

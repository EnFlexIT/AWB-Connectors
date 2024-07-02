# Generic connector implementation

## Overview

To minimize the number of open connections and ports, connectors are used to collect all connections from different entities inside 
an AWB instance towards the same server/broker/... using the same protocol, and act as a kind of proxy towards the respective counterpart. 
For example, all MQTT subscriptions towards the same broker from different agents running in the context of an AWB instance are bundled 
by one MQTTConnector instance.

## ConnectorManager

The connector manager is a singleton that manages the connectors for an AWB instance. It has a set of **configured connectors**, which are
basically *Properties* objects containing all relevant settings for a specific connector, and **available connectors**, which are instances 
of the *AbstractConnector* subclass of the respective protocol. Configured connectors can be created and edited using the UI, and are stored
to a JSON file and loaded by the *ConnectorManager* at start-up. If the *ConnectorService* for the connector's protocol is available at that 
time, the *ConnectorManager* will also create an instance of the actual connector class based on the properties, and add it to the available 
connectors. This also happens when a new *ConnectorService* is added at runtime, and there is a configured connector for the protocol provided
by this service. The *ConnectorManager* also takes care of starting connections at the time configured in their properties, e.g. at AWB startup
or simulation start. Agents and other entities from within the AWB instance can acquire available connectors from the *ConnectorManager*, either 
by their configured name or by the combination of protocol and host/IP. 

## Implementing new connectors

To implement a connector for a new protocol,  at least three things are required:

### The connector service

An implementation of the *ConnectorService* interface, making the respective connector implementation known to the ConnectorManager. This
lightweight interface mainly provides one method to instantiate the respective connector, and a method providing the protocol name to
easily identify the correct service implementation.

### The actual connector

A subclass of *AbstractConnector*, providing the actual connector implementation. This will usually use a library to implement the
protocol-specific communication tasks, and act as an intermediator between the communicating entities inside the AWB and the external
counterpart.

### A connector configuration

A subclass of *AbstractConnectorConfiguration*. While the actual settings are stored in a generic Properties object, and thus can also
be processed if the connector service for the protocol is not (yet) available, the configuration class acts as a protocol-aware wrapper
for the properties, providing convenient getter and setter methods for required properties, as well as public String constants for the 
property names. The superclass does so for properties that are relevant for all kinds of connectors, it is strongly recommended to provide 
a subclass for protocol-specific properties. If not needed for your protocol, this class can be empty and just provide the superclass 
methods.   
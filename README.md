# FIX-Integrations
FIX client/server sample app for FX market data and trading using quickFix engine.

[![Build Status](https://travis-ci.com/Pirimid/fix-integrations.svg?branch=master)](https://travis-ci.com/Pirimid/fix-integrations)

## Getting Started

1. Clone the repository
2. Do mvn clean install on both `FxFixAcceptor` and `FxFixInitiator` projects. To do that you can run below commands in root directory of this repository. 
   1. `mvn -f ./FxFixAcceptor/ clean install`
   2. `mvn -f ./FxFixInitiator/ clean install`
3. Start the Acceptor `StartAcceptor.class`
4. Start the Initiator `StartInitiator.class`

- You can modify the configurations of acceptor and initiator in `acceptorSettings.txt` and `initiatorSettings.txt` files.


## Types of Requests that are supported

### Client/Initiator can send the different trading requests
1. Spot
2. Forward
3. NDF
4. Unsubscribe
5. New Order Single

### Server/Acceptor can handle all above mentioned requests from client and can send response according to the type of request it gets
1. Full Refresh
2. Incremental Refresh
3. Execution Report

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

### MarketData Requests
1. Deal Types : Spot, Forward , NDF
2. Full/Incremental Refresh

### Trading
1. NewOrderSingle
2. ExcutionReports

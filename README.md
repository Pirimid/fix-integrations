# fix-integrations
FIX client/server sample app for FX market data and trading using quickFix engine.

## Types of Requests that are supported by this Sample Application

### Initiator
 1. Market Data Request
    1. Spot
    2. FWD
    3. NDF
### Acceptor
 1. Market Data Full Refresh
    1. Spot : It will continuously send the Market Data Full Refresh reaponse back to initiator until the initiator is available.
    2. FWD
    3. NDF

# Sender Implementation for Payable Services

Read more about this Payable API implementation here: http://blockchainers.tech/pay-robots-with-crypto-money/

This project implements the Sender ("Client") of payments provided by the [crypyapi-receiver](https://github.com/ice09/crypyapi-receiver). A new Sender implementation would clone this repo and look into `EthereumClient`integration tests for usage.

## New Payment Senders Setup

The Payment Receipt is transferred via HTTP Headers, so the service calls do not have to know about the Payment part.

### Sender Configuration

This project uses Spring Boot, but the process flow can easily be reused in other Java projects or even ported to other platforms like .NET, Python etc.

#### Properties

All properties are defined in `application.properties`. These are intentionally empty for most properties, for real values see `test.properties`.

    # Use Sokol Testnet
    ethereum.rpc.url=https://sokol.poa.network
    
    # Private Key for Client Account // Make sure to change for own account
    sender.private.key=0x4d8b2017fb8c3b7f57199ebc3ee0fc2e4f00e8585997b213d391311e4c65b1b8
    
    # Service URL
    service.url=http://localhost:8889/chucknorris

* The sender private key can be extracted from a wallet like MetaMask or Nifty or being created by `ethereum-signer`.
* The RPC URL must be known and can be any valid EVM Chain, we use Testnet URLs only for the POA Network.

### Sender Implementation

You just have to call the REST services as you would with any framework you like. In our example we use `RestTemplate` from Spring Boot, but you are free to use any framework which allows to add HTTP Headers to the requests.

The Headers are created by the framework, you just have to call

    HttpHeaders headers = paymentHeaderService.createPaymentHeaders(senderCredentials.getAddress(), "chuckNorrisService");

and make sure that the account defined by the configured private key has some amount of the native currency (eg. SPOA for the Sokol Testnet).

The service name (*"chuckNorrisService"*) as well as the service URL have to be provided by the payment receiver.


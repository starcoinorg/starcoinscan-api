## StarcoinScan-API
Starcoinscan-api
[![GitHub Action](https://github.com/starcoinorg/starcoinscan-api/workflows/Java%20CI/badge.svg)](https://github.com/starcoinorg/starcoinscan-api/actions?query=workflow%3A%22Build+Docker%22)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=starcoinorg_starcoinscan-api&metric=coverage)](https://sonarcloud.io/dashboard?id=starcoinorg_starcoinscan-api)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=starcoinorg_starcoinscan-api&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=starcoinorg_starcoinscan-api)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=starcoinorg_starcoinscan-api&metric=ncloc)](https://sonarcloud.io/dashboard?id=starcoinorg_starcoinscan-api)

[explorer.starcoin.org](https://explorer.starcoin.org/) Java API implementation.

Library supports all available StarcoinScan *API* calls for all available *Starcoin Networks* for *explorer.starcoin.org*

## Dependency :springboot:
**Maven**
```xml
<dependency>
  <groupId>org.starcoin</groupId>
  <artifactId>scan</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>
```

## Content
- [Starcoin Networks](#mainnet-and-testnets)
- [API examples](#api-examples)
  - [Block](#block-api)
  - [Transactions](#transaction-api)
- [Version History](#version-history)

## Mainnet and Testnets
API support Starcoin: *[MAINNET](https://explorer.starcoin.org)

## API Examples

You can read about all API methods on [StarcoinScan](https://explorer.starcoin.org)

*Library support all available StarcoinScan API.*

Below are examples for each API category.

### Block Api
** Get block Related Interfaces **
```
GET http://localhost:8500/v1/block/
```


### Transaction Api
**Get Transaction Related Interfaces**
```
GET http://localhost:8500/v1/transaction/
```

## Version History

## License
Starcoinscan-api is licensed as [Apache 2.0](./LICENSE).
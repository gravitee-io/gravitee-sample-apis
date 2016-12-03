# WhatTimeIsIt API


## How to run
`$ java -jar gravitee-whattimeisit-api-VERSION.jar <port>`

`port ` is optional. Default `8080`

## How to use

`GET` Return current timestamp and formated date in the JSON body response.
  
## Samples
```
$ curl -v "http://localhost:8080/"

< HTTP/1.1 200 OK
< content-type: application/json
< Content-Length: 71
<
{
  "timestamp" : 1480763464699,
  "date" : "03/12/2016 12:11:04.699"
}
```

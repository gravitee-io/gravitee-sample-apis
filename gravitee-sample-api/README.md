# WhoAmI API


## How to run
`$ java -jar gravitee-whoami-api-VERSION.jar -Dhttp.port=<port> -Dgrpc.port=<port>`

`http.port ` is optional. Default `8080`
`http.port ` is optional. Default `50051`

## How to use

`GET` Return server port, OS and network informations in the body response.
* query params:
  * `statusCode`: the return status code. Default `200`
  * `latency`: the latency in ms you want to add. Default `1`
  
## Samples
```
$ curl -v "http://localhost:8080?statusCode=201&latency=1000"

< HTTP/1.1 201 Created
< content-type: application/json
< Content-Length: 573
<
{
  "port": 8080,
  "os": {
    "arch": "x86_64",
    "name": "Mac OS X",
    "version": "10.11.6",
    "availableProcessors": 8,
    "systemLoadAverage": 2.36474609375
  },
  "network": [
    {
      "name": "awdl0",
      "displayName": "awdl0",
      "InetAddress": [
        "/fe80:0:0:0:2803:a4ff:fe25:bd5%awdl0"
      ]
    },
    {
      "name": "en0",
      "displayName": "en0",
      "InetAddress": [
        "/fe80:0:0:0:f65c:89ff:feb4:363%en0",
        "/192.168.0.12"
      ]
    },
    {
      "name": "lo0",
      "displayName": "lo0",
      "InetAddress": [
        "/fe80:0:0:0:0:0:0:1%lo0",
        "/0:0:0:0:0:0:0:1",
        "/127.0.0.1"
      ]
    }
  ]
}
```

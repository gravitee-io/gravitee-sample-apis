# Echo API


## How to use

`GET` Return headers as response content in JSON and que parameters list.
* query params:
  * `statusCode`: the return status code. Default `200`
  * `statusMessage`: the return status message. Default `OK`
  
  
`POST` Return posted headers in response headers, same thing, with body 
* query params:
  * `statusCode`: the return status code. Default `200`
  * `statusMessage`: the return status message. Default `OK`

## Samples
```
$ curl -v "http://localhost:8080/?q=foo"

< HTTP/1.1 200 OK
< content-type: application/json
< Content-Length: 109
<
{
  "headers" : {
    "Host" : "localhost:8080",
    "User-Agent" : "curl/7.43.0",
    "Accept" : "*/*"
  },
  "query_params" : {
    "q": "foo"
  }
}
```

```
$ curl -v -H "my-key: my value" "http://localhost:8080?statusCode=201"

< HTTP/1.1 201 Created
< content-type: application/json
< Content-Length: 136
<
{
  "headers" : {
    "Host" : "localhost:8080",
    "User-Agent" : "curl/7.43.0",
    "Accept" : "*/*",
    "my-key" : "my value"
  }
}
```

```
$ curl -v -X POST -H "my-key: my value" -H "Content-Type: application/json" -d '{ "json-body": "json value" }' "http://localhost:8080?statusCode=201&statusMessage=Creation OK"
   
< HTTP/1.1 201 Creation OK
< User-Agent: curl/7.43.0
< Accept: */*
< my-key: my value
< Content-Type: application/json
< Content-Length: 29
<
{ "json-body": "json value" }
```
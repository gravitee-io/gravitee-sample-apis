# AI API

Fake LLM API that returns provider-shaped responses, useful for testing Gravitee AI gateway policies without hitting a real provider.

## How to run
`$ java -jar gravitee-sample-api-VERSION.jar <port>`

`port` is optional. Default `8080`

## How to use

### `GET /ai/llm` / `POST /ai/llm`

Returns a fake LLM completion response shaped after the target provider's format.

* query params:
  * `provider`: the provider format to simulate. `anthropic` (default) or `openai`
  * `model`: override the model name in the response. Default: `claude-opus-4-5` (Anthropic) or `gpt-4o` (OpenAI)
  * `latency`: artificial response delay in milliseconds. Default `0`
  * `statusCode`: the return status code. Default `200`

## Samples

### Anthropic (default)
```
$ curl "http://localhost:8080/ai/llm"

{
  "id" : "msg_982983dd0e15472ca0cecb01",
  "type" : "message",
  "role" : "assistant",
  "content" : [ {
    "type" : "text",
    "text" : "This is a simulated LLM response from the Gravitee sample API. Use it to test your AI gateway configuration without hitting a real provider."
  } ],
  "model" : "claude-opus-4-5",
  "stop_reason" : "end_turn",
  "stop_sequence" : null,
  "usage" : {
    "input_tokens" : 25,
    "output_tokens" : 42
  }
}
```

### OpenAI
```
$ curl "http://localhost:8080/ai/llm?provider=openai"

{
  "id" : "chatcmpl-ba6365a085d34b60a3f6e5f0",
  "object" : "chat.completion",
  "created" : 1780554629,
  "model" : "gpt-4o",
  "choices" : [ {
    "index" : 0,
    "message" : {
      "role" : "assistant",
      "content" : "This is a simulated LLM response from the Gravitee sample API. Use it to test your AI gateway configuration without hitting a real provider."
    },
    "finish_reason" : "stop"
  } ],
  "usage" : {
    "prompt_tokens" : 25,
    "completion_tokens" : 42,
    "total_tokens" : 67
  }
}
```

### Custom model + simulated latency
```
$ curl "http://localhost:8080/ai/llm?provider=openai&model=gpt-4o-mini&latency=500"
```

### Simulate a rate limit error
```
$ curl "http://localhost:8080/ai/llm?statusCode=429"
```

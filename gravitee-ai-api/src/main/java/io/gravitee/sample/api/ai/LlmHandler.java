/**
 * Copyright (C) 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.sample.api.ai;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import java.time.Instant;
import java.util.UUID;

/**
 * Fake LLM API handler that returns provider-shaped responses (Anthropic, OpenAI).
 *
 * Query params:
 *   provider   = anthropic (default) | openai
 *   model      = override model name
 *   latency    = artificial latency in ms (default 0)
 *   statusCode = override HTTP status code (default 200)
 */
public class LlmHandler implements Handler<RoutingContext> {

    private static final String PROVIDER_ANTHROPIC = "anthropic";
    private static final String PROVIDER_OPENAI = "openai";

    private static final String DEFAULT_ANTHROPIC_MODEL = "claude-opus-4-5";
    private static final String DEFAULT_OPENAI_MODEL = "gpt-4o";

    private static final String FAKE_RESPONSE_TEXT =
        "This is a simulated LLM response from the Gravitee sample API. " +
        "Use it to test your AI gateway configuration without hitting a real provider.";

    @Override
    public void handle(final RoutingContext routingContext) {
        final HttpServerRequest request = routingContext.request();

        String provider = request.getParam("provider");
        if (provider == null || provider.isBlank()) {
            provider = PROVIDER_ANTHROPIC;
        }

        int statusCode = 200;
        if (request.getParam("statusCode") != null) {
            try {
                statusCode = Integer.parseInt(request.getParam("statusCode"));
            } catch (NumberFormatException ignored) {}
        }

        long latency = 0;
        if (request.getParam("latency") != null) {
            try {
                latency = Long.parseLong(request.getParam("latency"));
            } catch (NumberFormatException ignored) {}
        }

        final String resolvedProvider = provider;
        final int resolvedStatusCode = statusCode;
        final long resolvedLatency = latency;

        request.endHandler(v -> {
            if (resolvedLatency > 0) {
                routingContext
                    .vertx()
                    .setTimer(resolvedLatency, id -> sendResponse(routingContext, request, resolvedProvider, resolvedStatusCode));
            } else {
                sendResponse(routingContext, request, resolvedProvider, resolvedStatusCode);
            }
        });
    }

    private void sendResponse(RoutingContext ctx, HttpServerRequest request, String provider, int statusCode) {
        HttpServerResponse response = ctx.response();
        JsonObject body;

        switch (provider.toLowerCase()) {
            case PROVIDER_OPENAI:
                body = buildOpenAiResponse(request);
                break;
            case PROVIDER_ANTHROPIC:
            default:
                body = buildAnthropicResponse(request);
                break;
        }

        response.putHeader("content-type", "application/json").setStatusCode(statusCode).end(body.encodePrettily());
    }

    private JsonObject buildAnthropicResponse(HttpServerRequest request) {
        String model = request.getParam("model");
        if (model == null || model.isBlank()) {
            model = DEFAULT_ANTHROPIC_MODEL;
        }

        JsonObject contentBlock = new JsonObject().put("type", "text").put("text", FAKE_RESPONSE_TEXT);

        JsonObject usage = new JsonObject().put("input_tokens", 25).put("output_tokens", 42);

        return new JsonObject()
            .put("id", "msg_" + shortUuid())
            .put("type", "message")
            .put("role", "assistant")
            .put("content", new JsonArray().add(contentBlock))
            .put("model", model)
            .put("stop_reason", "end_turn")
            .put("stop_sequence", (Object) null)
            .put("usage", usage);
    }

    private JsonObject buildOpenAiResponse(HttpServerRequest request) {
        String model = request.getParam("model");
        if (model == null || model.isBlank()) {
            model = DEFAULT_OPENAI_MODEL;
        }

        JsonObject message = new JsonObject().put("role", "assistant").put("content", FAKE_RESPONSE_TEXT);

        JsonObject choice = new JsonObject().put("index", 0).put("message", message).put("finish_reason", "stop");

        JsonObject usage = new JsonObject().put("prompt_tokens", 25).put("completion_tokens", 42).put("total_tokens", 67);

        return new JsonObject()
            .put("id", "chatcmpl-" + shortUuid())
            .put("object", "chat.completion")
            .put("created", Instant.now().getEpochSecond())
            .put("model", model)
            .put("choices", new JsonArray().add(choice))
            .put("usage", usage);
    }

    private String shortUuid() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 24);
    }
}

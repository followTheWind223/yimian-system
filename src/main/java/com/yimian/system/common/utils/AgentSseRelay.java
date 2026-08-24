package com.yimian.system.common.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class AgentSseRelay {

    private AgentSseRelay() {
    }

    public static void writeEvent(OutputStream output,
                                  ObjectMapper objectMapper,
                                  String event,
                                  Object data) throws IOException {
        writeLine(output, "event: " + event);
        writeLine(output, "data: " + objectMapper.writeValueAsString(data));
        writeLine(output, "");
        output.flush();
    }

    public static void relay(InputStream input,
                             OutputStream output,
                             ObjectMapper objectMapper,
                             String publicSessionId) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(input, StandardCharsets.UTF_8))) {
            List<String> eventLines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    writeEventLines(output, objectMapper, eventLines, publicSessionId);
                    eventLines.clear();
                    output.flush();
                } else {
                    eventLines.add(line);
                }
            }
            if (!eventLines.isEmpty()) {
                writeEventLines(output, objectMapper, eventLines, publicSessionId);
                output.flush();
            }
        }
    }

    private static void writeEventLines(OutputStream output,
                                        ObjectMapper objectMapper,
                                        List<String> lines,
                                        String publicSessionId) throws IOException {
        if (lines.isEmpty()) {
            return;
        }
        String event = lines.stream()
                .filter(line -> line.startsWith("event:"))
                .map(line -> line.substring("event:".length()).trim())
                .findFirst()
                .orElse("");

        for (String line : lines) {
            if (line.startsWith("data:") && "done".equals(event)) {
                writeLine(output, "data: " + rewriteDoneData(
                        objectMapper,
                        line.substring("data:".length()).trim(),
                        publicSessionId
                ));
            } else if (line.startsWith("data:") && "error".equals(event)) {
                writeLine(output, "data: " + objectMapper.writeValueAsString(
                        Map.of("error", "Agent 服务暂时不可用")
                ));
            } else {
                writeLine(output, line);
            }
        }
        writeLine(output, "");
    }

    private static String rewriteDoneData(ObjectMapper objectMapper,
                                          String data,
                                          String publicSessionId) throws IOException {
        JsonNode node = objectMapper.readTree(data);
        if (node instanceof ObjectNode objectNode) {
            objectNode.put("session_id", publicSessionId);
            return objectMapper.writeValueAsString(objectNode);
        }
        return data;
    }

    private static void writeLine(OutputStream output, String line) throws IOException {
        output.write((line + "\n").getBytes(StandardCharsets.UTF_8));
    }
}

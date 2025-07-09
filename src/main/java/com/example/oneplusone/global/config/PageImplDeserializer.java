package com.example.oneplusone.global.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.io.IOException;
import java.util.List;

public class PageImplDeserializer extends JsonDeserializer<PageImpl<?>> {

    @Override
    public PageImpl<?> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        List<?> content = jsonParser.getCodec().treeToValue(node.get("content"), List.class);
        int page = node.get("number").asInt();
        int size = node.get("size").asInt();
        long total = node.get("totalElements").asLong();
        return new PageImpl<>(content, PageRequest.of(page, size), total);
    }
}
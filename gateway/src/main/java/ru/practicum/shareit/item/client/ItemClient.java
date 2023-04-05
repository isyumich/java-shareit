package ru.practicum.shareit.item.client;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.RequestBodyCommentDto;
import ru.practicum.shareit.item.dto.RequestBodyItemDto;

import java.util.Map;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemClient extends BaseClient {
    static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl,
                      RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build()
        );
    }

    public ResponseEntity<Object> addNewItem(RequestBodyItemDto requestBodyItemDto, Long userId) {
        return post("", userId, requestBodyItemDto);
    }

    public ResponseEntity<Object> addNewComment(RequestBodyCommentDto requestBodyCommentDto, Long userId, long itemId) {
        return post("/" + itemId + "/comment", userId, requestBodyCommentDto);
    }

    public ResponseEntity<Object> updateItem(long itemId, RequestBodyItemDto requestBodyItemDto, Long userId) {
        return patch("/" + itemId, userId, requestBodyItemDto);
    }

    public ResponseEntity<Object> getAllItems(Long userId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getItemById(long itemId, Long userId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getItemByNameOrDescription(String text, Long userId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size
        );
        return get("/search?text={text}&from={from}&size={size}", userId, parameters);
    }
}
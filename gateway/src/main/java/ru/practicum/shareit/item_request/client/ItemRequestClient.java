package ru.practicum.shareit.item_request.client;

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
import ru.practicum.shareit.item_request.dto.RequestBodyItemRequestDto;

import java.util.Map;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestClient extends BaseClient {
    static final String API_PREFIX = "/requests";

    @Autowired
    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build()
        );
    }

    public ResponseEntity<Object> addNewItemRequest(Long userId, RequestBodyItemRequestDto requestBodyItemRequestDto) {
        String path = "";
        return post(path, userId, requestBodyItemRequestDto);

    }

    public ResponseEntity<Object> getOwnItemRequests(Long userId) {
        String path = "";
        return get(path, userId);
    }

    public ResponseEntity<Object> getAllItemRequests(Integer from, Integer size, Long userId) {
        String path = "/all/?from={from}&size={size}";
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get(path, userId, parameters);
    }

    public ResponseEntity<Object> getRequestById(Long userId, long requestId) {
        String path = String.format("%s%d", "/", requestId);
        return get(path, userId);
    }


}
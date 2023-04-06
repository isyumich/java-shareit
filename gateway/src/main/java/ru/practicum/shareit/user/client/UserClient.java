package ru.practicum.shareit.user.client;

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
import ru.practicum.shareit.user.dto.RequestBodyUserDto;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserClient extends BaseClient {
    static final String API_PREFIX = "/users";

    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build()
        );
    }

    public ResponseEntity<Object> addNewUser(RequestBodyUserDto requestBodyUserDto) {
        String path = "";
        return post(path, requestBodyUserDto);
    }

    public ResponseEntity<Object> updateUser(RequestBodyUserDto requestBodyUserDto, Long userId) {
        String path = String.format("%s%d", "/", userId);
        return patch(path, requestBodyUserDto);
    }

    public ResponseEntity<Object> getUserById(Long userId) {
        String path = String.format("%s%d", "/", userId);
        return get(path);
    }

    public ResponseEntity<Object> getAllUsers() {
        String path = "";
        return get(path);
    }

    public ResponseEntity<Object> deleteUser(Long userId) {
        String path = String.format("%s%d", "/", userId);
        return delete(path);
    }
}
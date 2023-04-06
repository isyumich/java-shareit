package ru.practicum.shareit.booking.client;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.RequestBodyBookingDto;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.exception.IllegalArgumentException;

import java.util.List;
import java.util.Map;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingClient extends BaseClient {
    static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl,
                         RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build()
        );
    }

    public ResponseEntity<Object> addNewBooking(RequestBodyBookingDto requestBodyBookingDto, Long userId) {
        String path = "";
        return post(path, userId, requestBodyBookingDto);
    }

    public ResponseEntity<Object> approveOrRejectBooking(Long userId, long bookingId, Boolean approved) {
        String path = String.format("%s%d%s", "/", bookingId, "?approved={approved}");
        Map<String, Object> parameters = Map.of("approved", approved);
        return patch(path, userId, parameters, null);
    }

    public ResponseEntity<Object> getBookingById(Long userId, long bookingId) {
        String path = String.format("%s%d", "/", bookingId);
        return get(path, userId);
    }

    public ResponseEntity<Object> getBookingCurrentUser(Long userId, String state, Integer from, Integer size) {
        String path = "?state={state}&from={from}&size={size}";
        BookingState bookingState = getBookingStateValue(state);
        Map<String, Object> parameters = Map.of(
                "state", bookingState,
                "from", from,
                "size", size
        );
        return get(path, userId, parameters);
    }

    public ResponseEntity<Object> getBookingForItemsCurrentUser(Long userId, String state, Integer from, Integer size) {
        String path = "/owner?state={state}&from={from}&size={size}";
        BookingState bookingState = getBookingStateValue(state);
        Map<String, Object> parameters = Map.of(
                "state", bookingState,
                "from", from,
                "size", size
        );
        return get(path, userId, parameters);
    }

    private BookingState getBookingStateValue(String state) {
        List<BookingState> states = List.of(BookingState.values());
        for (BookingState bookingState : states) {
            if (state.equals(bookingState.toString())) {
                return bookingState;
            }
        }
        throw new IllegalArgumentException("Поле State имеет недопустимое значение");
    }
}
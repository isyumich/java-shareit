package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.IsAlreadyExistsException;
import ru.practicum.shareit.exception.ValidationException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@AutoConfigureMockMvc
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingControllerTest {
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mockMvc;

    @MockBean
    @Qualifier("BookingServiceImpl")
    BookingService bookingService;

    BookingDto bookingDtoCorrect;
    BookingDto bookingDtoEmptyEnd;
    BookingDto bookingDtoIsAlreadyApproved;
    final String pathBookings = "/bookings";
    final String pathBookingId = "/{bookingId}";
    final String headerUserValue = "X-Sharer-User-Id";


    @BeforeEach
    void beforeEach() {
        bookingDtoCorrect = BookingDto.builder().id(1L).start(LocalDateTime.now())
                .end(LocalDateTime.now().plus(10, ChronoUnit.DAYS)).status(BookingStatus.WAITING).build();
        bookingDtoEmptyEnd = BookingDto.builder().id(2L).start(LocalDateTime.now())
                .end(null).status(BookingStatus.WAITING).build();
        bookingDtoIsAlreadyApproved = BookingDto.builder().id(3L).start(LocalDateTime.now())
                .end(LocalDateTime.now().plus(10, ChronoUnit.DAYS)).status(BookingStatus.APPROVED).build();
    }

    @SneakyThrows
    @Test
    void addBookingTest_whenBookingCorrect_thenReturnOK() {
        when(bookingService.addNewBooking(any(), anyLong())).thenReturn(bookingDtoCorrect);

        String result = mockMvc.perform(post(pathBookings)
                        .content(objectMapper.writeValueAsString(bookingDtoCorrect))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(headerUserValue, 1))
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(bookingService).addNewBooking(any(), anyLong());
        assertEquals(objectMapper.writeValueAsString(bookingDtoCorrect), result);
    }

    @SneakyThrows
    @Test
    void addBookingTest_whenBookingEndEmpty_thenThrow() {
        when(bookingService.addNewBooking(any(), anyLong())).thenThrow(new ValidationException("Start date and end date must not be null"));

        String result = mockMvc.perform(post(pathBookings)
                        .content(objectMapper.writeValueAsString(bookingDtoEmptyEnd))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(headerUserValue, 1))
                .andExpect(status().is4xxClientError())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(bookingService).addNewBooking(any(), anyLong());
        assertEquals("{\"error\":\"Start date and end date must not be null\"}", result);
    }

    @SneakyThrows
    @Test
    void approveBookingTest_whenBookingCorrect_thenReturnOK() {
        long bookingId = 1L;
        when(bookingService.approveOrRejectBooking(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingDtoCorrect);

        String result = mockMvc.perform(patch(pathBookings + pathBookingId, bookingId)
                        .param("approved", "true")
                        .header(headerUserValue, 1)
                        .content(objectMapper.writeValueAsString(bookingDtoCorrect))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(bookingService).approveOrRejectBooking(anyLong(), anyLong(), anyBoolean());
        assertEquals(objectMapper.writeValueAsString(bookingDtoCorrect), result);
    }

    @SneakyThrows
    @Test
    void approveBookingTest_whenBookingIsAlreadyApproved_thenThrow() {
        long bookingId = 3L;
        when(bookingService.approveOrRejectBooking(anyLong(), anyLong(), anyBoolean()))
                .thenThrow(new IsAlreadyExistsException("This booking is already approved"));

        String result = mockMvc.perform(patch(pathBookings + pathBookingId, bookingId)
                        .param("approved", "true")
                        .header(headerUserValue, 1)
                        .content(objectMapper.writeValueAsString(bookingDtoIsAlreadyApproved))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(bookingService).approveOrRejectBooking(anyLong(), anyLong(), anyBoolean());
        assertEquals("{\"error\":\"This booking is already approved\"}", result);
    }

    @SneakyThrows
    @Test
    void getBookingsForCurrentUserTest() {
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("from", "0");
        requestParams.add("size", "10");
        requestParams.add("state", "ALL");

        when(bookingService.getBookingCurrentUser(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(List.of(bookingDtoCorrect));

        mockMvc.perform(get(pathBookings)
                        .header(headerUserValue, 1)
                        .params(requestParams))
                .andExpect(status().is2xxSuccessful());

        verify(bookingService).getBookingCurrentUser(anyLong(), anyString(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void getBookingsForItemsCurrentUserTest() {
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("from", "0");
        requestParams.add("size", "10");
        requestParams.add("state", "ALL");

        when(bookingService.getBookingForItemsCurrentUser(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(List.of(bookingDtoCorrect));

        mockMvc.perform(get(pathBookings + "/owner")
                        .header(headerUserValue, 1)
                        .params(requestParams))
                .andExpect(status().is2xxSuccessful());

        verify(bookingService).getBookingForItemsCurrentUser(anyLong(), anyString(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void getBookingByIdTest() {
        when(bookingService.getBookingById(anyLong(), anyLong())).thenReturn(bookingDtoCorrect);

        mockMvc.perform(get(pathBookings + pathBookingId, 1).header(headerUserValue, 1)).andExpect(status().is2xxSuccessful());

        verify(bookingService).getBookingById(anyLong(), anyLong());
    }
}

package ru.practicum.shareit.item_request.controller;

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
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item_request.dto.ItemRequestDto;
import ru.practicum.shareit.item_request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
@AutoConfigureMockMvc
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestControllerTest {
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mockMvc;

    @MockBean
    @Qualifier("ItemRequestServiceImpl")
    ItemRequestService itemRequestService;

    ItemRequestDto itemRequestDtoCorrect;
    ItemRequestDto itemRequestDtoEmptyDesc;
    final String pathRequests = "/requests";
    final String headerUserValue = "X-Sharer-User-Id";

    @BeforeEach
    void beforeEach() {
        itemRequestDtoCorrect = ItemRequestDto.builder().id(1L)
                .created(LocalDateTime.now()).description("itemRequestDesc1").build();
        itemRequestDtoEmptyDesc = ItemRequestDto.builder().id(2L)
                .created(LocalDateTime.now()).description("").build();
    }

    @SneakyThrows
    @Test
    void addItemRequestTest_whenItemRequestCorrect_thenReturnOK() {
        when(itemRequestService.addNewItemRequest(anyLong(), any())).thenReturn(itemRequestDtoCorrect);

        String result = mockMvc.perform(post(pathRequests)
                        .content(objectMapper.writeValueAsString(itemRequestDtoCorrect))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(headerUserValue, 1))
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(itemRequestService).addNewItemRequest(anyLong(), any());
        assertEquals(objectMapper.writeValueAsString(itemRequestDtoCorrect), result);
    }


    @SneakyThrows
    @Test
    void addItemRequestTest_whenItemRequestEmptyDesc_thenThrow() {
        when(itemRequestService.addNewItemRequest(anyLong(), any())).thenThrow(new ValidationException("The itemRequest's description is missing"));

        String result = mockMvc.perform(post(pathRequests)
                        .content(objectMapper.writeValueAsString(itemRequestDtoEmptyDesc))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(headerUserValue, 1))
                .andExpect(status().is4xxClientError())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemRequestService).addNewItemRequest(anyLong(), any());
        assertEquals("{\"error\":\"The itemRequest's description is missing\"}", result);
    }

    @SneakyThrows
    @Test
    void getAllItemRequestsTest() {
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("from", "0");
        requestParams.add("size", "10");
        when(itemRequestService.getAllItemRequests(anyInt(), anyInt(), anyLong())).thenReturn(List.of(itemRequestDtoCorrect));

        mockMvc.perform(get(pathRequests + "/all").params(requestParams).header(headerUserValue, 1)).andExpect(status().is2xxSuccessful());

        verify(itemRequestService).getAllItemRequests(anyInt(), anyInt(), anyLong());
    }

    @SneakyThrows
    @Test
    void getOwnItemRequestsTest() {
        when(itemRequestService.getOwnItemRequests(anyLong())).thenReturn(List.of(itemRequestDtoCorrect));

        mockMvc.perform(get(pathRequests).header(headerUserValue, 1)).andExpect(status().is2xxSuccessful());

        verify(itemRequestService).getOwnItemRequests(anyLong());
    }

    @SneakyThrows
    @Test
    void getItemRequestByIdTest() {
        when(itemRequestService.getRequestById(anyLong(), anyLong())).thenReturn(itemRequestDtoCorrect);

        mockMvc.perform(get(pathRequests + "/{requestId}", 1).header(headerUserValue, 1)).andExpect(status().is2xxSuccessful());

        verify(itemRequestService).getRequestById(anyLong(), anyLong());
    }
}

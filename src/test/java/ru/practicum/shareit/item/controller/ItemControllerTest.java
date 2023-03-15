package ru.practicum.shareit.item.controller;

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
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
@AutoConfigureMockMvc
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemControllerTest {
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mockMvc;

    @MockBean
    @Qualifier("ItemServiceImpl")
    ItemService itemService;

    ItemDto itemDtoCorrect;
    ItemDto itemDtoEmptyName;
    CommentDto commentDtoCorrect;
    CommentDto commentDtoEmptyText;
    final String pathItems = "/items";
    final String pathItemId = "/{itemId}";
    final String pathComment = "/comment";
    final String headerUserValue = "X-Sharer-User-Id";


    @BeforeEach
    void beforeEach() {
        itemDtoCorrect = ItemDto.builder().id(1L).name("itemCorrect").description("itemCorrectDesc").available(true).build();
        itemDtoEmptyName = ItemDto.builder().id(2L).name("").description("itemEmptyNameDesc").available(true).build();
        commentDtoCorrect = CommentDto.builder().id(1L).text("commentCorrect").build();
        commentDtoEmptyText = CommentDto.builder().id(2L).text("").build();
    }

    @SneakyThrows
    @Test
    void addItemTest_whenItemCorrect_thenReturnOK() {
        when(itemService.addNewItem(any(), anyLong())).thenReturn(itemDtoCorrect);

        String result = mockMvc.perform(post(pathItems)
                        .content(objectMapper.writeValueAsString(itemDtoCorrect))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(headerUserValue, 1))
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(itemService).addNewItem(any(), anyLong());
        assertEquals(objectMapper.writeValueAsString(itemDtoCorrect), result);
    }

    @SneakyThrows
    @Test
    void addItemTest_whenBookingNameEmpty_thenThrow() {
        when(itemService.addNewItem(any(), anyLong())).thenThrow(new ValidationException("Имя не может быть пустым"));

        String result = mockMvc.perform(post(pathItems)
                        .content(objectMapper.writeValueAsString(itemDtoEmptyName))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(headerUserValue, 1))
                .andExpect(status().is4xxClientError())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService).addNewItem(any(), anyLong());
        assertEquals("{\"error\":\"Имя не может быть пустым\"}", result);
    }

    @SneakyThrows
    @Test
    void addCommentTest_whenCommentCorrect_thenReturnOK() {
        when(itemService.addNewComment(any(), anyLong(), anyLong())).thenReturn(commentDtoCorrect);

        String result = mockMvc.perform(post(pathItems + pathItemId + pathComment, 1)
                        .content(objectMapper.writeValueAsString(commentDtoCorrect))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(headerUserValue, 1))
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(itemService).addNewComment(any(), anyLong(), anyLong());
        assertEquals(objectMapper.writeValueAsString(commentDtoCorrect), result);
    }

    @SneakyThrows
    @Test
    void addCommentTest_whenCommentTextEmpty_thenThrow() {
        when(itemService.addNewComment(any(), anyLong(), anyLong())).thenThrow(new ValidationException("Текст комментария должен быть указан"));

        String result = mockMvc.perform(post(pathItems + pathItemId + pathComment, 1)
                        .content(objectMapper.writeValueAsString(commentDtoEmptyText))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(headerUserValue, 1))
                .andExpect(status().is4xxClientError())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService).addNewComment(any(), anyLong(), anyLong());
        assertEquals("{\"error\":\"Текст комментария должен быть указан\"}", result);
    }


    @SneakyThrows
    @Test
    void updateItemTest_whenItemCorrect_thenReturnOK() {
        long itemId = 1L;
        when(itemService.updateItem(anyLong(), any(), anyLong())).thenReturn(itemDtoCorrect);

        String result = mockMvc.perform(patch(pathItems + pathItemId, itemId)
                        .content(objectMapper.writeValueAsString(itemDtoCorrect))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(headerUserValue, 1))
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(itemService).updateItem(anyLong(), any(), anyLong());
        assertEquals(objectMapper.writeValueAsString(itemDtoCorrect), result);
    }

    @SneakyThrows
    @Test
    void getAllItemsTest() {
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("from", "0");
        requestParams.add("size", "10");

        when(itemService.getAllItems(anyLong(), anyInt(), anyInt())).thenReturn(List.of(itemDtoCorrect));

        mockMvc.perform(get(pathItems)
                        .header(headerUserValue, 1)
                        .params(requestParams))
                .andExpect(status().is2xxSuccessful());

        verify(itemService).getAllItems(anyLong(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void getItemByIdTest() {
        when(itemService.getItemById(anyLong(), anyLong())).thenReturn(itemDtoCorrect);

        mockMvc.perform(get(pathItems + pathItemId, 1).header(headerUserValue, 1)).andExpect(status().is2xxSuccessful());

        verify(itemService).getItemById(anyLong(), anyLong());
    }

    @SneakyThrows
    @Test
    void getAllItemsBySearchTest() {
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("from", "0");
        requestParams.add("size", "10");
        requestParams.add("text", "itemCorrectDesc");

        when(itemService.getItemByNameOrDescription(anyString(), anyLong(), anyInt(), anyInt())).thenReturn(List.of(itemDtoCorrect));

        mockMvc.perform(get(pathItems + "/search")
                        .header(headerUserValue, 1)
                        .params(requestParams))
                .andExpect(status().is2xxSuccessful());

        verify(itemService).getItemByNameOrDescription(anyString(), anyLong(), anyInt(), anyInt());
    }
}

package com.crud.tasks.trello.client;

import com.crud.tasks.domain.*;
import com.crud.tasks.trello.config.TrelloConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrelloClientTest {

    @InjectMocks
    private TrelloClient trelloClient;

    @Mock
    private RestTemplate restTemplate;
    @Mock
    private TrelloConfig trelloConfig;

    @BeforeEach
    public void mockInit() {
        when(trelloConfig.getTrelloApiEndpoint()).thenReturn("http://test.com");
        when(trelloConfig.getTrelloAppKey()).thenReturn("test");
        when(trelloConfig.getTrelloToken()).thenReturn("test");
    }

    @Test
    public void shouldFetchTrelloBoards() throws URISyntaxException {
        //Given
        when(trelloConfig.getTrelloApiUsername()).thenReturn("test");

        TrelloBoardDto[] trelloBoards = new TrelloBoardDto[1];
        trelloBoards[0] = new TrelloBoardDto("test_id", "Kodilla", new ArrayList<>());

        URI uri = new URI("http://test.com/members/test/boards?key=test&token=test&fields=name,id&lists=all");

        when(restTemplate.getForObject(uri, TrelloBoardDto[].class)).thenReturn(trelloBoards);

        //When
        List<TrelloBoardDto> fetchedTrelloBoard = trelloClient.getTrelloBoards();

        //Then
        assertEquals(1, fetchedTrelloBoard.size());
        assertEquals("test_id", fetchedTrelloBoard.get(0).getId());
        assertEquals("Kodilla", fetchedTrelloBoard.get(0).getName());
        assertEquals(new ArrayList<>(), fetchedTrelloBoard.get(0).getLists());
    }

    @Test
    public void shouldCreateCard() throws URISyntaxException {
        //Given
        TrelloCardDto trelloCardDto = new TrelloCardDto(
                "Test task",
                "Test Description",
                "top",
                "test_id"
        );

        URI uri = new URI("http://test.com/cards?key=test&token=test&name=Test%20task&desc=Test%20Description&pos=top&idList=test_id");

        TrelloDto trelloDto = new TrelloDto(0, 0);
        TrelloAttachmentDto trelloAttachmentDto = new TrelloAttachmentDto(trelloDto);
        TrelloBadgesDto trelloBadgesDto = new TrelloBadgesDto(
                1,
                trelloAttachmentDto
        );
        CreatedTrelloCard createdTrelloCard = new CreatedTrelloCard(
                "1",
                "Test task",
                "http://test.com",
                trelloBadgesDto
        );
        when(restTemplate.postForObject(uri, null, CreatedTrelloCard.class)).thenReturn(createdTrelloCard);

        //When
        CreatedTrelloCard newCard = trelloClient.createNewCard(trelloCardDto);

        //Then
        assertEquals("1", newCard.getId());
        assertEquals("Test task", newCard.getName());
        assertEquals("http://test.com", newCard.getShortUrl());
    }

    @Test
    public void shouldReturnEmptyList() throws URISyntaxException {
        //Given
        when(trelloConfig.getTrelloApiUsername()).thenReturn("test");

        URI uri = new URI("http://test.com/members/test/boards?key=test&token=test&fields=name,id&lists=all");

        when(restTemplate.getForObject(uri, TrelloBoardDto[].class)).thenReturn(null);

        //When
        List<TrelloBoardDto> fetchedTrelloBoard = trelloClient.getTrelloBoards();

        //Then
        assertEquals(0, fetchedTrelloBoard.size());
    }
}
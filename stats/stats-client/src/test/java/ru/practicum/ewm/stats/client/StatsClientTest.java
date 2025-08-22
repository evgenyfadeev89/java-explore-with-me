package ru.practicum.ewm.stats.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import ru.practicum.ewm.stats.dto.EndpointHitRequest;
import ru.practicum.ewm.stats.dto.EndpointHitResponse;
import ru.practicum.ewm.stats.dto.ViewStatsResponse;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@SpringBootTest(classes = {StatsClientConfig.class, StatsClient.class})
public class StatsClientTest {

    @Autowired
    private StatsClient statsClient;

    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    @BeforeEach
    public void setup() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    public void testSaveHit() {
        String jsonResponse = """
        {
          "id": 1,
          "app": "ewm-main-service",
          "uri": "/events/1",
          "ip": "127.0.0.1",
          "timestamp": "2025-08-22 20:00:00"
        }
        """;

        mockServer.expect(requestTo("http://stats-server:9090/hit")).andExpect(method(HttpMethod.POST)).andRespond(withStatus(HttpStatusCode.valueOf(201)).contentType(MediaType.APPLICATION_JSON).body(jsonResponse));

        EndpointHitRequest request = new EndpointHitRequest("ewm-main-service", "/events/1", "127.0.0.1", LocalDateTime.of(2025, 8, 22, 20, 0, 0));

        EndpointHitResponse response = statsClient.saveHit(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("ewm-main-service", response.getApp());

        mockServer.verify();
    }

    @Test
    public void testGetStats() {
        String jsonResponse = """
        [
          {
            "app": "ewm-main-service",
            "uri": "/events/1",
            "hits": 5
          }
        ]
        """;

        mockServer.expect(requestTo("http://stats-server:9090/stats?start=2025-08-22%2019:00:00&" + "end=2025-08-22%2021:00:00&unique=false&uris=/events/1")).andExpect(method(HttpMethod.GET)).andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        LocalDateTime start = LocalDateTime.of(2025, 8, 22, 19, 0, 0);
        LocalDateTime end = LocalDateTime.of(2025, 8, 22, 21, 0, 0);

        List<ViewStatsResponse> stats = statsClient.getStats(start, end, List.of("/events/1"), false);

        assertNotNull(stats);
        assertEquals(1, stats.size());
        assertEquals("ewm-main-service", stats.get(0).getApp());
        assertEquals("/events/1", stats.get(0).getUri());
        assertEquals(5L, stats.get(0).getHits());

        mockServer.verify();
    }
}


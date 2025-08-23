package ru.practicum.ewm.stats.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.ewm.stats.dto.EndpointHitRequest;
import ru.practicum.ewm.stats.dto.EndpointHitResponse;
import ru.practicum.ewm.stats.dto.ViewStatsResponse;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
@RequiredArgsConstructor
public class StatsClient {
    private final RestTemplate restTemplate;

    @Value("${stats-server.url:http://stats-server:9090}")
    private String statsServerUrl;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public EndpointHitResponse saveHit(EndpointHitRequest request) {
        String url = statsServerUrl + "/hit";
        ResponseEntity<EndpointHitResponse> response = restTemplate.postForEntity(url,
                request,
                EndpointHitResponse.class);

        return response.getBody();
    }

    public List<ViewStatsResponse> getStats(
            LocalDateTime start,
            LocalDateTime end,
            @Nullable List<String> uris,
            boolean unique
    ) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(statsServerUrl + "/stats")
                .queryParam("start", start.format(formatter))
                .queryParam("end", end.format(formatter))
                .queryParam("unique", unique);

        if (uris != null && !uris.isEmpty()) {
            for (String uri : uris) {
                builder.queryParam("uris", uri);
            }
        }

        String url = builder.build(false).toUriString();

        ResponseEntity<ViewStatsResponse[]> response = restTemplate.getForEntity(url, ViewStatsResponse[].class);
        ViewStatsResponse[] statsArr = response.getBody();
        return statsArr == null ? new ArrayList<>() : Arrays.asList(statsArr);
    }
}
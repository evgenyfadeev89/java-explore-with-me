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
                .queryParam("start", formatter.format(start))
                .queryParam("end", formatter.format(end))
                .queryParam("unique", unique);

        if (uris != null && !uris.isEmpty()) {
            for (String uri : uris) {
                builder.queryParam("uris", uri);
            }
        }

        ResponseEntity<ViewStatsResponse[]> response = restTemplate.getForEntity(builder.toUriString(), ViewStatsResponse[].class);
        ViewStatsResponse[] statsArr = response.getBody();
        return statsArr == null ? new ArrayList<>() : Arrays.asList(statsArr);
    }
}







//package ru.practicum.ewm.stats.client;
//
//import org.springframework.http.*;
//import org.springframework.web.client.RestTemplate;
//import ru.practicum.ewm.stats.dto.EndpointHitRequest;
//import ru.practicum.ewm.stats.dto.ViewStatsResponse;
//
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class StatsClient {
//    private final String serverUrl;
//    private final RestTemplate restTemplate;
//
//    public StatsClient(String serverUrl) {
//        this.serverUrl = serverUrl;
//        this.restTemplate = new RestTemplate();
//    }
//
//    public void saveHit(EndpointHitRequest hitRequest) {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        HttpEntity<EndpointHitRequest> requestEntity = new HttpEntity<>(hitRequest, headers);
//
//        restTemplate.exchange(
//                serverUrl + "/hit",
//                HttpMethod.POST,
//                requestEntity,
//                Void.class
//        );
//    }
//
//    public List<ViewStatsResponse> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//
//        Map<String, Object> parameters = new HashMap<>();
//        parameters.put("start", start.format(formatter));
//        parameters.put("end", end.format(formatter));
//
//        if (uris != null && !uris.isEmpty()) {
//            parameters.put("uris", String.join(",", uris));
//        }
//
//        if (unique != null) {
//            parameters.put("unique", unique);
//        }
//
//        ResponseEntity<ViewStatsResponse[]> response = restTemplate.getForEntity(
//                serverUrl + "/stats?start={start}&end={end}&uris={uris}&unique={unique}",
//                ViewStatsResponse[].class,
//                parameters
//        );
//
//        return Arrays.asList(response.getBody());
//    }
//}

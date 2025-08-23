package ru.practicum.ewm.stats.service;

import ru.practicum.ewm.stats.dto.EndpointHitRequest;
import ru.practicum.ewm.stats.dto.EndpointHitResponse;
import ru.practicum.ewm.stats.dto.ViewStatsResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    EndpointHitResponse createHit(EndpointHitRequest request);

    List<ViewStatsResponse> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
package ru.practicum.ewm.stats.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.dto.EndpointHitRequest;
import ru.practicum.ewm.stats.dto.EndpointHitResponse;
import ru.practicum.ewm.stats.dto.ViewStatsResponse;
import ru.practicum.ewm.stats.mapper.StatsMapper;
import ru.practicum.ewm.stats.model.EndpointHit;
import ru.practicum.ewm.stats.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;
    private final StatsMapper statsMapper;

    @Override
    public EndpointHitResponse createHit(EndpointHitRequest request) {
        EndpointHit hit = statsMapper.toEndpointHit(request);
        EndpointHit savedHit = statsRepository.save(hit);
        return statsMapper.toEndpointHitResponse(savedHit);
    }

    @Override
    public List<ViewStatsResponse> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        List<ViewStatsResponse> viewStats;

        if (Boolean.TRUE.equals(unique)) {
            viewStats = statsRepository.findUniqueViewStats(start, end, uris);
        } else {
            viewStats = statsRepository.findAllViewStats(start, end, uris);
        }

        return viewStats;
    }
}
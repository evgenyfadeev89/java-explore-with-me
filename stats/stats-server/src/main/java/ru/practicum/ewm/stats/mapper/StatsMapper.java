package ru.practicum.ewm.stats.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.ewm.stats.dto.EndpointHitRequest;
import ru.practicum.ewm.stats.dto.EndpointHitResponse;
import ru.practicum.ewm.stats.model.EndpointHit;

@Mapper(componentModel = "spring")
public interface StatsMapper {

    StatsMapper INSTANCE = Mappers.getMapper(StatsMapper.class);

    @Mapping(target = "id", ignore = true)
    EndpointHit toEndpointHit(EndpointHitRequest request);

    EndpointHitResponse toEndpointHitResponse(EndpointHit hit);
}
package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.ParticipationRequestDto;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.ForbiddenException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.ParticipationRequestMapper;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.ParticipationRequest;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.ParticipationRequestRepository;
import ru.practicum.ewm.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ParticipationRequestServiceImpl implements ParticipationRequestService {
    private final ParticipationRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final ParticipationRequestMapper requestMapper;

    @Override
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено"));

        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Запрос от владельца события");
        }

        if (!Event.EventState.PUBLISHED.equals(event.getState())) {
            throw new ConflictException("Событие не опубликовано");
        }

        boolean requestExists = requestRepository.existsByEventIdAndRequesterId(eventId, userId);
        if (requestExists) {
            throw new ConflictException("Ошибка: повторный запрос");
        }

        if (event.getParticipantLimit() != null && event.getParticipantLimit() > 0) {
            Integer confirmedCount = requestRepository.countByEventIdAndStatus(
                    eventId, ParticipationRequest.Status.CONFIRMED);
            if (confirmedCount >= event.getParticipantLimit()) {
                throw new ConflictException("Лимит на событие исчерпан");
            }
        }

        ParticipationRequest.Status initialStatus;

        if (event.getParticipantLimit() == null || event.getParticipantLimit() == 0) {
            initialStatus = ParticipationRequest.Status.CONFIRMED;
        } else if (Boolean.FALSE.equals(event.getRequestModeration())) {
            initialStatus = ParticipationRequest.Status.CONFIRMED;
        } else {
            initialStatus = ParticipationRequest.Status.PENDING;
        }

        ParticipationRequest request = ParticipationRequest.builder()
                .created(LocalDateTime.now())
                .event(event)
                .requester(requester)
                .status(initialStatus)
                .build();

        ParticipationRequest saved = requestRepository.save(request);
        return requestMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        return requestRepository.findByRequesterId(userId).stream()
                .map(requestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        ParticipationRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос не найден"));
        if (!request.getRequester().getId().equals(userId)) {
            throw new ForbiddenException("Нет доступа для отмены чужого запроса");
        }
        request.setStatus(ParticipationRequest.Status.CANCELED);
        ParticipationRequest updated = requestRepository.save(request);
        return requestMapper.toDto(updated);
    }
}


package ru.practicum.ewm.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.CommentDto;
import ru.practicum.ewm.dto.NewCommentDto;
import ru.practicum.ewm.dto.UpdateCommentDto;
import ru.practicum.ewm.exception.ConditionsNotMetException;
import ru.practicum.ewm.exception.ForbiddenException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.CommentMapper;
import ru.practicum.ewm.model.Comment;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.repository.CommentRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.UserRepository;
import ru.practicum.ewm.stats.client.StatsClient;
import ru.practicum.ewm.stats.dto.EndpointHitRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CommentMapper commentMapper;
    private final StatsClient statsClient;

    @Override
    public CommentDto addComment(Long userId, NewCommentDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Event event = eventRepository.findById(dto.getEventId())
                .orElseThrow(() -> new NotFoundException("Событие не найдено"));

        if (!Event.EventState.PUBLISHED.equals(event.getState())) {
            throw new ForbiddenException("Нельзя комментировать неопубликованное событие");
        }

        Comment comment = commentMapper.fromNewCommentDto(dto, user, event, LocalDateTime.now());

        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Override
    public CommentDto updateComment(Long userId, Long commentId, UpdateCommentDto dto) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий не найден"));

        if (!comment.getUser().getId().equals(userId)) {
            throw new ForbiddenException("Вы можете редактировать только свои комментарии");
        }

        if (dto.getText() != null && !dto.getText().isBlank()) {
            comment.setText(dto.getText());
        }

        Comment updatedComment = commentRepository.save(comment);
        return commentMapper.toDto(updatedComment);
    }

    @Override
    public void deleteComment(Long userId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий не найден"));

        if (!comment.getUser().getId().equals(userId)) {
            throw new ForbiddenException("Вы можете удалять только свои комментарии");
        }

        commentRepository.delete(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public CommentDto getComment(Long userId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий не найден"));

        if (!comment.getUser().getId().equals(userId)) {
            throw new ForbiddenException("Вы можете просматривать только свои комментарии");
        }

        return commentMapper.toDto(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getUserComments(Long userId, int from, int size) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь не найден");
        }

        Pageable pageable = PageRequest.of(from / size, size);
        List<Comment> comments = commentRepository.findByUserIdOrderByCreatedDesc(userId, pageable);

        return comments.stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> findCommentsAdmin(List<Long> users, List<Long> events,
                                              String rangeStart, String rangeEnd, int from, int size) {

        LocalDateTime start = null;
        LocalDateTime end = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        try {
            if (rangeStart != null && !rangeStart.isEmpty()) {
                start = LocalDateTime.parse(rangeStart, formatter);
            }
            if (rangeEnd != null && !rangeEnd.isEmpty()) {
                end = LocalDateTime.parse(rangeEnd, formatter);
            }
        } catch (Exception e) {
            throw new ConditionsNotMetException("Некорректный формат даты yyyy-MM-dd HH:mm:ss");
        }

        if (start != null && end != null && start.isAfter(end)) {
            throw new ConditionsNotMetException("Дата начала не может быть позже даты окончания");
        }

        Pageable pageable = PageRequest.of(from / size, size);
        List<Comment> comments = commentRepository.findCommentsWithFilters(users, events, start, end, pageable);

        return comments.stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CommentDto getCommentByIdAdmin(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий не найден"));

        return commentMapper.toDto(comment);
    }

    @Override
    public void deleteCommentAdmin(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий не найден"));

        commentRepository.delete(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> findCommentsPublic(Long eventId, List<Long> users, String rangeStart,
                                               String rangeEnd, int from, int size, HttpServletRequest request) {

        LocalDateTime start = null;
        LocalDateTime end = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        try {
            if (rangeStart != null && !rangeStart.isEmpty()) {
                start = LocalDateTime.parse(rangeStart, formatter);
            }
            if (rangeEnd != null && !rangeEnd.isEmpty()) {
                end = LocalDateTime.parse(rangeEnd, formatter);
            }
        } catch (Exception e) {
            throw new ConditionsNotMetException("Некорректный формат даты yyyy-MM-dd HH:mm:ss");
        }

        if (start != null && end != null && start.isAfter(end)) {
            throw new ConditionsNotMetException("Дата начала не может быть позже даты окончания");
        }

        EndpointHitRequest hitRequest = new EndpointHitRequest();
        hitRequest.setApp("ewm-service");
        hitRequest.setUri(request.getRequestURI());
        hitRequest.setIp(request.getRemoteAddr());
        hitRequest.setTimestamp(LocalDateTime.now());

        statsClient.saveHit(hitRequest);

        Pageable pageable = PageRequest.of(from / size, size);

        List<Long> publishedEventIds = null;
        if (eventId != null) {
            Event event = eventRepository.findById(eventId)
                    .orElseThrow(() -> new NotFoundException("Событие не найдено"));

            if (!Event.EventState.PUBLISHED.equals(event.getState())) {
                throw new NotFoundException("Событие не опубликовано");
            }

            publishedEventIds = List.of(eventId);
        }

        List<Comment> comments = commentRepository.findPublishedCommentsWithFilters(
                publishedEventIds, users, start, end, pageable);

        return comments.stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CommentDto getCommentByIdPublic(Long commentId, HttpServletRequest request) {

        EndpointHitRequest hitRequest = new EndpointHitRequest();
        hitRequest.setApp("ewm-service");
        hitRequest.setUri(request.getRequestURI());
        hitRequest.setIp(request.getRemoteAddr());
        hitRequest.setTimestamp(LocalDateTime.now());

        statsClient.saveHit(hitRequest);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий не найден"));

        if (!Event.EventState.PUBLISHED.equals(comment.getEvent().getState())) {
            throw new NotFoundException("Комментарий недоступен");
        }

        return commentMapper.toDto(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getEventCommentsPublic(Long eventId, int from, int size, HttpServletRequest request) {

        EndpointHitRequest hitRequest = new EndpointHitRequest();
        hitRequest.setApp("ewm-service");
        hitRequest.setUri(request.getRequestURI());
        hitRequest.setIp(request.getRemoteAddr());
        hitRequest.setTimestamp(LocalDateTime.now());

        statsClient.saveHit(hitRequest);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено"));

        if (!Event.EventState.PUBLISHED.equals(event.getState())) {
            throw new NotFoundException("Событие не опубликовано");
        }

        Pageable pageable = PageRequest.of(from / size, size);
        List<Comment> comments = commentRepository.findByEventIdOrderByCreatedDesc(eventId, pageable);

        return comments.stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
    }
}

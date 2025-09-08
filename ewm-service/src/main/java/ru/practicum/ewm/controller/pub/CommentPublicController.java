package ru.practicum.ewm.controller.pub;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.CommentDto;
import ru.practicum.ewm.service.CommentService;

import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
@Slf4j
public class CommentPublicController {

    private final CommentService commentService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<CommentDto>> getCommentsPublic(
            @RequestParam(required = false) Long eventId,
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) String rangeStart,
            @RequestParam(required = false) String rangeEnd,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request
    ) {
        log.info("Публичный запрос к комментариям с eventId={}, users={}, range=[{}, {}], from={}, size={}",
                eventId, users, rangeStart, rangeEnd, from, size);

        return ResponseEntity.ok(commentService.findCommentsPublic(eventId, users, rangeStart, rangeEnd, from, size, request));
    }

    @GetMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommentDto> getCommentPublic(@PathVariable Long commentId,
                                                       HttpServletRequest request) {
        log.info("Публичный запрос к комментарию с id={}", commentId);

        return ResponseEntity.ok(commentService.getCommentByIdPublic(commentId, request));
    }

    @GetMapping("/event/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<CommentDto>> getEventCommentsPublic(
            @PathVariable Long eventId,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request
    ) {
        log.info("Публичный запрос к комментариям события eventId={}, from={}, size={}", eventId, from, size);

        return ResponseEntity.ok(commentService.getEventCommentsPublic(eventId, from, size, request));
    }
}
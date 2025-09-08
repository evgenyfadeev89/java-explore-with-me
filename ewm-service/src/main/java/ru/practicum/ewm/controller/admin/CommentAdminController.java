package ru.practicum.ewm.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.CommentDto;
import ru.practicum.ewm.service.CommentService;

import java.util.List;

@RestController
@RequestMapping("/admin/comments")
@RequiredArgsConstructor
public class CommentAdminController {

    private final CommentService commentService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<CommentDto>> getCommentsAdmin(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<Long> events,
            @RequestParam(required = false) String rangeStart,
            @RequestParam(required = false) String rangeEnd,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(commentService.findCommentsAdmin(users, events, rangeStart, rangeEnd, from, size));
    }

    @GetMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CommentDto> getCommentAdmin(@PathVariable Long commentId) {
        return ResponseEntity.ok(commentService.getCommentByIdAdmin(commentId));
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentAdmin(@PathVariable Long commentId) {
        commentService.deleteCommentAdmin(commentId);
    }
}

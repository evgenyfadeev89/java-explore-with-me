package ru.practicum.ewm.controller.pub;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.EventShortDto;
import ru.practicum.ewm.service.EventService;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Slf4j
public class EventPublicController {
    private final EventService eventService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<EventShortDto>> getEvents(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) String rangeStart,
            @RequestParam(required = false) String rangeEnd,
            @RequestParam(defaultValue = "false") boolean onlyAvailable,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        return ResponseEntity.ok(eventService.getPublicEvents(
                text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable, sort, from, size, request));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<EventFullDto> getEvent(@PathVariable Long id, HttpServletRequest request) {
        log.info("client ip: {}", request.getRemoteAddr());
        log.info("endpoint path: {}", request.getRequestURI());
        return ResponseEntity.ok(eventService.getPublicEvent(id, request));
    }
}

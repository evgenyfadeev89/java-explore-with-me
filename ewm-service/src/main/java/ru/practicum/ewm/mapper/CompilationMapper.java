
package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.dto.CompilationDto;
import ru.practicum.ewm.dto.NewCompilationDto;
import ru.practicum.ewm.model.Compilation;

@Mapper(componentModel = "spring", uses = {EventMapper.class})
public interface CompilationMapper {

    @Mapping(source = "events", target = "events")
    CompilationDto toDto(Compilation compilation);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "events", ignore = true)
    Compilation fromNewDto(NewCompilationDto dto);
}


//    @Autowired
//    protected EventRepository eventRepository;
//
//    public abstract CompilationDto toDto(Compilation compilation);
//
//    @Mapping(target = "events", ignore = true)
//    public abstract Compilation fromNewDto(NewCompilationDto dto);
//
//    @AfterMapping
//    protected void afterMapping(@MappingTarget Compilation compilation, NewCompilationDto dto) {
//        if (dto.getEvents() != null && !dto.getEvents().isEmpty()) {
//            Set<Event> events = new HashSet<>(eventRepository.findAllById(dto.getEvents()));
//            compilation.setEvents(events);
//        }
//    }
//
//    public Set<Event> mapEventIdsToEvents(Set<Long> eventIds) {
//        if (eventIds == null || eventIds.isEmpty()) {
//            return Collections.emptySet();
//        }
//        return new HashSet<>(eventRepository.findAllById(eventIds));
//    }
//}

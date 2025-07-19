package ru.practicum.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.entity.ParticipationRequest;

import java.util.List;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {

    @EntityGraph(attributePaths = {"event", "requester"})
    List<ParticipationRequest> findAllByRequester_Id(long userId);

    @EntityGraph(attributePaths = {"event", "requester"})
    List<ParticipationRequest> findAllByEvent_Initiator_idAndEvent_Id(long userId, long eventId);

    @EntityGraph(attributePaths = {"event", "requester"})
    List<ParticipationRequest> findAllByIdIn(List<Long> requestIds);

    boolean existsByRequester_IdAndEvent_Id(long userId, long eventId);
}

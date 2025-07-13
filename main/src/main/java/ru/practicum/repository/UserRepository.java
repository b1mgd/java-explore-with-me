package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.model.entity.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u " +
            "WHERE u.id IN :ids " +
            "ORDER BY u.id " +
            "LIMIT :size " +
            "OFFSET :from "
    )
    List<User> findAllUsers(@Param("ids") List<Long> ids,
                            @Param("from") long from,
                            @Param("size") long size);

    @Query("SELECT u FROM User u " +
            "ORDER BY u.id " +
            "LIMIT :size " +
            "OFFSET :from ")
    List<User> findAllUsers(@Param("from") int from,
                            @Param("size") int size);
}

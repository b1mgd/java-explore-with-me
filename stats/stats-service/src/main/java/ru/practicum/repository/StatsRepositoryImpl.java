package ru.practicum.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Hit;
import ru.practicum.model.Stats;
import ru.practicum.model.rowMapper.StatsRowMapper;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class StatsRepositoryImpl implements StatsRepository {

    private final JdbcTemplate jdbcTemplate;

    private final StatsRowMapper statsRowMapper;

    /**
     * При тестировании в H2 требуется удалить "RETURNING id" для корректной работы
     */
    public Hit save(Hit hit) {
        String sql = """
                INSERT INTO hits (app, uri, ip, timestamp)
                VALUES (?, ?, ?, ?)
                RETURNING id
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, hit.getApp());
            ps.setString(2, hit.getUri());
            ps.setString(3, hit.getIp());
            ps.setObject(4, hit.getTimestamp());

            return ps;
        }, keyHolder);

        Long id = keyHolder.getKeyAs(Long.class);

        if (id != null) {
            hit.setId(id);
        }

        return hit;
    }

    public List<Stats> findAllStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        String countExpr = unique ? "DISTINCT ip" : "ip";
        String uriExpr = "";
        boolean uriFilter = uris != null && !uris.isEmpty();

        List<Object> params = new ArrayList<>();
        params.add(Timestamp.valueOf(start));
        params.add(Timestamp.valueOf(end));

        if (uriFilter) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < uris.size(); i++) {
                sb.append("?");
                if (i < uris.size() - 1) {
                    sb.append(", ");
                }
            }
            uriExpr = "AND uri IN (" + sb + ")";
            params.addAll(uris.stream().filter(Objects::nonNull).collect(Collectors.toList()));
        }

        String sql = """
                SELECT app, uri, COUNT(%s) AS hits
                FROM hits
                WHERE timestamp BETWEEN ? AND ?
                %s
                GROUP BY app, uri
                ORDER BY hits DESC
                """.formatted(countExpr, uriExpr);

        return jdbcTemplate.query(sql, statsRowMapper, params.toArray());
    }
}

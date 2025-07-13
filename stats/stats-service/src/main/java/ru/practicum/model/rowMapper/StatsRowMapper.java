package ru.practicum.model.rowMapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.practicum.model.Stats;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class StatsRowMapper implements RowMapper<Stats> {

    @Override
    public Stats mapRow(ResultSet resultSet, int rowNumber) throws SQLException {
        return new Stats(
                resultSet.getString("app"),
                resultSet.getString("uri"),
                resultSet.getLong("hits")
        );
    }
}

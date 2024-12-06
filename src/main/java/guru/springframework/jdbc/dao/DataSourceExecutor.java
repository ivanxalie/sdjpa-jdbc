package guru.springframework.jdbc.dao;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class DataSourceExecutor {
    private final DataSource dataSource;

    @SneakyThrows
    public <TYPE> TYPE select(String sql, Function<ResultSet, TYPE> function, Object... params) {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                for (int i = 0; i < params.length; i++) {
                    statement.setObject(i + 1, params[i]);
                }
                return function.apply(statement.executeQuery());
            }
        }
    }

    public Long insert(String tableName, Map<String, Object> params) {
        try (Connection connection = dataSource.getConnection()) {
            List<String> questionMarks = new ArrayList<>();
            for (int i = 0; i < params.size(); i++)
                questionMarks.add("?");
            String sql = "insert into " +
                    tableName +
                    "(" +
                    String.join(",", params.keySet()) +
                    ") values (" +
                    String.join(",", questionMarks) +
                    ")";
            try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ArrayList<Object> objects = new ArrayList<>(params.values());
                for (int i = 0; i < objects.size(); i++) {
                    statement.setObject(i + 1, objects.get(i));
                }
                statement.executeUpdate();
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getLong(1);
                    }
                }
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public Integer update(String tableName, Long id, Map<String, Object> params) {
        try (Connection connection = dataSource.getConnection()) {
            StringBuilder sql = new StringBuilder("update ").append(tableName).append(" set ");
            List<String> keys = new ArrayList<>(params.keySet());
            for (int i = 0; i < params.size(); i++) {
                sql.append(keys.get(i)).append(" = ?");
                if (i != params.size() - 1) sql.append(",");
            }
            sql.append(" where id = ?");
            try (PreparedStatement statement = connection.prepareStatement(sql.toString())) {
                ArrayList<Object> objects = new ArrayList<>(params.values());
                for (int i = 0; i < objects.size(); i++) {
                    statement.setObject(i + 1, objects.get(i));
                }
                statement.setLong(params.size() + 1, id);
                return statement.executeUpdate();
            }
        } catch (Exception e) {
            return null;
        }
    }

    public <TYPE> Function<ResultSet, TYPE> idFunction(Mapper<TYPE> mapper) {
        return resultSet -> getSingleFromResultSet(resultSet, mapper);
    }

    private <TYPE> TYPE getSingleFromResultSet(ResultSet resultSet, Mapper<TYPE> mapper) {
        try {
            if (resultSet.next()) {
                try {
                    return mapper.map(resultSet);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public void delete(String tableName, Long id) {
        try (Connection connection = dataSource.getConnection()) {
            String sql = String.format("delete from %s where id = ?", tableName);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setLong(1, id);
                statement.executeUpdate();
            }
        } catch (Exception e) {
        }
    }

    interface Mapper<TO> {
        TO map(ResultSet resultSet) throws SQLException;
    }
}

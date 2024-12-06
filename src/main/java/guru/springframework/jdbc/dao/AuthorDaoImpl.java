package guru.springframework.jdbc.dao;

import guru.springframework.jdbc.domain.Author;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class AuthorDaoImpl implements AuthorDao {
    private final DataSource dataSource;

    @Override
    public Author getById(Long id) {
        return select(
                "select id, first_name, last_name from author where id = ?",
                idFunction(singleAuthorMapper()),
                id
        );

    }

    @Override
    public Author getByName(String firstName, String lastName) {
        return select(
                "select id, first_name, last_name from author where first_name = ? and last_name = ?",
                idFunction(singleAuthorMapper()),
                firstName,
                lastName
        );
    }

    @SneakyThrows
    private <TYPE> TYPE select(String sql, Function<ResultSet, TYPE> function, Object... params) {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                for (int i = 0; i < params.length; i++) {
                    statement.setObject(i + 1, params[i]);
                }
                return function.apply(statement.executeQuery());
            }
        }
    }

    private <TYPE> Function<ResultSet, TYPE> idFunction(Mapper<TYPE> mapper) {
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

    private Mapper<Author> singleAuthorMapper() {
        return resultSet -> Author.builder()
                .id(resultSet.getLong(1))
                .firstName(resultSet.getString(2))
                .lastName(resultSet.getString(3))
                .build();
    }

    interface Mapper<TO> {
        TO map(ResultSet resultSet) throws SQLException;
    }
}

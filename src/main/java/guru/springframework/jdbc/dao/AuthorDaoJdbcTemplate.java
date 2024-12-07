package guru.springframework.jdbc.dao;

import guru.springframework.jdbc.domain.Author;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.Statement;

@Component
@RequiredArgsConstructor
public class AuthorDaoJdbcTemplate implements AuthorDao {
    private final ObjectProvider<RowMapper<Author>> mapper;
    private final JdbcTemplate template;

    @Override
    public Author getById(Long id) {
        try {
            return template.queryForObject(
                    "select * from author where id = ?",
                    mapper.getObject(), id);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Author getByName(String firstName, String lastName) {
        return template.queryForObject(
                "select * from author where first_name = ? and last_name = ?",
                mapper.getObject(), firstName, lastName);
    }

    @Override
    public Author saveNew(Author author) {
        String sql = "INSERT INTO author (first_name, last_name) VALUES (?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        template.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, author.getFirstName());
            ps.setObject(2, author.getLastName());
            return ps;
        }, keyHolder);
        author.setId(keyHolder.getKey().longValue());
        return author;
    }

    @Override
    public Author update(Author author) {
        template.update(
                "update author set first_name = ?, last_name = ? where id = ?"
                , author.getFirstName(), author.getLastName(), author.getId());
        return author;
    }

    @Override
    public void deleteById(Long id) {
        template.update("delete from author where id = ?", id);
    }
}

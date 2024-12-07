package guru.springframework.jdbc.dao;

import guru.springframework.jdbc.domain.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;

@Component
@RequiredArgsConstructor
public class BookDaoJdbcTemplate implements BookDao {
    private final ObjectProvider<RowMapper<Book>> mapper;
    private final JdbcTemplate template;

    @Override
    public Book getById(Long id) {
        try {
            return template.queryForObject(
                    "select * from book where id = ?",
                    mapper.getObject(), id);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Book getByTitle(String title) {
        try {
            return template.queryForObject(
                    "select * from book where title = ?",
                    mapper.getObject(), title);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Book saveNew(Book book) {
        String sql = "INSERT INTO book (isbn, publisher, title, author_id) VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        template.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, book.getIsbn());
            ps.setObject(2, book.getPublisher());
            ps.setObject(3, book.getTitle());
            if (book.getAuthor() != null)
                ps.setObject(4, book.getAuthor());
            else
                ps.setNull(4, Types.BIGINT);
            return ps;
        }, keyHolder);
        book.setId(keyHolder.getKey().longValue());
        return book;
    }

    @Override
    public Book update(Book book) {
        template.update(
                "update book set isbn = ?, publisher = ?, title = ? where id = ?"
                , book.getIsbn(), book.getPublisher(), book.getTitle(), book.getId());
        return book;
    }

    @Override
    public void deleteById(Long id) {
        template.update("delete from book where id = ?", id);
    }
}

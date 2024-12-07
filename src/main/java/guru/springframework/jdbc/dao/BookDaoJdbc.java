package guru.springframework.jdbc.dao;

import guru.springframework.jdbc.domain.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

//@Component
@RequiredArgsConstructor
public class BookDaoJdbc implements BookDao {
    private final DataSourceExecutor executor;
    private final AuthorDao authorDao;

    private DataSourceExecutor.Mapper<Book> singleBookMapper() {
        return resultSet -> Book.builder()
                .id(resultSet.getLong(1))
                .title(resultSet.getString(2))
                .publisher(resultSet.getString(3))
                .isbn(resultSet.getString(4))
                .author(authorDao.getById(resultSet.getLong(5)))
                .build();
    }

    @Override
    public Book getById(Long id) {
        return executor
                .select(
                        "select id, title, publisher, isbn, author_id from book where id = ?",
                        executor.idFunction(singleBookMapper()),
                        id);
    }

    @Override
    public Book getByTitle(String title) {
        return executor
                .select(
                        "select id, title, publisher, isbn, author_id from book where title = ?",
                        executor.idFunction(singleBookMapper()),
                        title);
    }

    @Override
    public Book saveNew(Book book) {
        Long id = executor.insert("book", Map.of(
                        "isbn", Objects.toString(book.getIsbn(), ""),
                        "publisher", Objects.toString(book.getPublisher(), ""),
                        "title", Objects.toString(book.getTitle(), "")
                )
        );
        book.setId(id);
        return book;
    }

    @Override
    public Book update(Book book) {
        executor.update("book", book.getId(), Map.of(
                "isbn", Objects.toString(book.getIsbn(), ""),
                "publisher", Objects.toString(book.getPublisher(), ""),
                "title", Objects.toString(book.getTitle(), "")
        ));
        return book;
    }

    @Override
    public void deleteById(Long id) {
        executor.delete("book", id);
    }
}

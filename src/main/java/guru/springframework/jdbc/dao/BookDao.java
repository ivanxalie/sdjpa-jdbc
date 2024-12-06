package guru.springframework.jdbc.dao;

import guru.springframework.jdbc.domain.Book;

public interface BookDao {
    Book getById(Long id);

    Book getByTitle(String title);

    Book saveNew(Book book);

    Book update(Book book);

    void deleteById(Long id);
}

package com.example.lms.repository;

import com.example.lms.model.Books;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepo extends JpaRepository<Books,Integer> {
    @Query("SELECT r FROM Books r WHERE LOWER(r.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    List<Books> searchByTitle(String title);
    List<Books> findByTitle(String author);
    @Query("SELECT b FROM Books b JOIN b.category a WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Books> findByCategory_Name(String name);
    @Query("SELECT b FROM Books b JOIN b.authors a WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Books> findByAuthors_Name(String name);
    @Modifying
    @Query("DELETE FROM Books b WHERE b.id = :id")
    void deleteById(Integer id);
}

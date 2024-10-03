package com.example.lms.repository;

import com.example.lms.model.Reader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReaderRepo  extends JpaRepository<Reader, Integer> {

    @Query("SELECT r FROM Reader r WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Reader> findByName(@Param("name") String name);
}

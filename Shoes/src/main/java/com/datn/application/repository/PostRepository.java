package com.datn.application.repository;

import com.datn.application.entity.Post;
import com.datn.application.model.dto.PostDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query(value = "SELECT * " +
            "FROM post p " +
            "WHERE p.title LIKE CONCAT('%',:title,'%') " +
            "AND p.status LIKE CONCAT('%',:status,'%') ",nativeQuery = true)
    Page<Post> adminGetListPosts(String title, String status, Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT * FROM post p WHERE p.status = ?1 ORDER BY p.published_at DESC LIMIT ?2")
    List<Post> getLatesPosts(int status, int limit);

    Page<Post> findAllByStatus(int status, Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT * FROM post WHERE status = ?1 AND id != ?2 ORDER BY published_at DESC LIMIT ?3")
    List<Post> getLatestPostsNotId(int publicStatus, long id, int limit);
}

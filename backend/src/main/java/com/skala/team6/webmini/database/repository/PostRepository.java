package com.skala.team6.webmini.database.repository;

import com.skala.team6.webmini.database.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<PostEntity, Long> {
}

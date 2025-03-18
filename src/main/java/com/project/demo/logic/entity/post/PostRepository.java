package com.project.demo.logic.entity.post;

import com.project.demo.logic.entity.Post;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

}

package com.springboot.blog.service;

import com.springboot.blog.payload.PostDto;
import com.springboot.blog.payload.PaginationPostResponse;

import java.util.List;
import java.util.Optional;

public interface PostService {
    PostDto createPost(PostDto postDto);

    PaginationPostResponse getAllPosts(int pageNo, int pageSize, String sortBy, String sortDir);

//    Optional<PostDto> getPostById(long id);

    PostDto getPostById(long id);

    PostDto updatePost(PostDto postDto, long id);

    void deletePostById(long id);
}

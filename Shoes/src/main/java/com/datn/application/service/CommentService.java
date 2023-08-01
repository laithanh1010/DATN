package com.datn.application.service;

import com.datn.application.entity.Comment;
import com.datn.application.model.request.CreateCommentPostRequest;
import com.datn.application.model.request.CreateCommentProductRequest;
import org.springframework.stereotype.Service;

@Service
public interface CommentService {
    Comment createCommentPost(CreateCommentPostRequest createCommentPostRequest, long userId);
    Comment createCommentProduct(CreateCommentProductRequest createCommentProductRequest, long userId);
}

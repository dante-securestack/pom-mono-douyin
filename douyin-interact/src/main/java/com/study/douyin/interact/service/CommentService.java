package com.study.douyin.interact.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.study.douyin.interact.entity.CommentEntity;
import com.study.douyin.interact.vo.Comment;

import java.util.concurrent.ExecutionException;

public interface CommentService extends IService<CommentEntity> {
    Integer countByVideoId(Integer videoId);

    Comment PostComment(int userId, int videoId, int actionType, String commentText, Integer commentId);

    Comment[] getCommentList(int userId, int videoId) throws Exception;

}

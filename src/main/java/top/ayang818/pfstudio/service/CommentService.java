package top.ayang818.pfstudio.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.ayang818.pfstudio.dto.CommentDTO;
import top.ayang818.pfstudio.mapper.UserMapper;
import top.ayang818.pfstudio.model.Comment;
import top.ayang818.pfstudio.model.User;

/**
 * @ClassName CommentService
 * @Dessription TODO
 * @Author 杨丰畅
 * @Date 2019/9/6 19:12
 **/

@Service
public class CommentService {

    @Autowired
    private UserMapper userMapper;

    public CommentDTO changeToCommentDTO(Comment comment, User currentUser) {
        User user = userMapper.selectByPrimaryKey(comment.getCreator());
        CommentDTO commentDTO = new CommentDTO();
        BeanUtils.copyProperties(comment, commentDTO);
        commentDTO.setUserMessage(user);
        commentDTO.setHasRight(false);
        if (currentUser != null && comment.getCreator().equals(currentUser.getId())) {
            commentDTO.setHasRight(true);
        }
        return commentDTO;
    }
}

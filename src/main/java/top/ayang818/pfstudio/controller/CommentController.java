package top.ayang818.pfstudio.controller;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.ayang818.pfstudio.dto.CommentDTO;
import top.ayang818.pfstudio.dto.CommentInputDTO;
import top.ayang818.pfstudio.exception.CustomizeErrorCode;
import top.ayang818.pfstudio.mapper.CommentExtMapper;
import top.ayang818.pfstudio.mapper.CommentMapper;
import top.ayang818.pfstudio.mapper.UserMapper;
import top.ayang818.pfstudio.model.Comment;
import top.ayang818.pfstudio.model.CommentExample;
import top.ayang818.pfstudio.model.User;
import top.ayang818.pfstudio.model.UserExample;
import top.ayang818.pfstudio.service.CommentService;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName CommentController
 * @Dessription TODO
 * @Author 杨丰畅
 * @Date 2019/9/6 17:51
 **/

@RestController
public class CommentController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentExtMapper commentExtMapper;

    @RequestMapping(value = "/api/comments", method = RequestMethod.GET)
    public Object list(@RequestParam(value = "token", required = false) String token,
                        @RequestParam("offset") String offset,
                       @RequestParam("limit") String limit) {
        User user = null;
        if (token != null) {
            UserExample example = new UserExample();
            example.createCriteria().andTokenEqualTo(token);
            List<User> users = userMapper.selectByExample(example);
            if (token != null) {
                user = users.get(0);
            }
        }
        CommentExample example = new CommentExample();
        RowBounds rowBounds = new RowBounds(Integer.valueOf(offset), Integer.valueOf(limit));
        List<Comment> commentList =  commentMapper.selectByExampleWithRowbounds(example, rowBounds);
        List<CommentDTO> commentDTOList = new ArrayList<>();
        for (Comment comment : commentList) {
            CommentDTO commentDTO = commentService.changeToCommentDTO(comment, user);
            commentDTOList.add(commentDTO);
        }
        return commentDTOList;
    }

    @RequestMapping(value = "/api/comments", method = RequestMethod.POST)
    public Object insertComment(@RequestParam("token") String token,
                                 @RequestBody()CommentInputDTO commentInputDTO) {
        UserExample userExample = new UserExample();
        userExample.createCriteria().andTokenEqualTo(token);
        List<User> users = userMapper.selectByExample(userExample);
        if (users.size() == 0) {
            return CustomizeErrorCode.NEVER_AUTHRIZED;
        }
        Comment comment = new Comment();
        comment.setContent(commentInputDTO.getContent());
        comment.setCreator(users.get(0).getId());
        comment.setGmtCreated(System.currentTimeMillis());
        comment.setGmtModified(comment.getGmtCreated());
        int insert = commentMapper.insert(comment);
        return CustomizeErrorCode.SUCCESS;
    }

    @RequestMapping(value = "/api/comments", method = RequestMethod.DELETE)
    public CustomizeErrorCode deleteComment(@RequestParam("token") String token, @RequestParam("id") Integer id) {
        UserExample userExample = new UserExample();
        userExample.createCriteria().andTokenEqualTo(token);
        List<User> users = userMapper.selectByExample(userExample);
        if (users.size() == 0) {
            return CustomizeErrorCode.NEVER_AUTHRIZED;
        }
        Comment comment = commentMapper.selectByPrimaryKey(id);
        if (comment == null) {
            return CustomizeErrorCode.NO_SUCH_COMMENT;
        }
        if (comment.getCreator()!= users.get(0).getId()) {
            return CustomizeErrorCode.NEVER_AUTHRIZED;
        }
        int i = commentMapper.deleteByPrimaryKey(id);
        return CustomizeErrorCode.SUCCESS;
    }

    @RequestMapping(value = "/api/comments/count", method = RequestMethod.GET)
    public Long countPage() {
        Long dataNum = commentMapper.countByExample(new CommentExample());
        Long pageNum = dataNum/10+1;
        return pageNum;
    }
}

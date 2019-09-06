package top.ayang818.pfstudio.dto;

import lombok.Data;
import top.ayang818.pfstudio.model.User;

/**
 * @ClassName CommentDTO
 * @Dessription TODO
 * @Author 杨丰畅
 * @Date 2019/9/6 19:11
 **/

@Data
public class CommentDTO {
    private Integer id;

    private String content;

    private Long gmtCreated;

    private Long gmtModified;

    private Long creator;

    private User userMessage;

    private boolean isHasRight;
}

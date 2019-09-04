package top.ayang818.pfstudio.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.ayang818.pfstudio.dto.ResTokenDTO;
import top.ayang818.pfstudio.exception.CustomizeErrorCode;
import top.ayang818.pfstudio.exception.CustomizeException;
import top.ayang818.pfstudio.mapper.UserMapper;
import top.ayang818.pfstudio.model.User;
import top.ayang818.pfstudio.model.UserExample;

import java.util.List;

/**
 * @ClassName userController
 * @Dessription TODO
 * @Author 杨丰畅
 * @Date 2019/9/4 19:46
 **/

@RestController
public class userController {
    @Autowired
    private UserMapper userMapper;

    @ResponseBody
    @RequestMapping(value = "/api/users", method = RequestMethod.POST)
    public Object selectUser(@RequestBody()ResTokenDTO resTokenDTO) {
        String token = resTokenDTO.getToken();
        if (token == null) {
            return CustomizeErrorCode.NEVER_AUTHRIZED;
        }
        UserExample example = new UserExample();
        example.createCriteria().andTokenEqualTo(token);
        List<User> users = userMapper.selectByExample(example);
        if (users.size() == 0) {
            return CustomizeErrorCode.NO_SUCH_USER;
        }
        return users.get(0);
    }
}

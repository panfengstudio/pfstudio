package top.ayang818.pfstudio.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.ayang818.pfstudio.mapper.UserMapper;
import top.ayang818.pfstudio.model.User;
import top.ayang818.pfstudio.model.UserExample;

import java.util.List;

/**
 * @ClassName UserService
 * @Dessription TODO
 * @Author 杨丰畅
 * @Date 2019/9/6 16:34
 **/

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public int insertOrUpdate(User user) {
        UserExample example = new UserExample();
        example.createCriteria().andGithubIdEqualTo(user.getGithubId());
        List<User> users = userMapper.selectByExample(example);
        if (users.size() != 0) {
            int i = userMapper.updateByPrimaryKey(user);
            return i;
        }
        int insert = userMapper.insert(user);
        return insert;
    }
}

package top.ayang818.pfstudio.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.ayang818.pfstudio.exception.CustomizeErrorCode;
import top.ayang818.pfstudio.mapper.AttendMapper;
import top.ayang818.pfstudio.mapper.UserMapper;
import top.ayang818.pfstudio.model.Attend;
import top.ayang818.pfstudio.model.AttendExample;
import top.ayang818.pfstudio.model.User;
import top.ayang818.pfstudio.model.UserExample;

import java.util.Date;
import java.util.List;

/**
 * @ClassName AttendController
 * @Dessription TODO
 * @Author 杨丰畅
 * @Date 2019/9/8 13:02
 **/

@RestController
public class AttendController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AttendMapper attendMapper;

    @ResponseBody
    @RequestMapping(value = "/api/attend", method = RequestMethod.GET)
    public Object attend(@RequestParam("token") String token) {
        UserExample example = new UserExample();
        example.createCriteria().andTokenEqualTo(token);
        List<User> users = userMapper.selectByExample(example);
        if (users.size() == 0) {
            return CustomizeErrorCode.NEVER_AUTHRIZED;
        }
        User user = users.get(0);
        AttendExample attendExample = new AttendExample();
        attendExample.createCriteria().andCreatorEqualTo(user.getId());
        List<Attend> attends = attendMapper.selectByExample(attendExample);
        if (attends.size()!=0) {
            Attend lastAttend = attends.get(attends.size() - 1);
            Long gmtCreated = lastAttend.getGmtCreated();
            Date lastAttendDate = new Date(gmtCreated);
            Date current = new Date(System.currentTimeMillis());
            if (lastAttendDate.getDay() == current.getDay()) {
                return CustomizeErrorCode.HAD_ATTENDED;
            }
        }
        Attend attend = new Attend();
        attend.setCreator(user.getId());
        attend.setGmtCreated(System.currentTimeMillis());
        int insert = attendMapper.insert(attend);
        System.out.println(insert);
        return insert;
    }

    @ResponseBody
    @RequestMapping(value = "/api/attendtimes", method = RequestMethod.GET)
    public Object getAttendTimes(@RequestParam("token") String token) {
        UserExample example = new UserExample();
        example.createCriteria().andTokenEqualTo(token);
        List<User> users = userMapper.selectByExample(example);
        if (users.size() == 0) {
            return CustomizeErrorCode.NEVER_AUTHRIZED;
        }
        AttendExample exampleOption = new AttendExample();
        exampleOption.createCriteria().andCreatorEqualTo(users.get(0).getId());
        long times = attendMapper.countByExample(exampleOption);
        return times;
    }
}

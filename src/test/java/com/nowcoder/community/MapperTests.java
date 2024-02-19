package com.nowcoder.community;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTests {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Test
    public void testSelectUser(){
        User use = userMapper.selectById(101);
        System.out.println(use);

        use = userMapper.selectByName("liubei");
        System.out.println(use);

        use = userMapper.selectByEmail("nowcoder101@sina.com");
        System.out.println(use);
    }


    @Test
    public void testInsertUser(){  // 测试数据库插入语句
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("testuser@qq.com");
        user.setSalt("abc");
        user.setPassword("123456");
        user.setHeaderUrl("http://www.nowcoder.com/101.png");
        user.setCreateTime(new Date());

        int rows = userMapper.insertUser(user);
        System.out.println(rows);
        System.out.println(user.getId());
    }

    @Test
    public void testUpdataUser(){  // 测试userMapper接口的映射配置文件     测试数据库更新操作
        int rows = userMapper.updateStatus(150,1);
        System.out.println(rows);

        rows = userMapper.updateHeader(150,"http://www.nowcoder.com/102.png");
        System.out.println(rows);

        rows = userMapper.updatePassWord(150,"hello");
        System.out.println(rows);
    }


    @Test
    public void testSelectPost(){  //  测试DisscussPostMapper接口的映射配置文件    测试数据库语句查询操作
        List<DiscussPost> list = discussPostMapper.selectDiscussPosts(0,0,10);
        for(DiscussPost post :list){
            System.out.println(post);
        }
        int rows = discussPostMapper.selectDiscussPostRows(0);
        System.out.println(rows);
    }

}

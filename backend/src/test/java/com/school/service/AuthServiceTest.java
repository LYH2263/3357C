package com.school.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.school.entity.Teacher;
import com.school.entity.User;
import com.school.mapper.TeacherMapper;
import com.school.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private TeacherMapper teacherMapper;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private Teacher testTeacher;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUid(1);
        testUser.setUsername("student1");
        testUser.setUserpassword("123456");
        testUser.setUserno("2024001");
        testUser.setCheckedok("已通过");

        testTeacher = new Teacher();
        testTeacher.setTid(1);
        testTeacher.setTname("teacher1");
        testTeacher.setTpassword("123456");
        testTeacher.setTno("T001");
    }

    // ==================== login 方法测试 ====================

    @Test
    @DisplayName("学生登录 - 用户不存在")
    void testLogin_StudentNotFound() {
        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        Map<String, Object> result = authService.login("student1", "123456", "student");

        assertNotNull(result);
        assertFalse((Boolean) result.get("success"));
        assertEquals("用户名或密码错误", result.get("message"));
        assertNull(result.get("role"));
        assertNull(result.get("user"));
        verify(userMapper, times(1)).selectOne(any(LambdaQueryWrapper.class));
        verifyNoInteractions(teacherMapper);
    }

    @Test
    @DisplayName("学生登录 - 账号未通过审核")
    void testLogin_StudentNotApproved() {
        User pendingUser = new User();
        pendingUser.setUid(2);
        pendingUser.setUsername("student2");
        pendingUser.setUserpassword("123456");
        pendingUser.setCheckedok("待审核");

        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(pendingUser);

        Map<String, Object> result = authService.login("student2", "123456", "student");

        assertNotNull(result);
        assertFalse((Boolean) result.get("success"));
        assertEquals("账号审核中，请联系老师批准", result.get("message"));
        assertNull(result.get("role"));
        assertNull(result.get("user"));
        verify(userMapper, times(1)).selectOne(any(LambdaQueryWrapper.class));
        verifyNoInteractions(teacherMapper);
    }

    @Test
    @DisplayName("学生登录 - 登录成功")
    void testLogin_StudentSuccess() {
        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testUser);

        Map<String, Object> result = authService.login("student1", "123456", "student");

        assertNotNull(result);
        assertTrue((Boolean) result.get("success"));
        assertEquals("student", result.get("role"));
        assertSame(testUser, result.get("user"));
        assertNull(result.get("message"));
        verify(userMapper, times(1)).selectOne(any(LambdaQueryWrapper.class));
        verifyNoInteractions(teacherMapper);
    }

    @Test
    @DisplayName("教师登录 - 登录成功")
    void testLogin_TeacherSuccess() {
        when(teacherMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testTeacher);

        Map<String, Object> result = authService.login("teacher1", "123456", "teacher");

        assertNotNull(result);
        assertTrue((Boolean) result.get("success"));
        assertEquals("teacher", result.get("role"));
        assertSame(testTeacher, result.get("user"));
        assertNull(result.get("message"));
        verify(teacherMapper, times(1)).selectOne(any(LambdaQueryWrapper.class));
        verifyNoInteractions(userMapper);
    }

    @Test
    @DisplayName("教师登录 - 教师不存在")
    void testLogin_TeacherNotFound() {
        when(teacherMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        Map<String, Object> result = authService.login("teacher1", "123456", "teacher");

        assertNotNull(result);
        assertFalse((Boolean) result.get("success"));
        assertEquals("用户名或密码错误", result.get("message"));
        assertNull(result.get("role"));
        assertNull(result.get("user"));
        verify(teacherMapper, times(1)).selectOne(any(LambdaQueryWrapper.class));
        verifyNoInteractions(userMapper);
    }

    @Test
    @DisplayName("登录 - 非法角色")
    void testLogin_InvalidRole() {
        Map<String, Object> result = authService.login("admin", "123456", "admin");

        assertNotNull(result);
        assertFalse((Boolean) result.get("success"));
        assertEquals("非法角色", result.get("message"));
        assertNull(result.get("role"));
        assertNull(result.get("user"));
        verifyNoInteractions(userMapper, teacherMapper);
    }

    // ==================== registerStudent 方法测试 ====================

    @Test
    @DisplayName("学生注册 - 用户名已存在")
    void testRegisterStudent_UsernameExists() {
        User newUser = new User();
        newUser.setUsername("existingUser");
        newUser.setUserno("2024999");
        newUser.setUserpassword("123456");

        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testUser);

        Map<String, Object> result = authService.registerStudent(newUser);

        assertNotNull(result);
        assertFalse((Boolean) result.get("success"));
        assertEquals("用户名或学号已存在", result.get("message"));
        verify(userMapper, times(1)).selectOne(any(LambdaQueryWrapper.class));
        verify(userMapper, never()).insert(any(User.class));
    }

    @Test
    @DisplayName("学生注册 - 学号已存在")
    void testRegisterStudent_UsernoExists() {
        User newUser = new User();
        newUser.setUsername("newUser");
        newUser.setUserno("2024001");
        newUser.setUserpassword("123456");

        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testUser);

        Map<String, Object> result = authService.registerStudent(newUser);

        assertNotNull(result);
        assertFalse((Boolean) result.get("success"));
        assertEquals("用户名或学号已存在", result.get("message"));
        verify(userMapper, times(1)).selectOne(any(LambdaQueryWrapper.class));
        verify(userMapper, never()).insert(any(User.class));
    }

    @Test
    @DisplayName("学生注册 - 注册成功")
    void testRegisterStudent_Success() {
        User newUser = new User();
        newUser.setUsername("newStudent");
        newUser.setUserno("2024002");
        newUser.setUserpassword("123456");
        newUser.setUsersex("男");

        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        when(userMapper.insert(any(User.class))).thenReturn(1);

        Map<String, Object> result = authService.registerStudent(newUser);

        assertNotNull(result);
        assertTrue((Boolean) result.get("success"));
        assertEquals("注册成功，请等待老师审核", result.get("message"));
        assertEquals("待审核", newUser.getCheckedok());

        verify(userMapper, times(1)).selectOne(any(LambdaQueryWrapper.class));
        verify(userMapper, times(1)).insert(newUser);
    }
}

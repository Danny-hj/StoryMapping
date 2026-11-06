package cn.nju.edu.serviceImpl;

import cn.nju.edu.entity.User;
import cn.nju.edu.repository.UserRepository;
import cn.nju.edu.service.UserService;
import cn.nju.edu.vo.SimpleUserVo;
import cn.nju.edu.vo.UserLoginVo;
import cn.nju.edu.vo.UserPswdVo;
import cn.nju.edu.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public int getUserByNameAndPassword(UserLoginVo userLoginVo) {
        if (userLoginVo == null) {
            return 0;
        }
        String name = userLoginVo.getName();
        String password = userLoginVo.getPassword();
        if (name == null || password == null) {
            return 0;
        }
        User user = userRepository.findByName(name);
        if (user == null || user.getPassword() == null) {
            return 0;
        }
        if (name.equals(user.getName()) && passwordEncoder.matches(password, user.getPassword())) {
            return user.getId();
        }
        return 0;
    }

    @Override
    @Transactional
    public UserVo getUserByName(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        User user = userRepository.findByName(name);
        return getUserVo(user);
    }

    @Override
    @Transactional
    public UserVo getUserById(int id) {
        User user = userRepository.findById(id).orElse(new User());
        return getUserVo(user);
    }

    private UserVo getUserVo(User user) {
        if (user == null) {
            return null;
        }
        UserVo userVo = new UserVo();
        userVo.setId(user.getId());
        userVo.setName(user.getName());
        userVo.setNickname(user.getNickname());
        userVo.setEmail(user.getEmail());
        return userVo;
    }

    @Override
    @Transactional
    public int addUser(UserVo userVo) {
        if (userVo == null || userVo.getName() == null || userVo.getName().isEmpty()
                || userVo.getPassword() == null || userVo.getPassword().isEmpty()) {
            return 0;
        }
        if (userRepository.findByName(userVo.getName()) != null) {
            return 0;
        }
        User user = new User();
        user.setName(userVo.getName());
        user.setPassword(passwordEncoder.encode(userVo.getPassword()));
        user.setNickname(userVo.getNickname());
        user.setEmail(userVo.getEmail());
        userRepository.save(user);
        return userRepository.findByName(user.getName()).getId();
    }

    @Override
    @Transactional
    public boolean updateUser(UserVo userVo) {
        if (userVo == null || userVo.getNickname() == null || userVo.getNickname().isEmpty()) {
            return false;
        }
        int id = userVo.getId();
        if (id == 0) {
            return false;
        }
        User existing = userRepository.findById(id).orElse(null);
        if (existing == null) {
            return false;
        }
        existing.setNickname(userVo.getNickname());
        existing.setEmail(userVo.getEmail());
        userRepository.save(existing);
        return true;
    }

    @Override
    @Transactional
    public boolean updatePassword(UserPswdVo userPswdVo) {
        if (userPswdVo == null || userPswdVo.getOldPassword() == null
                || userPswdVo.getNewPassword() == null || userPswdVo.getNewPassword().isEmpty()) {
            return false;
        }
        int id = userPswdVo.getId();
        User existing = userRepository.findById(id).orElse(null);
        if (existing == null || existing.getPassword() == null) {
            return false;
        }
        if (!passwordEncoder.matches(userPswdVo.getOldPassword(), existing.getPassword())) {
            return false;
        }
        existing.setPassword(passwordEncoder.encode(userPswdVo.getNewPassword()));
        userRepository.save(existing);
        return true;
    }

    @Override
    public List<SimpleUserVo> listUser() {
        Iterable<User> all = userRepository.findAll();
        List<SimpleUserVo> users = new ArrayList<>();
        for (User user : all) {
            SimpleUserVo simpleUserVo = new SimpleUserVo();
            simpleUserVo.setId(user.getId());
            simpleUserVo.setName(user.getNickname());
            users.add(simpleUserVo);
        }
        return users;
    }

}

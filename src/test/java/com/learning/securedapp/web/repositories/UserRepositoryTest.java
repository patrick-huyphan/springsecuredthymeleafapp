package com.learning.securedapp.web.repositories;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.learning.securedapp.ApplicationTests;
import com.learning.securedapp.domain.User;
import com.learning.securedapp.exception.SecuredAppException;
import com.learning.securedapp.web.utils.KeyGeneratorUtil;

public class UserRepositoryTest extends ApplicationTests{
    
    @Autowired private RoleRepository roleRepository;
    @Autowired private UserRepository userRepository;

    @Test
    public final void test() throws SecuredAppException {
        userRepository.deleteAll();
        User user = new User();
        user.setUserName("superadmin");
        user.setPassword(KeyGeneratorUtil.encrypt("superadmin"));
        user.setEmail("rajadileepkolli@gmail.com");
        user.setEnabled(true);
        user.setRoleList(roleRepository.findAll());
        userRepository.save(user);
    }

}
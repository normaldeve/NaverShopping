package com.navershop.navershop.todo.custom.adapter.provider.user;

import com.navershop.navershop.template.adapter.provider.user.UserProvider;
import com.navershop.navershop.todo.repository.user.UserRepository;
import com.navershop.navershop.todo.repository.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * HomeSweet 프로젝트의 UserProvider 구현
 */
@Component
@RequiredArgsConstructor
public class HomeSweetUserProvider implements UserProvider<User> {

    private final UserRepository userRepository;

    @Override
    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다: " + userId));
    }
}
package com.aivlev.vcp.repository.storage;

import com.aivlev.vcp.model.User;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by aivlev on 4/27/16.
 */
public interface UserRepository extends PagingAndSortingRepository<User, String> {

    User findByLogin(String login);

    User findByEmail(String email);
}

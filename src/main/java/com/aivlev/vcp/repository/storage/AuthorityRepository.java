package com.aivlev.vcp.repository.storage;

import com.aivlev.vcp.model.Authority;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by aivlev on 4/27/16.
 */
public interface AuthorityRepository extends MongoRepository<Authority, String> {

    Authority findByName(String name);

}

package com.aivlev.vcp.repository.storage;

import com.aivlev.vcp.model.Token;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by aivlev on 4/27/16.
 */
public interface TokenRepository extends MongoRepository<Token, String> {
}

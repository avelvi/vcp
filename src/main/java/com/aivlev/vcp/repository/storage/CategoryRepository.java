package com.aivlev.vcp.repository.storage;

import com.aivlev.vcp.model.Category;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by aivlev on 5/6/16.
 */
public interface CategoryRepository extends MongoRepository<Category, String> {
}

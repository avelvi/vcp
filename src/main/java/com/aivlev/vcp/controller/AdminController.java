package com.aivlev.vcp.controller;

import com.aivlev.vcp.model.*;
import com.aivlev.vcp.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Created by aivlev on 4/29/16.
 */
@RestController
@RequestMapping(value = "/admin")
public class AdminController {

    @Autowired
    AdminService adminService;

    @Autowired
    UserService userService;

    @Autowired
    VideoService videoService;

    @Autowired
    CompanyService companyService;

    @Autowired
    CategoryService categoryService;

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public Object getAllUsers(@PageableDefault(size = 12)Pageable pageable){
        Page<User> result = adminService.findAllUsers(pageable);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
    public Object getUser(@PathVariable(value = "id") String id){
        User user = adminService.findUser(id);
        if(user == null){
            return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
        }
        user.setPassword(null);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @RequestMapping(value = "/users/{id}", method = RequestMethod.PUT)
    public void updateUser(@PathVariable(value = "id") String id, @RequestBody User user){
        userService.save(id, user);
    }

    @RequestMapping(value = "/videos", method = RequestMethod.GET)
    public Object getUser(@RequestParam("userId") String userId, @PageableDefault(size = 12)Pageable pageable){
        ResponseHolder<Page<Video>> result = videoService.findAllVideosByOwnerId(userId, pageable);
        return new ResponseEntity<>(result.getResponse(), HttpStatus.OK);
    }

    @RequestMapping(value = "/companies", method = RequestMethod.GET)
    public Object getCompanies(@PageableDefault(size = 10)Pageable pageable){
        Page<Company> result = companyService.findAllCompanies(pageable);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/companies/{id}", method = RequestMethod.GET)
    public Object getCompany(@PathVariable(value = "id") String id){
        Company company = companyService.findCompany(id);
        if(company == null){
            return new ResponseEntity<Company>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(company, HttpStatus.OK);
    }

    @RequestMapping(value = "/companies/{id}", method = RequestMethod.PUT)
    public Object updateCompany(@PathVariable(value = "id") String id, @RequestBody Company company){
        Company comp = companyService.updateCompany(company);
        if(company == null){
            return new ResponseEntity<Company>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(comp, HttpStatus.OK);
    }

    @RequestMapping(value = "/companies/{id}", method = RequestMethod.DELETE)
    public void deleteCompany(@PathVariable(value = "id") String id){
        companyService.deleteCompany(id);
    }

    @RequestMapping(value = "/companies", method = RequestMethod.POST)
    public Object createCompany(@RequestBody Company company){
        Company comp = companyService.saveCompany(company);
        return new ResponseEntity<>(comp, HttpStatus.OK);
    }

    @RequestMapping(value = "/categories", method = RequestMethod.GET)
    public Object getCategories(@PageableDefault(size = 10)Pageable pageable){
        Page<Category> result = categoryService.findAllCategories(pageable);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/categories/{id}", method = RequestMethod.GET)
    public Object getCategory(@PathVariable(value = "id") String id){
        Category category = categoryService.findCategory(id);
        if(category == null){
            return new ResponseEntity<Company>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(category, HttpStatus.OK);
    }

    @RequestMapping(value = "/categories/{id}", method = RequestMethod.PUT)
    public Object updateCategory(@PathVariable(value = "id") String id, @RequestBody Category category){
        Category updatedCategory = categoryService.updateCategory(category);
        if(category == null){
            return new ResponseEntity<Company>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
    }

    @RequestMapping(value = "/categories/{id}", method = RequestMethod.DELETE)
    public void deleteCategory(@PathVariable(value = "id") String id){
        categoryService.deleteCategory(id);
    }

    @RequestMapping(value = "/categories", method = RequestMethod.POST)
    public Object createCategory(@RequestBody Category category){
        Category newCategory = categoryService.saveCategory(category);
        return new ResponseEntity<>(newCategory, HttpStatus.OK);
    }

}

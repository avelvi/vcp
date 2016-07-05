package com.aivlev.vcp.controller;

import com.aivlev.vcp.model.Company;
import com.aivlev.vcp.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Created by aivlev on 5/16/16.
 */
@RestController
@RequestMapping(value = "/companies")
@PreAuthorize("hasAuthority('admin')")
public class CompanyController {

    @Autowired
    CompanyService companyService;

    @RequestMapping(method = RequestMethod.GET)
    public Object getCompanies(@PageableDefault(size = 10)Pageable pageable){
        Page<Company> result = companyService.findAll(pageable);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Object getCompany(@PathVariable(value = "id") String id){
        Company company = companyService.findOne(id);
        return new ResponseEntity<>(company, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public void updateCompany(@PathVariable(value = "id") String id, @RequestBody Company company){
        companyService.createOrUpdate(id, company);
    }

    @RequestMapping(method = RequestMethod.POST)
    public void createCompany(@RequestBody Company company){
        companyService.createOrUpdate(null, company);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteCompany(@PathVariable(value = "id") String id){
        companyService.deleteCompany(id);
    }

}

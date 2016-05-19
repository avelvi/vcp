package com.aivlev.vcp.controller;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Iterator;

/**
 * Created by aivlev on 5/18/16.
 */
public class GenericController {

    private static final String ADMIN_ROLE_VALUE = "admin";

    protected boolean isAdmin(UserDetails userDetails){
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

        Iterator iterator = authorities.iterator();

        GrantedAuthority grantedAuthority;
        do {
            if(!iterator.hasNext()) {
                return false;
            }
            grantedAuthority = (GrantedAuthority)iterator.next();
        }
        while(!ADMIN_ROLE_VALUE.equals(grantedAuthority.getAuthority()));

        return true;
    }

}

'use strict';

var appCompany = angular.module('app.company', ['ngRoute', 'app.constants'])
    .config(['$routeProvider', 'USER_ROLES',
        function($routeProvider, USER_ROLES){
            $routeProvider
                .when('/admin/companies', {
                    templateUrl: 'static/appjs/admin/company/list.html',
                    controller: 'CompanyController',
                    access: {
                        loginRequired: true,
                        authorizedRoles: [USER_ROLES.admin]
                    }
                }).when('/admin/companies/:id/edit', {
                    templateUrl: 'static/appjs/admin/company/details.html',
                    controller: 'CompanyDetailsController',
                    access: {
                        loginRequired: true,
                        authorizedRoles: [USER_ROLES.admin]
                    }
                })
        }
    ]);
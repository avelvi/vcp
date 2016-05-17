'use strict';

var appCategory = angular.module('app.category', ['ngRoute', 'app.constants'])
    .config(['$routeProvider', 'USER_ROLES',
        function($routeProvider, USER_ROLES){
            $routeProvider
                .when('/admin/categories', {
                    templateUrl: 'static/appjs/admin/category/list.html',
                    controller: 'CategoryController',
                    access: {
                        loginRequired: true,
                        authorizedRoles: [USER_ROLES.admin]
                    }
                }).when('/admin/categories/:id/edit', {
                    templateUrl: 'static/appjs/admin/category/details.html',
                    controller: 'CategoryDetailsController',
                    access: {
                        loginRequired: true,
                        authorizedRoles: [USER_ROLES.admin]
                    }
                })
        }
    ]);
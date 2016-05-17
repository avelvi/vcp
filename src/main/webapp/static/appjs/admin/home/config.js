'use strict';

var appAdminHome = angular.module('app.adminHome', ['ngRoute', 'app.constants'])
    .config(['$routeProvider', 'USER_ROLES',
        function($routeProvider, USER_ROLES){
            $routeProvider
                .when('/admin', {
                    templateUrl: 'static/appjs/admin/home/home.html',
                    controller: 'AdminHomeController',
                    access: {
                        loginRequired: true,
                        authorizedRoles: [USER_ROLES.admin]
                    }
                })
        }
    ]);
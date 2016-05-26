'use strict';

var appUsers = angular.module('app.users', ['ngRoute', 'app.constants'])
    .config(['$routeProvider', 'USER_ROLES',
        function($routeProvider, USER_ROLES){
            $routeProvider
                .when('/admin/users', {
                    templateUrl: 'static/appjs/admin/users/list.html',
                    controller: 'UsersController',
                    access: {
                        loginRequired: true,
                        authorizedRoles: [USER_ROLES.admin]
                    }
                }).when('/admin/users/:id/edit', {
                    templateUrl: 'static/appjs/admin/users/details.html',
                    controller: 'UserDetailsController',
                    access: {
                        loginRequired: true,
                        authorizedRoles: [USER_ROLES.admin]
                    }
                }).when('/admin/users/:id/videos', {
                    templateUrl: 'static/appjs/admin/users/user.video.list.html',
                    controller: 'UserVideosController',
                    access: {
                        loginRequired: true,
                        authorizedRoles: [USER_ROLES.admin]
                    }
                })
        }
    ]);
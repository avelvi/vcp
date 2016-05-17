'use strict';

var appAdminVideo = angular.module('app.adminVideo', ['ngRoute', 'app.constants'])
    .config(['$routeProvider', 'USER_ROLES',
        function($routeProvider, USER_ROLES){
            $routeProvider
                .when('/admin/videos', {
                    templateUrl: 'static/appjs/admin/video/list.html',
                    controller: 'AdminVideoController',
                    access: {
                        loginRequired: true,
                        authorizedRoles: [USER_ROLES.user]
                    }
                }).when('/admin/videos/:id/edit', {
                    templateUrl: 'static/appjs/admin/video/details.html',
                    controller: 'AdminVideoDetailsController',
                    access: {
                        loginRequired: true,
                        authorizedRoles: [USER_ROLES.admin]
                    }
                })
        }
    ]);
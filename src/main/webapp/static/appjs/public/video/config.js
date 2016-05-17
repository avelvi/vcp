'use strict';

var appVideo = angular.module('app.video', ['ngRoute', 'app.constants'])
    .config(['$routeProvider', 'USER_ROLES',
        function($routeProvider, USER_ROLES){
            $routeProvider
                .when('/videos', {
                    templateUrl: 'static/appjs/public/video/list.html',
                    controller: 'VideoController',
                    access: {
                        loginRequired: false,
                        authorizedRoles: [USER_ROLES.all]
                    }
                }).when('/videos/:id', {
                    templateUrl: 'static/appjs/public/video/details.html',
                    controller: 'VideoDetailsController',
                    access: {
                        loginRequired: false,
                        authorizedRoles: [USER_ROLES.all]
                    }
                }).when('/', {
                    redirectTo: '/videos'
                })
        }
    ]);
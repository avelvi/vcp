'use strict';

var appProfile = angular.module('app.profile', ['ngRoute', 'app.constants'])
    .config(['$routeProvider', 'USER_ROLES',
        function($routeProvider, USER_ROLES){
            $routeProvider
                .when('/profile/:id', {
                    templateUrl: 'static/appjs/public/profile/profile.html',
                    controller: 'ProfileController',
                    access: {
                        loginRequired: true,
                        authorizedRoles: [USER_ROLES.user]
                    }
                }).when('/profile/:id/edit', {
                    templateUrl: 'static/appjs/public/profile/profile.edit.html',
                    controller: 'ProfileEditController',
                    access: {
                        loginRequired: true,
                        authorizedRoles: [USER_ROLES.user]
                    }
                }).when('/profile/:id/changePassword', {
                    templateUrl: 'static/appjs/public/profile/change.password.html',
                    controller: 'ProfileChangePasswordController',
                    access: {
                        loginRequired: true,
                        authorizedRoles: [USER_ROLES.user]
                    }
                }).when('/profile/:id/video/:videoId', {
                    templateUrl: 'static/appjs/public/profile/edit.video.html',
                    controller: 'ProfileEditVideoController',
                    access: {
                        loginRequired: true,
                        authorizedRoles: [USER_ROLES.user]
                    }
                })
        }
    ]);
'use strict';

var appAuth = angular.module('app.auth', ['ngRoute', 'app.constants'])
    .config(['$routeProvider', 'USER_ROLES',
        function($routeProvider, USER_ROLES){
            $routeProvider
                .when('/signin', {
                    templateUrl: 'static/appjs/public/auth/signin.html',
                    controller: 'AuthController',
                    access: {
                        loginRequired: false,
                        authorizedRoles: [USER_ROLES.all]
                    }
                }).when('/signup', {
                    templateUrl: 'static/appjs/public/auth/signup.html',
                    controller: 'AuthController',
                    access: {
                        loginRequired: false,
                        authorizedRoles: [USER_ROLES.all]
                    }
                }).when('/registration', {
                    templateUrl: 'static/appjs/public/auth/registration.success.html',
                    controller: 'AuthController',
                    access: {
                        loginRequired: false,
                        authorizedRoles: [USER_ROLES.all]
                    }
                }).when('/forgot-password', {
                    templateUrl: 'static/appjs/public/auth/forgot-password.html',
                    controller: 'ForgotController',
                    access: {
                        loginRequired: false,
                        authorizedRoles: [USER_ROLES.all]
                    }
                }).when('/activate/code/:code', {
                    templateUrl: 'partials/public/activation.html',
                    controller: 'ActivationController',
                    access: {
                        loginRequired: false,
                        authorizedRoles: [USER_ROLES.all]
                    }
                }).when('/recovery/code/:code', {
                    templateUrl: 'static/appjs/public/auth/recovery.html',
                    controller: 'RecoveryController',
                    access: {
                        loginRequired: false,
                        authorizedRoles: [USER_ROLES.all]
                    },
                    resolve: {
                        factory: function($route, AuthSharedService){
                            AuthSharedService.validate($route.current.params.code)
                        }
                    }
                })
        }
    ]);

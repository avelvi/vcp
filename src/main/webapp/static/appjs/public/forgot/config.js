'use strict';

var appForgot = angular.module('app.forgot', ['ngRoute', 'app.constants'])
    .config(['$routeProvider', 'USER_ROLES',
        function($routeProvider, USER_ROLES){
            $routeProvider
                .when('/forgot-password', {
                    templateUrl: 'static/appjs/public/forgot/forgot-password.html',
                    controller: 'ForgotController',
                    access: {
                        loginRequired: false,
                        authorizedRoles: [USER_ROLES.all]
                    }
                }).when('/recovery/code/:code', {
                    templateUrl: 'static/appjs/public/forgot/recovery.html',
                    controller: 'RecoveryController',
                    access: {
                        loginRequired: false,
                        authorizedRoles: [USER_ROLES.all]
                    },
                    resolve: {
                        factory: function($route, ForgotService){
                            ForgotService.validate({data: $route.current.params.code})
                        }
                    }
                })
        }
    ]);

'use strict';

var appUpload = angular.module('app.upload', ['ngRoute', 'app.constants'])
    .config(['$routeProvider', 'USER_ROLES',
        function($routeProvider, USER_ROLES){
            $routeProvider
                .when('/upload', {
                    templateUrl: 'static/appjs/public/upload/upload.html',
                    controller: 'UploadController',
                    access: {
                        loginRequired: true,
                        authorizedRoles: [USER_ROLES.user]
                    }
                })
        }
    ]);
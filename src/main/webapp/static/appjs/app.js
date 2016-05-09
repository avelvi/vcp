
'use strict';

var app = angular.module('app', ['ngResource', 'ngRoute', 'ui.router', 'ui.bootstrap', 'http-auth-interceptor', 'app-filters', 'app-controllers', 'app-services']);

app.constant('USER_ROLES', {
    all: '*',
    admin: 'admin',
    user: 'user'
});

app.constant('ENTRIES_PER_PAGE', {
    1: 1,
    5: 5,
    10: 10,
    20: 20
});

app.config(['$routeProvider', 'USER_ROLES',
    function($routeProvider, USER_ROLES){
        $routeProvider.when('/home', {
            templateUrl: 'partials/public/home.html',
            controller: 'HomeController',
            access: {
                loginRequired: false,
                authorizedRoles: [USER_ROLES.all]
            }
        }).when('/', {
            redirectTo: '/home'
        }).when('/video/:videoId', {
            templateUrl: 'partials/public/video.html',
            controller: 'VideoController',
            access: {
                loginRequired: true,
                authorizedRoles: [USER_ROLES.admin]
            }
        }).when('/signin', {
            templateUrl: 'partials/public/signin.html',
            controller: 'LoginController',
            access: {
                loginRequired: false,
                authorizedRoles: [USER_ROLES.all]
            }
        }).when('/signup', {
            templateUrl: 'partials/public/signup.html',
            controller: 'SignUpController',
            access: {
                loginRequired: false,
                authorizedRoles: [USER_ROLES.all]
            }
        }).when('/home/search', {
            templateUrl: 'partials/public/searchResults.html',
            controller:'SearchResultsController',
            access: {
                loginRequired: false,
                authorizedRoles: [USER_ROLES.all]
            }
        }).when('/loading', {
            templateUrl: 'partials/public/loading.html',
            access: {
                loginRequired: false,
                authorizedRoles: [USER_ROLES.all]
            }
        }).when("/logout", {
            template: "",
            controller: "LogoutController",
            access: {
                loginRequired: false,
                authorizedRoles: [USER_ROLES.all]
            }
        }).when("/admin", {
            templateUrl: "partials/admin/admin.html",
            controller: "UsersListController",
            access: {
                loginRequired: true,
                authorizedRoles: [USER_ROLES.admin]
            }
        }).when("/admin/users", {
            templateUrl: "partials/admin/users.html",
            controller: "UsersListController",
            access: {
                loginRequired: true,
                authorizedRoles: [USER_ROLES.admin]
            }
        }).when("/admin/users/:id", {
            templateUrl: "partials/admin/user_details.html",
            controller: "UserDetailsController",
            access: {
                loginRequired: true,
                authorizedRoles: [USER_ROLES.admin]
            }
        }).when("/admin/users/:id/videos", {
            templateUrl: "partials/public/videos.html",
            controller: "VideosController",
            access: {
                loginRequired: true,
                authorizedRoles: [USER_ROLES.admin]
            }
        }).when("/admin/videos", {
            templateUrl: "partials/admin/videos.html",
            controller: "VideosListController",
            access: {
                loginRequired: true,
                authorizedRoles: [USER_ROLES.admin]
            }
        }).when("/admin/editVideo/:id", {
            templateUrl: "partials/public/video_details.html",
            controller: "VideoDetailsController",
            access: {
                loginRequired: true,
                authorizedRoles: [USER_ROLES.admin]
            }
        }).when("/admin/categories", {
            templateUrl: "partials/admin/categories.html",
            controller: "CategoriesListController",
            access: {
                loginRequired: true,
                authorizedRoles: [USER_ROLES.admin]
            }
        }).when("/admin/categories/edit/:id", {
            templateUrl: "partials/admin/category_details.html",
            controller: "CategoryDetailsController",
            access: {
                loginRequired: true,
                authorizedRoles: [USER_ROLES.admin]
            }
        }).when("/admin/categories/create", {
            templateUrl: "partials/admin/category_creation.html",
            controller: "CategoryCreationController",
            access: {
                loginRequired: true,
                authorizedRoles: [USER_ROLES.admin]
            }
        }).when("/admin/companies", {
            templateUrl: "partials/admin/companies.html",
            controller: "CompaniesListController",
            access: {
                loginRequired: true,
                authorizedRoles: [USER_ROLES.admin]
            }
        }).when("/admin/companies/edit/:id", {
            templateUrl: "partials/admin/company_details.html",
            controller: "CompanyDetailsController",
            access: {
                loginRequired: true,
                authorizedRoles: [USER_ROLES.admin]
            }
        }).when("/admin/companies/create", {
            templateUrl: "partials/admin/company_creation.html",
            controller: "CompanyCreationController",
            access: {
                loginRequired: true,
                authorizedRoles: [USER_ROLES.admin]
            }
        }).when("/error/:code", {
            templateUrl: "partials/public/error.html",
            controller: "ErrorController",
            access: {
                loginRequired: false,
                authorizedRoles: [USER_ROLES.all]
            }
        }).otherwise({
            redirectTo: '/error/404',
            access: {
                loginRequired: false,
                authorizedRoles: [USER_ROLES.all]
            }
        });
    }

]);


app.filter('pages', function () {
    return function (input, currentPage, totalPages, range) {
        currentPage = parseInt(currentPage);
        totalPages = parseInt(totalPages);
        range = parseInt(range);

        var minPage = (currentPage - range < 0) ? 0 : (currentPage - range > (totalPages - (range * 2))) ? totalPages - (range * 2) : currentPage - range;
        var maxPage = (currentPage + range > totalPages) ? totalPages : (currentPage + range < range * 2) ? range * 2 : currentPage + range;

        if(minPage < 0){
            minPage = 0;
        }
        if(maxPage > totalPages){
            maxPage = totalPages;
        }

        for(var i = minPage; i < maxPage; i++) {
            input.push(i);
        }

        return input;
    };
});

app.run(function ($rootScope, $location, $http, AuthSharedService, Session, USER_ROLES, $q, $timeout) {

    $rootScope.$on('$routeChangeStart', function (event, next) {

        $rootScope.authenticationError = false;

        if(next.originalPath === "/signin" && $rootScope.authenticated) {
            event.preventDefault();
        } else if (next.access && next.access.loginRequired && !$rootScope.authenticated) {
            event.preventDefault();
            $rootScope.$broadcast("event:auth-loginRequired", {});
        } else if (next.access && !AuthSharedService.isAuthorized(next.access.authorizedRoles)) {
            event.preventDefault();
            $rootScope.$broadcast("event:auth-forbidden", {});
        }
    });

    // Call when the the client is confirmed
    $rootScope.$on('event:auth-loginConfirmed', function (event, data) {
        console.log('login confirmed start ' + data);
        $rootScope.loadingAccount = false;
        var nextLocation = ($rootScope.requestedUrl ? $rootScope.requestedUrl : "/home");
        var delay = ($location.path() === "/loading" ? 1500 : 0);

        $timeout(function () {
            Session.create(data);
            $rootScope.account = Session;
            $rootScope.authenticated = true;
            $location.path(nextLocation).replace();
        }, delay);

    });

    // Call when the 401 response is returned by the server
    $rootScope.$on('event:auth-loginRequired', function (event, data) {
        if ($rootScope.loadingAccount && data.status !== 401) {
            $rootScope.requestedUrl = $location.path()
            $location.path('/loading');
        } else {
            Session.invalidate();
            $rootScope.authenticated = false;
            $rootScope.loadingAccount = false;
            $location.path('/signin');
        }
    });

    // Call when the 403 response is returned by the server
    $rootScope.$on('event:auth-forbidden', function (rejection) {
        $rootScope.$evalAsync(function () {
            $location.path('/error/403').replace();
        });
    });

    // Call when the user logs out
    $rootScope.$on('event:auth-loginCancelled', function () {
        $location.path('/home').replace();
    });

    // Get already authenticated user account
    AuthSharedService.getAccount();
});



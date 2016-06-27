'use strict';

appAdminHome.factory('AdminHomeService', ['$resource', function($resource){
    var data = $resource('/admin/statistics');
    return data;

}]);
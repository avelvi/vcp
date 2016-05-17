'use strict';

appSearch.factory('SearchService', ['$resource', function($resource){
    var data = $resource('/search', {},
        {
            findResults: {
                method: 'GET',
                isArray: false
            }
        });
    return data;
}]);




'use strict';

app.filter('trusted', ['$sce', function ($sce) {
    return function(url) {
        return $sce.trustAsResourceUrl(url);
    };
}]);

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


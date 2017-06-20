(function () {
    'use strict';

    angular
        .module('ecgViewerApp')
        .factory('Bill', Bill);

    Bill.$inject = ['$resource'];

    function Bill ($resource) {
        var service = $resource('api/users/bill', {}, {
            'query': {method: 'GET', isArray: true}
        });

        return service;
    }
})();

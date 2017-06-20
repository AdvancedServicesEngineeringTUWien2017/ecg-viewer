(function () {
    'use strict';

    angular
        .module('ecgViewerApp')
        .factory('ECGData', ECGData);

    ECGData.$inject = ['$resource'];

    function ECGData ($resource) {
        var service = $resource('api/ecgdata/:id', {}, {
            'query': {method: 'GET', isArray: true},
            'get': {
                method: 'GET'
            }
        });

        return service;
    }
})();

(function () {
    'use strict';

    angular
        .module('ecgViewerApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider.state('ecg-archive', {
            parent: 'app',
            url: '/ecg-archive',
            data: {
                authorities: []
            },
            views: {
                'content@': {
                    templateUrl: 'app/ecg-archive/ecg-archive.html',
                    controller: 'ECGArchiveController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                data: ['ECGData', function (ECGData) {
                    return ECGData.query();
                }]
            }
        });
    }
})();

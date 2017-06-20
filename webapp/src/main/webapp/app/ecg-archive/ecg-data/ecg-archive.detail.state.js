(function () {
    'use strict';

    angular
        .module('ecgViewerApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider.state('ecg-archive.detail', {
            parent: 'ecg-archive',
            url: '/ecg-archive/:dataId',
            data: {
                authorities: []
            },
            views: {
                'content@': {
                    templateUrl: 'app/ecg-archive/ecg-data/ecg-archive.detail.html',
                    controller: 'ECGArchiveDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                data: ['ECGData', '$stateParams', function (ECGData, $stateParams) {
                    return ECGData.get({id: $stateParams.dataId});
                }]
            }
        });
    }
})();

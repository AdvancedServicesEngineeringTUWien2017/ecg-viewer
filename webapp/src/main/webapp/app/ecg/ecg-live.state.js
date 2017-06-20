(function () {
    'use strict';

    angular
        .module('ecgViewerApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider.state('ecg-live', {
            parent: 'app',
            url: '/ecg-live',
            data: {
                authorities: []
            },
            views: {
                'content@': {
                    templateUrl: 'app/ecg/ecg-live.html',
                    controller: 'ECGLiveController',
                    controllerAs: 'vm'
                }
            },
            onEnter: ['ECGLiveService', function (ECGLiveService) {
                ECGLiveService.subscribe();
            }],
            onExit: ['ECGLiveService', function (ECGLiveService) {
                ECGLiveService.unsubscribe();
            }]
        });
    }
})();

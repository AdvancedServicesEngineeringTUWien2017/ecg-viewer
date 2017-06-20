(function() {
    'use strict';

    angular
        .module('ecgViewerApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider.state('bills', {
            parent: 'account',
            url: '/bill',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Bills'
            },
            views: {
                'content@': {
                    templateUrl: 'app/account/bill/bill.html',
                    controller: 'BillController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                data: ['Bill', function (Bill) {
                    return Bill.query();
                }]
            }
        });
    }
})();

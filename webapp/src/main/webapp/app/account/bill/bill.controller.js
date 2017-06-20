(function () {
    'use strict';

    angular
        .module('ecgViewerApp')
        .controller('BillController', BillController);

    BillController.$inject = ['data'];

    function BillController(data) {
        var vm = this;
        vm.bills = [];

        data.$promise.then(function (data_resolved) {
            vm.bills = data_resolved;
        }, function (reason) {
            alert('Failed: ' + reason);
        });

    }
})();

(function () {
    'use strict';

    angular
        .module('ecgViewerApp')
        .controller('ECGArchiveController', ECGArchiveController);

    ECGArchiveController.$inject = ['data', 'ECGData'];

    function ECGArchiveController(data, ECGData) {
        var vm = this;
        vm.download = download;

        data.$promise.then(function (data_resolved) {
            vm.data = data_resolved;
        }, function (reason) {
            alert('Failed: ' + reason);
        });

        function download(id) {
            ECGData.get({id: id}).$promise.then(function (data_resolved) {
                var dataStr = "data:text/json;charset=utf-8," + encodeURIComponent(JSON.stringify(data_resolved));
                var dlAnchorElem = document.getElementById('downloadAnchorElem');
                dlAnchorElem.setAttribute("href", dataStr);
                dlAnchorElem.setAttribute("download", id + "_data.json");
                dlAnchorElem.click();
            }, function (reason) {
                alert('Failed: ' + reason);
            });
        }


    }
})();

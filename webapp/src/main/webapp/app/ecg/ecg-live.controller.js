(function () {
    'use strict';

    angular
        .module('ecgViewerApp')
        .controller('ECGLiveController', ECGLiveController);

    ECGLiveController.$inject = ['$scope', 'ECGLiveService'];

    function ECGLiveController($scope, ECGLiveService) {
        var vm = this;

        vm.send = send;

        vm.sensors = {};

        var length_of_data = 5 * 500;

        function send() {
            ECGLiveService.sendActivity();
        }

        ECGLiveService.receive().then(null, null, function (activity) {

            if (activity.serial === -1) {
                delete vm.sensors[activity.id];
            }
            else {

                if (vm.sensors[activity.id] === undefined) {
                    vm.sensors[activity.id] = {
                        data: [],
                        serial: 0,
                        labels: [],
                        heartRate: 0.0
                    }
                }
                if (activity.heartRate !== undefined) {
                    vm.sensors[activity.id].heartRate = activity.heartRate;
                }
                if (vm.sensors[activity.id].data.length > length_of_data) {
                    var tmp = vm.sensors[activity.id].data.slice(-(vm.sensors[activity.id].data.length - 500));
                    tmp = tmp.concat(activity.leadI);
                    vm.sensors[activity.id].data = tmp;
                }
                else {

                    vm.sensors[activity.id].data = vm.sensors[activity.id].data.concat(activity.leadI);
                    vm.sensors[activity.id].labels = vm.sensors[activity.id].labels.concat(getLabel(vm.sensors[activity.id].serial++));
                }
            }
        });

        vm.series = ['ECG'];
        $scope.onClick = function (points, evt) {
            console.log(points, evt);
        };
        vm.datasetOverride = [{yAxisID: 'y-axis-1'}];
        vm.options = {
            elements: {
                point: {
                    radius: 0
                }
            },
            scales: {
                yAxes: [
                    {
                        id: 'y-axis-1',
                        type: 'linear',
                        display: true,
                        position: 'left'
                    }

                ],
                xAxes: [{
                    display: false
                }]
            },
            animation: {
                duration: 50
            }
        };

        function getLabel(serial) {
            var arr = new Array(500);
            arr[0] = serial;
            for (var i = 1; i < 500; i++) {
                arr[i] = arr[i - 1] + 1 / 500;
            }
            return arr;
        }


    }
})();

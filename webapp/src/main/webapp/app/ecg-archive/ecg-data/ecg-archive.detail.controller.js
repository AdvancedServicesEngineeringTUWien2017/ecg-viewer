(function () {
    'use strict';

    angular
        .module('ecgViewerApp')
        .controller('ECGArchiveDetailController', ECGArchiveDetailController);

    ECGArchiveDetailController.$inject = ['$scope', 'data'];

    function ECGArchiveDetailController($scope, data) {
        var vm = this;

        vm.increment = increment;
        vm.decrement = decrement;
        vm.reset = reset;


        vm.canvas = {
            data: [],
            labels: [],
            not_recorded: []
        };

        var length_of_data = 500;

        var length_of_window = 2;

        var null_array = new Array(length_of_data).fill(0);

        data.$promise.then(function (data_resolved) {
            vm.data = data_resolved;
            vm.data.serial = 1;
            vm.data.seconds = Object.keys(vm.data.values)[Object.keys(vm.data.values).length - 1];
            updateCanvas();
        }, function (reason) {
            alert('Failed: ' + reason);
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

        function increment() {
            if (vm.data.serial <= (vm.data.seconds - length_of_window)) {
                vm.data.serial++;
                updateCanvas();
            }
        }

        function decrement() {
            if (vm.data.serial > 1) {
                vm.data.serial--;
                updateCanvas();
            }
        }

        function reset() {
            vm.data.serial = 1;
            updateCanvas();
        }

        function updateCanvas() {
            var tmp = [];
            vm.canvas.not_recorded = [];
            for (var i = vm.data.serial; i < vm.data.serial + length_of_window; i++) {
                if (vm.data.values[i] !== undefined) {
                    tmp = tmp.concat(vm.data.values[i]);
                }
                else {
                    tmp = tmp.concat(null_array);
                    vm.canvas.not_recorded.push(i - 1);
                }
            }
            vm.canvas.data = tmp;
            vm.canvas.labels = getLabel(vm.data.serial);
            vm.data.secondLeft = vm.data.serial - 1;
            vm.data.secondRight = vm.data.serial + length_of_window - 1;
        }


        function getLabel(serial) {

            var arr = new Array(vm.canvas.data.length);
            arr[0] = serial;
            for (var i = 1; i < vm.canvas.data.length; i++) {
                arr[i] = arr[i - 1] + 1 / 500;
            }
            return arr;
        }


    }
})();

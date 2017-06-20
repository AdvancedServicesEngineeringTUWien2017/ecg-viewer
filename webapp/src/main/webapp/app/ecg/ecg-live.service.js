(function() {
    'use strict';
    /* globals SockJS, Stomp */

    angular
        .module('ecgViewerApp')
        .factory('ECGLiveService', ECGLiveService);

    ECGLiveService.$inject = ['$rootScope', '$window', '$cookies', '$http', '$q', 'AuthServerProvider'];

    function ECGLiveService ($rootScope, $window, $cookies, $http, $q, AuthServerProvider) {
        var stompClient = null;
        var subscriber = null;
        var listener = $q.defer();
        var connected = $q.defer();

        var service = {
            connect: connect,
            disconnect: disconnect,
            receive: receive,
            sendActivity: sendActivity,
            subscribe: subscribe,
            unsubscribe: unsubscribe
        };

        return service;

        function connect () {
            //todo auth with user

            //building absolute path so that websocket doesn't fail when deploying with a context path
            var loc = $window.location;
            var url = '//' + loc.host + loc.pathname + 'websocket/ecgdata';
            var authToken = AuthServerProvider.getToken();
            if(authToken){
                url += '?access_token=' + authToken;
            }
            var socket = new SockJS(url);
            stompClient = Stomp.over(socket);
            var stateChangeStart;
            var headers = {};
            stompClient.connect(headers, function() {
                connected.resolve('success');
                stompClient.debug = null;
            });
            $rootScope.$on('$destroy', function () {
                if(angular.isDefined(stateChangeStart) && stateChangeStart !== null){
                    stateChangeStart();
                }
            });
        }

        function disconnect () {
            if (stompClient !== null) {
                stompClient.disconnect();
                stompClient = null;
            }
        }

        function receive () {
            return listener.promise;
        }

        function sendActivity() {
            if (stompClient !== null && stompClient.connected) {
                stompClient
                    .send('/topic/ecgdata',
                    {},
                    'test');
            }
        }

        function subscribe () {
            connected.promise.then(function() {
                subscriber = stompClient.subscribe('/topic/ecgdata', function(data) {
                    listener.notify(angular.fromJson(data.body));
                });
            }, null, null);
        }

        function unsubscribe () {
            if (subscriber !== null) {
                subscriber.unsubscribe();
            }
            listener = $q.defer();
        }
    }
})();

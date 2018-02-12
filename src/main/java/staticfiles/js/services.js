/* 
 * cheff.casa is a platform that aims to cover all the business process
 * involved in operating a restaurant or a food delivery service. 
 *
 * Copyright (C) 2018  Laercio Metzner
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.  
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information please visit http://cheff.casa
 * or mail us via our e-mail contato@cheff.casa
 * or even mail-me via my own personal e-mail laercio.metzner@gmail.com 
 * 
 */
'use strict';

(function(){

angular.module("jFoodServices", ["ngStorage"])

.service("utilsService", ['$window', function($window){
    return {
        isDebug: false,
        localhostUrl: "http://localhost:8080",
        cloudUrl: "http://" + $window.location.hostname,
        apiPublicSufix: "/api/public",
        apiProtectedSufix: "/api/protected",
        init: function(){
            if (this.isDebug){
                this.apiPublicUrl = this.localhostUrl + this.apiPublicSufix;
                this.apiProtectedUrl = this.localhostUrl + this.apiProtectedSufix;
                this.webSocketUrl = this.localhostUrl.replace("http://","ws://");
            } else {
                this.apiPublicUrl = this.cloudUrl + this.apiPublicSufix;
                this.apiProtectedUrl = this.cloudUrl + this.apiProtectedSufix;
                this.webSocketUrl = this.cloudUrl.replace("http://","ws://") + ":8000";
            }
            return this;
        }
    }.init();
}])

.service("modalService", ['$window', function($window){

    return {
        title: '',
        msg: '',
        afterClick: function(){

        },
        show: function(){
            document.getElementById("showModal").click();
        },
        error: function(msg, afterClick){
            this.afterClick = afterClick || function(){};
            this.title = 'Ops, algo deu errado!';
            this.msg = msg;
            this.show();
        },
        warning: function(msg, afterClick){
            this.afterClick = afterClick || function(){};
            this.title = 'Atenção!';
            this.msg = msg;
            this.show();
        },
        success: function(msg, afterClick){
            this.afterClick = afterClick || function(){};
            this.title = 'Sucessso!';
            this.msg = msg;
            this.show();
        }
    }
}])

.service("validateInputService", [function(){
    return {};
}])

.factory('Main', ['$http', '$localStorage', 'utilsService', function($http, $localStorage, utilsService){

        var baseUrl = utilsService.apiPublicUrl;
        function changeUser(user) {
            angular.extend(currentUser, user);
        }
 
        function urlBase64Decode(str) {
            var output = str.replace('-', '+').replace('_', '/');
            switch (output.length % 4) {
                case 0:
                    break;
                case 2:
                    output += '==';
                    break;
                case 3:
                    output += '=';
                    break;
                default:
                    throw 'Illegal base64url string!';
            }
            return window.atob(output);
        }
 
        function getUserFromToken() {
            var token = $localStorage.token;
            var user = {};
            if (typeof token !== 'undefined') {
                var encoded = token.split('.')[1];
                user = JSON.parse(urlBase64Decode(encoded));
            }
            return user;
        }
 
        var currentUser = getUserFromToken();
 
        return {
            save: function(data, success, error) {
                $http.post(baseUrl + '/signin', data).then(success,error);
                //.success(success).error(error)
            },
            signin: function(data, success, error) {
                $http.post(baseUrl + '/authenticate', data).then(success, error);
                //.success(success).error(error)
            },
            me: function(success, error) {
                $http.get(baseUrl + '/me').then(success,error);      
                //success(success).error(error)
            },
            logout: function(success) {
                changeUser({});
                delete $localStorage.token;
                success();
            }
        };
    }
])
;

})();
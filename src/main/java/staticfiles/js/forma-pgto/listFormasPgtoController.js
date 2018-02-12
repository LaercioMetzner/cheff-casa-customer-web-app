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

    angular.module('jFoodControllers')
    
    .controller('listFormasPgtoController', ['$scope', '$http', '$routeParams', '$location', 'formaPgtoService', 'utilsService', function($scope, $http, $routeParams, $location, formaPgtoService, utilsService){

        $scope.editFormaPgto = function(formaPgto){
                $location.url('/editFormaPgto/' + $scope.codEstabelecimento + '/' + formaPgto.codFormaPgto);
        }

        $scope.newFormaPgto = function(){
                $location.url('/editFormaPgto/' + $scope.codEstabelecimento);
        }

        $scope.codEstabelecimento = $routeParams.codEstabelecimento;

        $http.get(utilsService.apiProtectedUrl + '/formaspgto/' + $routeParams.codEstabelecimento).then(function(res){
            formaPgtoService.formasPgto = res.data;
            $scope.formasPgto = formaPgtoService.formasPgto;
        }, function(res){
            alert(JSON.stringify(res.data));
        });

    }])
;

})();

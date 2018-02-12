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
    
    .controller('listTamanhosController', ['$scope', '$http', '$routeParams', '$location', 'tamanhoService', 'utilsService', function($scope, $http, $routeParams, $location, tamanhoService, utilsService){

        $scope.editTamanho = function(tamanho){
                $location.url('/editTamanho/' + tamanho.codEstabelecimento + '/' + tamanho.codTamanho);
        }
        $scope.newTamanho = function(){
                $location.url('/editTamanho/' + $scope.codEstabelecimento);
        }

        $scope.codEstabelecimento = $routeParams.codEstabelecimento;

        $http.get(utilsService.apiProtectedUrl + '/tamanhos/' + $routeParams.codEstabelecimento).then(function(res){
            tamanhoService.tamanhos = res.data;
            $scope.tamanhos = tamanhoService.tamanhos;
        }, function(res){
            alert(JSON.stringify(res.data));
        });

    }]);

})();

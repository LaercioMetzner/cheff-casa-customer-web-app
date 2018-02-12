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

    .controller('listPrecosItemCardapioController', ['$scope', '$http', '$routeParams', '$location', 'precoItemCardapioService', 'utilsService', function($scope, $http, $routeParams, $location, precoItemCardapioService, utilsService){

        $scope.editPreco = function(preco){
                $location.url('/editPreco/' + $scope.codEstabelecimento + '/' + $scope.codCardapio + '/' + $scope.codItemCardapio + '/' + preco.codPreco);
        }

        $scope.newPreco = function(preco){
                $location.url('/editPreco/' + $scope.codEstabelecimento + '/' + $scope.codCardapio + '/' + $scope.codItemCardapio);
        }

        $scope.codEstabelecimento = $routeParams.codEstabelecimento;
        $scope.codCardapio = $routeParams.codCardapio;
        $scope.codItemCardapio = $routeParams.codItemCardapio;

        $http.get(utilsService.apiProtectedUrl + '/cardapios/' + $routeParams.codEstabelecimento + '/' + $scope.codCardapio + '/itens/' + $scope.codItemCardapio + '/precos').then(function(res){
            precoItemCardapioService.precosItemCardapio = res.data;
            $scope.precosItemCardapio = precoItemCardapioService.precosItemCardapio;
        }, function(res){
            alert(JSON.stringify(res.data));
        });

    }]);

})();
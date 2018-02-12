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
    
    .controller('searchItensCardapioController', ['$scope', '$http', '$location', '$window', 'utilsService', 'estabelecimentoService', function($scope, $http, $location, $window, utilsService, estabelecimentoService){

        $scope.estabelecimento = estabelecimentoService.current;

        $http.get(utilsService.apiProtectedUrl + '/cardapios/' + $scope.estabelecimento.codEstabelecimento).then(function(res){
            $scope.cardapios = res.data;
        }, function(res){
            alert(JSON.stringify(res.data));;
        });

        $scope.search = function(){
            if (!$scope.estabelecimento){
                $window.alert('Selecione um estabelecimento.');
                return;
            }

            if (!$scope.cardapio){
                $window.alert('Selecione um cardápio.');
                return;
            }

            //$window.alert('Run bitch!');
            $location.path('/listItensCardapio/' + $scope.estabelecimento.codEstabelecimento + '/' + $scope.cardapio.codCardapio); 
        };

    /*
        $scope.$watch("estabelecimento", function(newVale, oldValue){
                if (newVale) {
                $http.get(utilsService.apiProtectedUrl + '/cardapios/' + $scope.estabelecimento.codEstabelecimento).then(function(res){
                    $scope.cardapios = res.data;
                }, function(res){
                    alert(JSON.stringify(res.data));;
                });            
            }
        });
    */
        $http.get(utilsService.apiProtectedUrl + '/estabelecimentos').then(function(res){
            $scope.estabelecimentos = res.data;
        }, function(res){
            alert(JSON.stringify(res.data));;
        });

    }]);

})();


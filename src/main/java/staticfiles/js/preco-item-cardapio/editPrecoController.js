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

    .controller('editPrecoController', ['$scope', '$http', '$routeParams', '$location', 'precoItemCardapioService', 'utilsService', '$window', function($scope, $http, $routeParams, $location, precoItemCardapioService, utilsService, $window){

        $scope.isUpdate = $routeParams.codPrecoItemCardapio !== undefined;
        $scope.precosItemCardapio = precoItemCardapioService.precosItemCardapio;
        $scope.preco = {};

        $scope.save = function(){
            if ($scope.sabor)
                $scope.preco.codSabor = $scope.sabor.codSabor;
            if ($scope.tamanho)
                $scope.preco.codTamanho = $scope.tamanho.codTamanho;
            if (!($scope.preco.qtdSabor > 0)){
                $window.alert('A Fração de sabores deve ser maior que zero');
                return;
            }
            if ($scope.isUpdate){
                // TODO: the following line will remain just until datepicker strategy is up and running
                delete($scope.preco.datCadastro);
                $http.put(utilsService.apiProtectedUrl + '/cardapios/' + $routeParams.codEstabelecimento + '/' + $routeParams.codCardapio + '/itens/' + $routeParams.codItemCardapio + '/precos/' + $scope.preco.codPreco, $scope.preco).then(function(res){
                    if (res.status == 204){
                        $window.history.back();
                    } else {
                        $window.alert(res.status + JSON.stringify(res.data));   
                    }
                }, function(res){
                   $window.alert(res.status + JSON.stringify(res.data));
               });            
            }else {
                $http.post(utilsService.apiProtectedUrl + '/cardapios/' + $routeParams.codEstabelecimento + '/' + $routeParams.codCardapio + '/itens/' + $routeParams.codItemCardapio + '/precos', $scope.preco).then(function(res){
                    if (res.status == 201){
                        //$location.path(gotoLocation);
                        $window.history.back();
                    } else {
                        $window.alert(res.status + JSON.stringify(res.data));   
                    }
                }, function(res){
                   $window.alert(res.status + JSON.stringify(res.data));
               });             
            }
        };

        if (!$scope.isUpdate){
            $scope.preco.indAtivo = 'S';
            $scope.preco.codEstabelecimento = $routeParams.codEstabelecimento;
            $scope.preco.codCardapio = $routeParams.codCardapio;
            $scope.preco.codItemCardapio = $routeParams.codItemCardapio;
            $scope.preco.qtdSabor = 1;
        } else {
            for (var i in $scope.precosItemCardapio){
                if ($scope.precosItemCardapio[i].codPreco == $routeParams.codPrecoItemCardapio){
                    $scope.preco = $scope.precosItemCardapio[i];                
                    break;
                }
            }
        }

        $http.get(utilsService.apiProtectedUrl + '/sabores/' + $routeParams.codEstabelecimento).then(function(res){
            $scope.sabores = res.data;
            for (var i in $scope.sabores){
                if ($scope.sabores[i].codSabor == $scope.preco.codSabor){
                    $scope.sabor = $scope.sabores[i];
                    break;  
                }
            }
        }, function(res){
            alert(JSON.stringify(res.data));;
        });

        $http.get(utilsService.apiProtectedUrl + '/tamanhos/' + $routeParams.codEstabelecimento).then(function(res){
            $scope.tamanhos = res.data;
            for (var i in $scope.tamanhos){
                if ($scope.tamanhos[i].codTamanho == $scope.preco.codTamanho){
                    $scope.tamanho = $scope.tamanhos[i];
                    break;  
                }
            }
                    //$scope.tamanho.codTamanho = $scope.preco.codTamanho;
        }, function(res){
            alert(JSON.stringify(res.data));;
        });

    }]);

})();




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

    .controller('editCardapioController', ['$scope', '$http', '$routeParams', '$location', 'cardapioService', 'utilsService', '$window', 'modalService', function($scope, $http, $routeParams, $location, cardapioService, utilsService, $window, modalService){

        $scope.isUpdate = $routeParams.codCardapio !== undefined;
        $scope.cardapios = cardapioService.cardapios;
        $scope.cardapio = {};

        $scope.save = function(){
            var gotoLocation = '/listCardapios/' + $routeParams.codEstabelecimento;
            if ($scope.isUpdate){
                // TODO: the following line will remain just until datepicker strategy is up and running
                delete($scope.cardapio.datCadastro);
                $http.put(utilsService.apiProtectedUrl + '/cardapios/' + $routeParams.codCardapio, $scope.cardapio).then(function(res){
                    if (res.status == 204){
                        $window.history.back();
                    } else {
                        modalService.error(res.status + JSON.stringify(res.data));   
                    }
                }, function(res){
                   modalService.error(res.status + JSON.stringify(res.data));
               });            
            }else {
                $http.post(utilsService.apiProtectedUrl + '/cardapios', $scope.cardapio).then(function(res){
                    if (res.status == 201){
                        $location.path(gotoLocation);
                    } else {
                        modalService.error(res.status + JSON.stringify(res.data));   
                    }
                }, function(res){
                   modalService.error(res.status + JSON.stringify(res.data));
               });             
            }
        };

        if (!$scope.isUpdate){
            $scope.cardapio.indAtivo = 'S';
            $scope.cardapio.codEstabelecimento = $routeParams.codEstabelecimento;
        } else {
            for (var i in $scope.cardapios){
                if ($scope.cardapios[i].codCardapio == $routeParams.codCardapio){
                    $scope.cardapio = $scope.cardapios[i];
                    break;
                }
            }
        }

    }]);

})();

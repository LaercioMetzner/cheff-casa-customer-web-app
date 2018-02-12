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

    .controller('editItemCardapioController', ['$scope', '$http', '$routeParams', '$location', 'itensCardapioService', 'utilsService', '$window', '$rootScope', function($scope, $http, $routeParams, $location, itensCardapioService, utilsService, $window, $rootScope){

        $scope.isUpdate = $routeParams.codItemCardapio !== undefined;
        $scope.itensCardapio = itensCardapioService.itensCardapio;
        $scope.itemCardapio = {};

        $scope.save = function(){
            var gotoLocation = '/listItensCardapio/' + $routeParams.codEstabelecimento + '/' + $routeParams.codCardapio;
            $scope.itemCardapio.codPhotoIdItem = $rootScope.codPhotoIdItem;
            if ($scope.isUpdate){
                // Guarantee that it's gonna be inserted null if item description is not informed
                if ($scope.itemCardapio.desItemCardapio === ""){
                    delete($scope.itemCardapio.desItemCardapio);
                }
                // TODO: the following line will remain just until datepicker strategy is up and running
                delete($scope.itemCardapio.datCadastro);
                $http.put(utilsService.apiProtectedUrl + '/cardapios/' + $routeParams.codEstabelecimento + '/' + $routeParams.codCardapio + '/itens/' + $routeParams.codItemCardapio, $scope.itemCardapio).then(function(res){
                    if (res.status == 204){
                        delete($rootScope.codPhotoIdItem);
                        $window.history.back();
                    } else {
                        alert(res.status + JSON.stringify(res.data));   
                    }
                }, function(res){
                   alert(res.status + JSON.stringify(res.data));
               });            
            }else {
                // Guarantee that it's gonna be inserted null if item description is not informed
                if ($scope.itemCardapio.desItemCardapio === ""){
                    delete($scope.itemCardapio.desItemCardapio);
                }
                $http.post(utilsService.apiProtectedUrl + '/cardapios/' + $routeParams.codEstabelecimento + '/' + $routeParams.codCardapio + '/itens', $scope.itemCardapio).then(function(res){
                    if (res.status == 201){
                        $location.path(gotoLocation);
                    } else {
                        alert(res.status + JSON.stringify(res.data));   
                    }
                }, function(res){
                   alert(res.status + JSON.stringify(res.data));
               });             
            }
        };

        if (!$scope.isUpdate){
            $scope.itemCardapio.indAtivo = 'S';
            $scope.itemCardapio.codEstabelecimento = $routeParams.codEstabelecimento;
            $scope.itemCardapio.codCardapio = $routeParams.codCardapio;
            delete($rootScope.codPhotoIdItem);
        } else {
            for (var i in $scope.itensCardapio){
                if ($scope.itensCardapio[i].codItemCardapio == $routeParams.codItemCardapio){
                    $scope.itemCardapio = $scope.itensCardapio[i];
                    $rootScope.codPhotoIdItem = $scope.itemCardapio.codPhotoIdItem;
                    break;
                }
            }
        }

    }]);

})();

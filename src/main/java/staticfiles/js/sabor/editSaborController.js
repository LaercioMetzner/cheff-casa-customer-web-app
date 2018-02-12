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
    
    .controller('editSaborController', ['$scope', '$http', '$routeParams', '$location', 'saborService', '$window', 'utilsService', function($scope, $http, $routeParams, $location, saborService, $window, utilsService){

        $scope.isUpdate = $routeParams.codSabor !== undefined;
        $scope.sabores = saborService.sabores;
        $scope.sabor = {};

        $scope.save = function(){
            if ($scope.isUpdate){
                // Guarantee that it's gonna be inserted null if flavor description is not informed
                if ($scope.sabor.desSabor === ""){
                    delete($scope.sabor.desSabor);
                }
                // TODO: the following line will remain just until datepicker strategy is up and running
                delete($scope.sabor.datCadastro);
                $http.put(utilsService.apiProtectedUrl + '/sabores/' + $routeParams.codSabor, $scope.sabor).then(function(res){
                    if (res.status == 204){
                        $window.history.back();
                    } else {
                        alert(res.status + JSON.stringify(res.data));   
                    }
                }, function(res){
                   alert(res.status + JSON.stringify(res.data));
               });            
            }else {
                // Guarantee that it's gonna be inserted null if flavor description is not informed
                if ($scope.sabor.desSabor === ""){
                    delete($scope.sabor.desSabor);
                }
                $http.post(utilsService.apiProtectedUrl + '/sabores', $scope.sabor).then(function(res){
                    if (res.status == 201){
                        $window.history.back();
                    } else {
                        alert(res.status + JSON.stringify(res.data));   
                    }
                }, function(res){
                   alert(res.status + JSON.stringify(res.data));
               });
            }
        };

        if (!$scope.isUpdate){
            $scope.sabor.indAtivo = 'S';
            $scope.sabor.codEstabelecimento = $routeParams.codEstabelecimento;
        } else {
            for (var i in $scope.sabores){
                if ($scope.sabores[i].codSabor == $routeParams.codSabor){
                    $scope.sabor = $scope.sabores[i];
                    break;
                }
            }
        }

    }]);

})();
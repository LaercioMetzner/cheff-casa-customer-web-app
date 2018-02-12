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

	.controller('editEstabelecimentoController', ['$scope', '$http', '$routeParams', '$location', 'estabelecimentoService', 'utilsService', '$rootScope', 'validateInputService', 'modalService', function($scope, $http, $routeParams, $location, estabelecimentoService, utilsService, $rootScope, validateInputService, modalService){

	    $scope.isUpdate = $routeParams.codEstabelecimento !== undefined;
	    $scope.estabelecimentoService = estabelecimentoService;
	    $scope.estabelecimentos = estabelecimentoService.estabelecimentos;

	    $scope.save = function(){
	        $scope.estabelecimento.codPhotoIdLogotipo = $rootScope.codPhotoIdLogotipo;
	        $scope.estabelecimento.codPhotoIdCapa = $rootScope.codPhotoIdCapa;
	        if ($routeParams.codEstabelecimento){
	            $http.put(utilsService.apiProtectedUrl + '/estabelecimentos/' + $routeParams.codEstabelecimento, $scope.estabelecimento).then(function(res){
	                if (res.status == 204){
	                    $location.path('/listEstabelecimentos');
	                } else {
	                    modalService.error(res.status + JSON.stringify(res.data));   
	                }
	            }, function(res){
	               modalService.error(res.status + JSON.stringify(res.data));
	           });            
	        }else {
	            $http.post(utilsService.apiProtectedUrl + '/estabelecimentos', $scope.estabelecimento).then(function(res){
	                if (res.status == 201){
	                    $location.path('/listEstabelecimentos');
	                } else {
	                    modalService.error(res.status + JSON.stringify(res.data));   
	                }
	            }, function(res){
	               modalService.error(res.status + JSON.stringify(res.data));
	           });            
	        }
	        delete($rootScope.codPhotoIdLogotipo);
	        delete($rootScope.codPhotoIdCapa);
	    };

	    if (!$scope.isUpdate){
	        $scope.estabelecimento = {};
	        $scope.estabelecimento.indAtivo = 'S';
	        delete($rootScope.codPhotoIdLogotipo);
	        delete($rootScope.codPhotoIdCapa);
	    }

	    for (var estabelecimento in $scope.estabelecimentos){
	        if ($scope.estabelecimentos[estabelecimento].codEstabelecimento == $routeParams.codEstabelecimento){
	            $scope.estabelecimento = $scope.estabelecimentos[estabelecimento];
	            $rootScope.codPhotoIdLogotipo = $scope.estabelecimento.codPhotoIdLogotipo;
	            $rootScope.codPhotoIdCapa = $scope.estabelecimento.codPhotoIdCapa;
	            break;
	        }
	    } 

	}]);

})();
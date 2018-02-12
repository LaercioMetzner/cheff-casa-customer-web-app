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
    
    .controller('editParceiroController', ['$scope', '$http', '$routeParams', '$location', 'parceiroService', '$window', 'utilsService', '$base64', function($scope, $http, $routeParams, $location, parceiroService, $window, utilsService, $base64){

        $scope.isUpdate = $routeParams.codParceiro !== undefined;
        $scope.parceiros = parceiroService.parceiros;
        $scope.parceiro = {};

        $scope.save = function(){
            var parceiro = angular.copy($scope.parceiro);
            if ($scope.alteraSenha == 'S'){
                if ($scope.parceiro.desSenha == undefined || $scope.parceiro.desSenha == ''){
                    $window.alert('Informe uma senha.');
                    return;
                }            
                if ($scope.repSenha == undefined || $scope.repSenha == ''){
                    $window.alert('Repita a senha.');
                    return;
                }            
                if ($scope.parceiro.desSenha != $scope.repSenha){
                    $window.alert('Repita exatamente a mesma senha.');
                    return;
                }
                parceiro.desSenha = md5($scope.parceiro.desSenha);
            } else {
                delete(parceiro.desSenha);
            }
            if ($scope.isUpdate){
                // TODO: the following line will remain just until datepicker strategy is up and running
                delete($scope.parceiro.datCadastro);
                var payload = $base64.encode(JSON.stringify(parceiro));
                $http.put(utilsService.apiProtectedUrl + '/parceiros/' + $routeParams.codParceiro, payload).then(function(res){
                    if (res.status == 204){
                        $window.history.go(-2);
                    } else {
                        $window.alert(res.status + JSON.stringify(res.data));   
                    }
                }, function(res){
                   alert(res.status + JSON.stringify(res.data));
               });            
            }else {
                parceiro.indAtivo = 'S';
                var payload = $base64.encode(JSON.stringify(parceiro));
                $http.post(utilsService.apiPublicUrl + '/parceiros', payload).then(function(res){
                    if (res.status == 201){
                        $window.history.back();
                    } else {
                        $window.alert(res.status + JSON.stringify(res.data));   
                    }
                }, function(res){
                   $window.alert(res.status + JSON.stringify(res.data));
               });
            }
        };

        if ($scope.isUpdate){
            for (var i in $scope.parceiros){
                if ($scope.parceiros[i].codParceiro == $routeParams.codParceiro){
                    $scope.parceiro = $scope.parceiros[i];
                    break;
                }
            }
            $scope.alteraSenha = 'N';
            $scope.parceiro.desSenha = '';
        } else {
            $scope.alteraSenha = 'S';
            $scope.parceiro.codParceiro = $routeParams.codParceiro;        
        }    

    }]);

})();

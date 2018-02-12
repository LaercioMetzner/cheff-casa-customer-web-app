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
    .controller('editPedidoController', ['$scope', '$http', '$routeParams', '$location', 'pedidoService', '$window', 'utilsService', 'modalService', function($scope, $http, $routeParams, $location, pedidoService, $window, utilsService, modalService){

    $scope.pedidos = pedidoService.pedidos;
    $scope.pedido = {};


    $scope.formatarReal = function(x) {
        return formatReal(x);
    };
    
    $scope.saboresComVirgula = function(itemPedido){
        var i = itemPedido.sabores.length;
        var strSaboresComVirgula = '';
        while (i--){
            if (strSaboresComVirgula != ''){
                strSaboresComVirgula += ', ';
            }
            strSaboresComVirgula +=  itemPedido.sabores[i].nomSabor;
        }
        return strSaboresComVirgula;
    };

    $scope.cancelaPedido = function(pedido){
        if (pedido.indStatus == "C"){
            modalService.warning('Não é possível cancelar um pedido fechado');
        } else {
            $http.post(utilsService.apiProtectedUrl + '/pedidos/cancelar/' + $scope.pedido.codPedido , '').then(function(res){
                if (res.status == 201){
                    $scope.pedido.indStatus = 'X';
                    modalService.success('Pedido Cancelado com sucesso');
                } else {
                    modalService.error(res.status + JSON.stringify(res.data));   
                }
            }, function(res){
               modalService.error(res.status + JSON.stringify(res.data));
           }); 
        }
    };

    $scope.enviarParaEntrega = function(pedido){
        if (pedido.indStatus == "C"){
            modalService.warning('Não é possível enviar para entrega um pedido já fechado');
        } else if (pedido.indStatus == "S"){
            modalService.warning('Você já enviou este pedido para entrega');
        } else if (pedido.indStatus == "X"){
            modalService.warning('  Não é possível enviar para entrega um pedido cancelado');
        } else {
             document.getElementById("showDadosEntrega").click();
        }
    };

    $scope.enviarPedido = function(){
        
        if (!$scope.pedido.nomEntregador){
            modalService.error('É obrigatório informar o nome do entregador.', function(){
                document.getElementById("showDadosEntrega").click(); 
            });
            return;
        }
        if (!$scope.pedido.desVeiculo){
            modalService.error('É obrigatório informar a descrição do veículo que fará a entrega!.', function(){
                document.getElementById("showDadosEntrega").click(); 
            });
            return;
        }
        $http.post(utilsService.apiProtectedUrl + '/pedidos/enviarparaentrega/' + $scope.pedido.codPedido , {
            nomEntregador: $scope.pedido.nomEntregador,
            desVeiculo: $scope.pedido.desVeiculo,
            codUsuario: $scope.pedido.codUsuario,
            codPedido: $scope.pedido.codPedido,
            codEstabelecimento: $scope.pedido.codEstabelecimento
        }).then(function(res){
            if (res.status == 201){
                $scope.pedido.indStatus = 'S';
                modalService.success('Status do pedido alterado com sucesso. O cliente será notificado que o pedido saiu para entrega.');
            } else {
                modalService.error(res.status + JSON.stringify(res.data));   
            }
        }, function(res){
           modalService.error(res.status + JSON.stringify(res.data));
        });         
    };


    $scope.calculaValorTotalSemFrete = function(pedido){
        var i = pedido.appPedido.itens.length
          , somaValItens = 0;
        while (i--){
            somaValItens += pedido.appPedido.itens[i].valTotal;
        }
        return somaValItens;
    };


    $scope.calculaValorTotalComFrete = function(pedido){
        return $scope.calculaValorTotalSemFrete(pedido) + pedido.appPedido.valEntrega;
    };

    $scope.calculaValorTotalComFreteReais = function(pedido){
        return formatReal($scope.calculaValorTotalComFrete(pedido));
    };

    $scope.getDesIndStatusPedido = pedidoService.getDesIndStatusPedido;

    for (var i in $scope.pedidos){
        if ($scope.pedidos[i].codPedido == $routeParams.codPedido){
            $scope.pedido = $scope.pedidos[i];
            break;
        }
    }

    if ($scope.pedido.indStatus == 'N'){

            $http.post(utilsService.apiProtectedUrl + '/pedidos/conhecer/' + $scope.pedido.codPedido , '').then(function(res){
                if (res.status == 201){
                    $scope.pedido.indStatus = 'R';
                } else {
                    modalService.error(res.status + JSON.stringify(res.data));   
                }
            }, function(res){
               modalService.error(res.status + JSON.stringify(res.data));
           }); 
    }

}]);

})();
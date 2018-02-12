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

.controller('listPedidosController', ['$scope', '$http', '$location', '$routeParams', '$window', 'estabelecimentoService', 'utilsService', 'pedidoService', 'modalService', function($scope, $http, $location, $routeParams, $window, estabelecimentoService, utilsService, pedidoService, modalService){
    
    $scope.editPedido = function(pedido){
            $location.url('/editPedido/' + pedido.codPedido);
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

    $scope.codEstabelecimento = $routeParams.codEstabelecimento;

    $http.get(utilsService.apiProtectedUrl + '/pedidos/' + $routeParams.codEstabelecimento).then(function(res){
        pedidoService.pedidos = res.data;
        $scope.pedidos = pedidoService.pedidos;
    }, function(res){
        //alert(JSON.stringify(res.data));
        modalService.error(JSON.stringify(res.data));
    });

    $scope.$watch("indStatusEstabelecimento", function(newVale, oldValue){
            if (newVale) {
                $http.get(utilsService.apiProtectedUrl + '/estabelecimentos/' + $scope.codEstabelecimento).then(function(res){
                    
                    $scope.estabelecimento = res.data;
                    if ($scope.estabelecimento.indStatus != newVale){
                        $scope.estabelecimento.indStatus = newVale;
                        $http.put(utilsService.apiProtectedUrl + '/estabelecimentos/' + $scope.codEstabelecimento, $scope.estabelecimento).then(function(res){
                            if (res.status == 204){
                                //$location.path('/listEstabelecimentos');
                            } else {
                                modalService.error(res.status + JSON.stringify(res.data));   
                            }
                        }, function(res){
                           modalService.error(res.status + JSON.stringify(res.data));
                       });
                    }


                }, function(res){
                    modalService.error(JSON.stringify(res.data));;
                });   
        }
    });
    
    $scope.listenPedidos = function(){
        var webSocket = new WebSocket(utilsService.webSocketUrl + "/pedidos?codEstabelecimento=" + $scope.codEstabelecimento);
        webSocket.onmessage = function (msg) { 
            //$window.alert(msg.data)
            var json = JSON.parse(msg.data);
            //$window.alert($scope.pedidos.length)
            $scope.pedidos.push(json);
            //pedidoService.pedidos.push(json);
            //$scope.pedidos = pedidoService.pedidos;
            //$window.alert($scope.pedidos.length)
            $scope.$apply();
        };
        webSocket.onclose = function () { $scope.listenPedidos(); };
    };

    $scope.listenPedidos();


    $http.get(utilsService.apiProtectedUrl + '/estabelecimentos/' + $scope.codEstabelecimento).then(function(res){
        $scope.estabelecimento = res.data;
        $scope.indStatusEstabelecimento = $scope.estabelecimento.indStatus;
    }, function(res){
        modalService.error(JSON.stringify(res.data));
    });   

}]);

})();
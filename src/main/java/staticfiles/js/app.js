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

angular.module("jFood", ["ngRoute", "jFoodControllers"])

.config(["$routeProvider", '$httpProvider', function($routeProvider, $httpProvider){

	$routeProvider.when("/home", {
        templateUrl: "../partials/home.html",
        controller: "homeController"
    })

    // Pedidos
    .when("/listPedidos/:codEstabelecimento", {
        templateUrl: "../partials/list-pedidos.html",
        controller: "listPedidosController"
    })
    .when("/editPedido/:codPedido", {
        templateUrl: "../partials/edit-pedido.html",
        controller: "editPedidoController"
    })

    // Estabelecimento
    .when("/listEstabelecimentos", {
		templateUrl: "../partials/list-estabelecimentos.html",
		controller: "listEstabelecimentosController"
	})
    .when("/editEstabelecimento", {
		templateUrl: "../partials/edit-estabelecimento.html",
		controller: "editEstabelecimentoController"
	})
    .when("/editEstabelecimento/:codEstabelecimento", {
		templateUrl: "../partials/edit-estabelecimento.html",
		controller: "editEstabelecimentoController"
    })

    // Cardápio    
	.when("/listCardapios/:codEstabelecimento", {
        templateUrl: "../partials/list-cardapios.html",
        controller: "listCardapiosController"
    })
    .when("/editCardapio/:codEstabelecimento", {
        templateUrl: "../partials/edit-cardapio.html",
        controller: "editCardapioController"
    })
    .when("/editCardapio/:codEstabelecimento/:codCardapio", {
        templateUrl: "../partials/edit-cardapio.html",
        controller: "editCardapioController"
    })

    //  Tamanho
    .when("/listTamanhos/:codEstabelecimento", {
        templateUrl: "../partials/list-tamanhos.html",
        controller: "listTamanhosController"
    })
    .when("/editTamanho/:codEstabelecimento", {
        templateUrl: "../partials/edit-tamanho.html",
        controller: "editTamanhoController"
    })
    .when("/editTamanho/:codEstabelecimento/:codTamanho", {
        templateUrl: "../partials/edit-tamanho.html",
        controller: "editTamanhoController"
    })

    //  Sabor
    .when("/listSabores/:codEstabelecimento", {
        templateUrl: "../partials/list-sabores.html",
        controller: "listSaboresController"
    })
    .when("/editSabor/:codEstabelecimento", {
        templateUrl: "../partials/edit-sabor.html",
        controller: "editSaborController"
    })
    .when("/editSabor/:codEstabelecimento/:codSabor", {
        templateUrl: "../partials/edit-sabor.html",
        controller: "editSaborController"
    })

    // Parceiro
    .when("/editParceiro", {
        templateUrl: "../partials/edit-parceiro.html",
        controller: "editParceiroController"
    })
    .when("/editParceiro/:codParceiro", {
        templateUrl: "../partials/edit-parceiro.html",
        controller: "editParceiroController"
    })
    .when("/listParceiros", {
        templateUrl: "../partials/list-parceiro.html",
        controller: "listParceirosController"
    })

    // Itens do Cardápio
    .when("/searchItensCardapio", {
        templateUrl: "../partials/search-item-cardapio.html",
        controller: "searchItensCardapioController"
    })
    .when("/listItensCardapio/:codEstabelecimento/:codCardapio", {
        templateUrl: "../partials/list-itens-cardapio.html",
        controller: "listItensCardapioController"
    })
    .when("/editItemCardapio/:codEstabelecimento/:codCardapio/:codItemCardapio", {
        templateUrl: "../partials/edit-item-cardapio.html",
        controller: "editItemCardapioController"
    })
    .when("/editItemCardapio/:codEstabelecimento/:codCardapio", {
        templateUrl: "../partials/edit-item-cardapio.html",
        controller: "editItemCardapioController"
    })

    // preços
    .when("/listPrecosItemCardapio/:codEstabelecimento/:codCardapio/:codItemCardapio", {
        templateUrl: "../partials/list-precos.html",
        controller: "listPrecosItemCardapioController"
    })
    .when("/editPreco/:codEstabelecimento/:codCardapio/:codItemCardapio/:codPrecoItemCardapio", {
        templateUrl: "../partials/edit-preco.html",
        controller: "editPrecoController"
    })
    .when("/editPreco/:codEstabelecimento/:codCardapio/:codItemCardapio", {
        templateUrl: "../partials/edit-preco.html",
        controller: "editPrecoController"
    })

    // Formas de Pagamento
    .when("/listFormasPgto/:codEstabelecimento", {
        templateUrl: "../partials/list-formas-pgto.html",
        controller: "listFormasPgtoController"
    })
    .when("/editFormaPgto/:codEstabelecimento", {
        templateUrl: "../partials/edit-forma-pgto.html",
        controller: "editFormaPgtoController"
    })
    .when("/editFormaPgto/:codEstabelecimento/:codFormaPgto", {
        templateUrl: "../partials/edit-forma-pgto.html",
        controller: "editFormaPgtoController"
    })

    // Ordens de Entrada
    .when("/listOrdensEntrada/:codEstabelecimento", {
        templateUrl: "../partials/list-ordens-entrada.html",
        controller: "listOrdensEntradaController"
    })
    .when("/editOrdemEntrada/:codEstabelecimento", {
        templateUrl: "../partials/edit-ordem-entrada.html",
        controller: "editOrdemEntradaController"
    })
    .when("/editOrdemEntrada/:codEstabelecimento/:codFormaPgto", {
        templateUrl: "../partials/edit-ordem-entrada.html",
        controller: "editOrdemEntradaController"
    })

    // :O
    .otherwise({
		redirectTo: "/home"
	});

    $httpProvider.interceptors.push(['$q', '$location', '$localStorage', function($q, $location, $localStorage) {
        return {
            'request': function (config) {
                config.headers = config.headers || {};
                if ($localStorage.token) {
                    var token = $localStorage.token.split('.')[0];
                    config.headers.Authorization = 'Bearer ' + token;
                }
                return config;
            },
            'responseError': function(response) {
                if(response.status === 401 || response.status === 403) {
                    $location.path('/signin');
                }
                return $q.reject(response);
            }
        };
    }]);

}]);

})();
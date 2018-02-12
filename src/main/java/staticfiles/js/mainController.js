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

    .controller('mainController', ['$rootScope', '$scope', '$location', '$localStorage', '$base64', '$http', '$window', 'Main', 'utilsService', 'modalService', 'estabelecimentoService', function($rootScope, $scope, $location, $localStorage, $base64, $http, $window, Main, utilsService, modalService, estabelecimentoService) {

        $scope.menus = [ { name: 'Pedidos'
                         , link: '/listPedidos/:codEstabelecimento'
                         , icon: 'motorcycle'},
                         { name: 'Estabelecimentos'
                         , link: '/listEstabelecimentos'
                         , icon: 'store'},
                         { name: 'Cardápios'
                         , link: '/listCardapios/:codEstabelecimento' 
                         , icon: 'local_restaurant'},
                         { name: 'Itens do cardapio'
                         , link: '/searchItensCardapio'
                         , icon: 'favorite_border'},
                         { name: 'Tamanhos'
                         , link: '/listTamanhos/:codEstabelecimento'
                         , icon: 'format_size'},
                         { name: 'Sabores'
                         , link: '/listSabores/:codEstabelecimento'
                         , icon: 'local_pizza'},
                         { name: 'Formas de Pagamento'
                         , link: '/listFormasPgto/:codEstabelecimento'
                         , icon: 'credit_card'},
                         { name: 'Meu Cadastro'
                         , link: '#/listParceiros'
                         , icon: 'person'},
                         { name: 'Ordens de Entrada'
                         , link: '/listOrdensEntrada/:codEstabelecimento'
                         , icon: 'credit_cardo'}];
        $scope.userName = '';
        $rootScope.loggedIn = false;
        $scope.signin = function() {
            var formData = {
                email: $scope.email,
                password: md5($scope.password)
            };

            formData = $base64.encode(JSON.stringify(formData));

            Main.signin(formData, function(res) {
              res = res.data;
                if (res.type == false) {
                   $rootScope.loggedIn = false;
                   alert(res.data)    
               } else {
                $scope.userName = res.data.email;
                $rootScope.loggedIn = true;
                $localStorage.token = res.token;
                $localStorage.email = $scope.userName;
                if ($rootScope.loggedIn){
                      $http.get(utilsService.apiProtectedUrl + '/estabelecimentos').then(function(res){
                          $scope.estabelecimentos = res.data;
                          if ($scope.estabelecimentos[0]){
                              $scope.estabelecimento = $scope.estabelecimentos[0];
                              $location.path('/listEstabelecimentos');
                          }
                      }, function(res){
                          alert(JSON.stringify(res.data));
                      });
                  }
            }
        }, function() {
           $rootScope.loggedIn = false;
           $rootScope.error = 'Failed to signin';
        });
            $scope.password = '';
        };

        $scope.signup = function() {
            var formData = {
                email: $scope.email,
                password: $scope.password
            }

            Main.save(formData, function(res) {
              res = res.data;
                if (res.type == false) {
                    alert(res.data)
                } else {
                    $localStorage.token = res.data.token;
                    $location.path('/home');
                }
            }, function() {
                $rootScope.error = 'Failed to signup';
            })
        };

        $scope.me = function() {
            Main.me(function(res) {
              res = res.data;
                $scope.myDetails = res;
            }, function() {
                $rootScope.error = 'Failed to fetch details';
            })
        };

        $scope.logout = function() {
            Main.logout(function() {
               $rootScope.loggedIn = false;
               $location.path('/home');
               delete $localStorage.email;
               delete $scope.email;
           }, function() {
            alert('Failed to logout!');
        });
        };

        $scope.token = $localStorage.token;
        $scope.userName = $localStorage.email;
        if ($scope.userName){
           $rootScope.loggedIn = true;
       }

        $scope.showMenuPage = function(link) {
              link = link.replace(':codEstabelecimento', $scope.estabelecimento.codEstabelecimento);
              $location.path(link);
              $window.scrollTo(0, 0);
        };


        $scope.modal = modalService;

        $scope.$watch("estabelecimento", function(newVale, oldValue){
            if (newVale) {
                estabelecimentoService.current = $scope.estabelecimento;    
            }
        });

/*
  $scope.toggleSidenav = function(menuId) {
    $mdSidenav(menuId).toggle();
  };


    $scope.showListBottomSheet = function($event) {
    $scope.alert = '';
    $mdBottomSheet.show({
      template: '<md-bottom-sheet class="md-list md-has-header"> <md-subheader>Settings</md-subheader> <md-list> <md-item ng-repeat="item in items"><md-item-content md-ink-ripple flex class="inset"> <a flex aria-label="{{item.name}}" ng-click="listItemClick($index)"> <span class="md-inline-list-icon-label">{{ item.name }}</span> </a></md-item-content> </md-item> </md-list></md-bottom-sheet>',
      controller: 'ListBottomSheetCtrl',
      targetEvent: $event
    }).then(function(clickedItem) {
      $scope.alert = clickedItem.name + ' clicked!';
    });
  };
  
  $scope.showAdd = function(ev) {
    $mdDialog.show({
      controller: DialogController,
      template: '<md-dialog aria-label="Mango (Fruit)"> <md-content class="md-padding"> <form name="userForm"> <div layout layout-sm="column"> <md-input-container flex> <label>First Name</label> <input ng-model="user.firstName" placeholder="Placeholder text"> </md-input-container> <md-input-container flex> <label>Last Name</label> <input ng-model="theMax"> </md-input-container> </div> <md-input-container flex> <label>Address</label> <input ng-model="user.address"> </md-input-container> <div layout layout-sm="column"> <md-input-container flex> <label>City</label> <input ng-model="user.city"> </md-input-container> <md-input-container flex> <label>State</label> <input ng-model="user.state"> </md-input-container> <md-input-container flex> <label>Postal Code</label> <input ng-model="user.postalCode"> </md-input-container> </div> <md-input-container flex> <label>Biography</label> <textarea ng-model="user.biography" columns="1" md-maxlength="150"></textarea> </md-input-container> </form> </md-content> <div class="md-actions" layout="row"> <span flex></span> <md-button ng-click="answer(\'not useful\')"> Cancel </md-button> <md-button ng-click="answer(\'useful\')" class="md-primary"> Save </md-button> </div></md-dialog>',
      targetEvent: ev,
    })
    .then(function(answer) {
      $scope.alert = 'You said the information was "' + answer + '".';
    }, function() {
      $scope.alert = 'You cancelled the dialog.';
    });
  };
*/

    if ($rootScope.loggedIn){
        $http.get(utilsService.apiProtectedUrl + '/estabelecimentos').then(function(res){
            $scope.estabelecimentos = res.data;
            if ($scope.estabelecimentos[0]){
                $scope.estabelecimento = $scope.estabelecimentos[0];
            }
        }, function(res){
            alert(JSON.stringify(res.data));
        });
    }


   }]);

})();
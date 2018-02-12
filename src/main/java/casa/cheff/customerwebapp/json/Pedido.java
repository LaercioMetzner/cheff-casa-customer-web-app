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
package casa.cheff.customerwebapp.json;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import casa.cheff.customerwebapp.json.EstabelecimentoDetail.Sabor;
import casa.cheff.customerwebapp.json.EstabelecimentoDetail.Tamanho;
import casa.cheff.customerwebapp.json.EstabelecimentoDetail.Cardapio.ItemCardapio;

public class Pedido {

	public static class FormaPgto {
		public int codFormaPgto;
		public String nomFormaPgto;
	}
	
	public static class AppItemPedido {
		public int qtdPedido;
		public float valUnitario, valTotal;
		public ItemCardapio item;
		public Tamanho tamanho;
		public List<Sabor> sabores = new ArrayList<>();
	}

	public static class AppPedido {
		public FacebookUserInfo fbUser;
		public DadosEntregaPedido dadosEntrega;
		public int codPedido, codEstabelecimento;
		public String phoneNumber, enderecoEntrega, status, observacao;

	    public float valEntrega;
		// public EstabelecimentoDetail estabelecimentoDetail;
		public List<AppItemPedido> itens = new ArrayList<>();
		public FormaPgto formaPgto;
		
		public Date datPedido;
	}

	public int codPedido, codUsuario, codEstabelecimento;
	public String indStatus, indAvaliacao, obsAvaliacao, obsCancelamento,
			objDadosPedido, nomEntregador, desVeiculo;
	public Date datPedido, datConhecimento, datSaidaEntrega, datConfEntrega,
			datAvaliacao, datCancelamento;
	public AppPedido  appPedido;

}

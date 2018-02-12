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
import java.util.List;

import casa.cheff.customerwebapp.bean.db.Estabelecimento;

public class EstabelecimentoDetail {

	public int codEstabelecimento;
	public String nomEstabelecimento, desEstabelecimento, numFone, desEndereco, desComplemento, desBairro, nomCidade,
			urlFacebook, urlInstagram, urlTwitter, urlPinterest, urlWebsite, numWhatsapp, desSourceLogotipo,
			desSourceCapa, indStatus;
	public float latEstabelecimento, lonEstabelecimento, valEntrega;
	public List<Cardapio> cardapios = new ArrayList<Cardapio>();
	public List<FormaPgto> formasPgto = new ArrayList<FormaPgto>();

	public EstabelecimentoDetail(Estabelecimento e) {
		this();
		codEstabelecimento = e.codEstabelecimento;
		nomEstabelecimento = e.nomEstabelecimento;
		desEstabelecimento = e.desEstabelecimento;
		numFone = e.numFone1;
		desEndereco = e.desEndereco;
		desComplemento = e.desComplemento;
		desBairro = e.desBairro;
		nomCidade = e.nomCidade;
		urlFacebook = e.urlFacebook;
		urlInstagram = e.urlInstagram;
		urlTwitter = e.urlTwitter;
		urlPinterest = e.urlPinterest;
		urlWebsite = e.urlWebsite;
		numWhatsapp = e.numWhatsapp;
		desSourceCapa = e.desSourceCapa;
		desSourceLogotipo = e.desSourceLogotipo;

		valEntrega = e.valEntrega;
		indStatus = e.indStatus;
	}

	public EstabelecimentoDetail() {
		super();
	}

	public class Cardapio {

		public int codCardapio, numOrdem;
		public String nomCardapio;
		public List<ItemCardapio> itens = new ArrayList<ItemCardapio>();

		public class ItemCardapio {

			public int codItemCardapio, numOrdem;
			public String nomItemCardapio, desItemCardapio, desSourcePhoto;
			public List<PrecoItemCardapio> precos = new ArrayList<PrecoItemCardapio>();

			public class PrecoItemCardapio {

				public int codPreco, qtdSabor;
				public Sabor sabor;
				public Tamanho tamanho;
				public float valPreco;
				public String codLegado, codBarra;

			}
		}
	}

	public static class FormaPgto {
		public int codFormaPgto;
		public String nomFormaPgto;
	}

	public static class Sabor {
		public int codSabor;
		public String nomSabor, desSabor;
	}

	public static class Tamanho {
		public int codTamanho;
		public String nomTamanho, desTamanho;
	}
}
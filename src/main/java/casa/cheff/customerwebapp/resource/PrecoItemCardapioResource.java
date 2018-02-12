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
package casa.cheff.customerwebapp.resource;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;

import java.util.List;

import casa.cheff.customerwebapp.Main;
import casa.cheff.customerwebapp.bean.db.ItemCardapio;
import casa.cheff.customerwebapp.bean.db.ParceiroAuth;
import casa.cheff.customerwebapp.bean.db.PrecoItemCardapio;
import casa.cheff.customerwebapp.dao.ItemCardapioDAO;
import casa.cheff.customerwebapp.dao.PrecoItemCardapioDAO;

import com.google.gson.Gson;

public class PrecoItemCardapioResource implements Resource {

	@Override
	public void addRoutes() {

		get(Main.API_PROTECTED + "/cardapios/:codEstabelecimento/:codCardapio/itens/:codItemCardapio/precos",
				(req, res) -> {
					ParceiroAuth parceiro = (ParceiroAuth) req
							.attribute(Main.PARCEIRO_ATRIBUTE);
					res.type(Main.APPLICATION_TYPE_JSON_UTF8);
					List<PrecoItemCardapio> precosItemCardapio = null;
					int codEstabelecimento = Integer.valueOf(req
							.params(":codEstabelecimento"));
					int codCardapio = Integer.valueOf(req
							.params(":codCardapio"));
					int codItemCardapio = Integer.valueOf(req
							.params(":codItemCardapio"));
					precosItemCardapio = new PrecoItemCardapioDAO()
							.getPrecostItemCardapio(codEstabelecimento, codCardapio, codItemCardapio, parceiro); 
					res.status(200);
					return precosItemCardapio;
				}, new Gson()::toJson);

		post(Main.API_PROTECTED + "/cardapios/:codEstabelecimento/:codCardapio/itens/:codItemCardapio/precos",
				(req, res) -> {
					try {
						PrecoItemCardapio precoItemCardapio = new Gson().fromJson(req.body(),
								PrecoItemCardapio.class);
						int codPrecoItemCardapio = new PrecoItemCardapioDAO()
								.insertPrecoItemCardapio(precoItemCardapio);
						res.status(201);
						res.header("codPreco", Integer.toString(codPrecoItemCardapio));
					} catch (Exception e) {
						throw new RuntimeException(
								"Ocorreu um erro ao processar a requisição POST para /precos: "
										+ e.getMessage());
					}
					return "";
				});

		// TODO: change everything
		put(Main.API_PROTECTED + "/cardapios/:codEstabelecimento/:codCardapio/itens/:codItemCardapio/precos/:codPreco",
				(req, res) -> {
					ParceiroAuth parceiro = (ParceiroAuth) req
							.attribute(Main.PARCEIRO_ATRIBUTE);
					try {
						String body = req.body();
						PrecoItemCardapio precoItemCardapio = new Gson().fromJson(body,
								PrecoItemCardapio.class);
						if (!req.params(":codPreco").equals(
								String.valueOf(precoItemCardapio.codPreco))) {
							throw new Exception(
									"Não é o mesmo preço a ser editado !!");
						}
						int qtyUpdated = new PrecoItemCardapioDAO()
								.updatePrecoItemCardapio(precoItemCardapio, parceiro);
						if (qtyUpdated > 0) {
							res.status(204);
						} else {
							res.status(404);
						}
					} catch (Exception e) {
						throw new RuntimeException(
								"Ocorreu um erro ao processar a requisição PUT para /cardapios/X/Y/itens/Z/precos/W: "
										+ e.getMessage());
					}
					return "";
				});

	}

}
	
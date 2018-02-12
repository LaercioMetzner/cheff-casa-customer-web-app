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
import casa.cheff.customerwebapp.bean.db.Cardapio;
import casa.cheff.customerwebapp.bean.db.ItemCardapio;
import casa.cheff.customerwebapp.bean.db.ParceiroAuth;
import casa.cheff.customerwebapp.dao.ItemCardapioDAO;

import com.google.gson.Gson;

public class ItemCardapioResource implements Resource {

	@Override
	public void addRoutes() {

		get(Main.API_PROTECTED + "/cardapios/:codEstabelecimento/:codCardapio/itens",
				(req, res) -> {
					ParceiroAuth parceiro = (ParceiroAuth) req
							.attribute(Main.PARCEIRO_ATRIBUTE);
					res.type(Main.APPLICATION_TYPE_JSON_UTF8);
					List<ItemCardapio> itensCardapio = null;
					int codEstabelecimento = Integer.valueOf(req
							.params(":codEstabelecimento"));
					int codCardapio = Integer.valueOf(req
							.params(":codCardapio"));
					itensCardapio = new ItemCardapioDAO()
							.getItensCardapio(codEstabelecimento, codCardapio , parceiro);
					res.status(200);
					return itensCardapio;
				}, new Gson()::toJson);

		post(Main.API_PROTECTED + "/cardapios/:codEstabelecimento/:codCardapio/itens",
				(req, res) -> {
					try {
						ItemCardapio itemCardapio = new Gson().fromJson(req.body(),
								ItemCardapio.class);
						int codItemCardapio = new ItemCardapioDAO()
								.insertItemCardapio(itemCardapio);
						res.status(201);
						res.header("codCardapio", Integer.toString(codItemCardapio));
					} catch (Exception e) {
						throw new RuntimeException(
								"Ocorreu um erro ao processar a requisição POST para /cardapios/X/Y/itens: "
										+ e.getMessage());
					}
					return "";
				});

		put(Main.API_PROTECTED + "/cardapios/:codEstabelecimento/:codCardapio/itens/:codItem",
				(req, res) -> {
					ParceiroAuth parceiro = (ParceiroAuth) req
							.attribute(Main.PARCEIRO_ATRIBUTE);
					try {
						String body = req.body();
						ItemCardapio itemCardapio = new Gson().fromJson(req.body(),
								ItemCardapio.class);
						if (!req.params(":codCardapio").equals(
								String.valueOf(itemCardapio.codCardapio))) {
							throw new Exception(
									"Não é o mesmo item a ser editado !!");
						}
						if (!req.params(":codItem").equals(
								String.valueOf(itemCardapio.codItemCardapio))) {
							throw new Exception(
									"Não é o mesmo item a ser editado !!");
						}
						int qtyUpdated = new ItemCardapioDAO()
								.updateItemCardapio(itemCardapio, parceiro);
						if (qtyUpdated > 0) {
							res.status(204);
						} else {
							res.status(404);
						}
					} catch (Exception e) {
						throw new RuntimeException(
								"Ocorreu um erro ao processar a requisição PUT para /cardapios/X/Y/itens: "
										+ e.getMessage());
					}
					return "";
				});

	}

}

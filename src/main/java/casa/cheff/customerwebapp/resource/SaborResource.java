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
import casa.cheff.customerwebapp.bean.db.ParceiroAuth;
import casa.cheff.customerwebapp.bean.db.Sabor;
import casa.cheff.customerwebapp.dao.SaborDAO;

import com.google.gson.Gson;

public class SaborResource implements Resource {

	@Override
	public void addRoutes() {

		get(Main.API_PROTECTED + "/sabores/:codestabelecimento",
				(req, res) -> {
					res.type(Main.APPLICATION_TYPE_JSON_UTF8);
					ParceiroAuth parceiro = (ParceiroAuth) req
							.attribute(Main.PARCEIRO_ATRIBUTE);
					List<Sabor> sabores = null;
					int codEstabelecimento = Integer.valueOf(req
							.params(":codestabelecimento"));
					sabores = new SaborDAO().getSabores(codEstabelecimento,
							parceiro);
					res.status(200);
					return sabores;
				}, new Gson()::toJson);

		post(Main.API_PROTECTED + "/sabores", (req, res) -> {
			try {
				Sabor sabor = new Gson().fromJson(req.body(), Sabor.class);
				int codSabor = new SaborDAO().insertSabor(sabor);
				res.status(201);
				res.header("codSabor", Integer.toString(codSabor));
			} catch (Exception e) {
				throw new RuntimeException(
						"Ocorreu um erro ao processar a requisição POST para /sabores: "
								+ e.getMessage());
			}
			return "";
		});

		put(Main.API_PROTECTED + "/sabores/:codsabor",
				(req, res) -> {
					try {
						ParceiroAuth parceiro = (ParceiroAuth) req
								.attribute(Main.PARCEIRO_ATRIBUTE);
						Sabor sabor = new Gson().fromJson(req.body(),
								Sabor.class);
						if (!req.params(":codsabor").equals(
								String.valueOf(sabor.codSabor))) {
							throw new Exception(
									"Não é o mesmo sabor a ser editado !!");
						}
						int qtyUpdated = new SaborDAO().updateSabor(sabor,
								parceiro);
						if (qtyUpdated > 0) {
							res.status(204);
						} else {
							res.status(404);
						}
					} catch (Exception e) {
						throw new RuntimeException(
								"Ocorreu um erro ao processar a requisição PUT para /sabores: "
										+ e.getMessage());
					}
					return "";
				});

	}

}
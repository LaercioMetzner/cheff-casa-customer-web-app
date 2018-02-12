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

import java.util.Base64;
import java.util.List;

import casa.cheff.customerwebapp.Main;
import casa.cheff.customerwebapp.bean.db.Parceiro;
import casa.cheff.customerwebapp.bean.db.ParceiroAuth;
import casa.cheff.customerwebapp.dao.ParceiroDAO;

import com.google.gson.Gson;

public class ParceiroResource implements Resource {

	@Override
	public void addRoutes() {

		get(Main.API_PROTECTED + "/parceiros",
				(req, res) -> {
					res.type(Main.APPLICATION_TYPE_JSON_UTF8);
					Parceiro parceiro = (Parceiro) req
							.attribute(Main.PARCEIRO_ATRIBUTE);
					List<Parceiro> parceiros = new ParceiroDAO()
							.getParceirosByToken(parceiro.codParceiro);
					res.status(200);
					return parceiros;
				}, new Gson()::toJson);

		post(Main.API_PUBLIC + "/parceiros",
				(req, res) -> {
					try {
						String json = new String(Base64.getDecoder().decode(
								req.body()));
						Parceiro Parceiro = new Gson().fromJson(json,
								Parceiro.class);
						int codParceiro = new ParceiroDAO()
								.insertParceiro(Parceiro);
						res.status(201);
						res.header("codCardapio", Integer.toString(codParceiro));
					} catch (Exception e) {
						throw new RuntimeException(
								"Ocorreu um erro ao processar a requisição POST para /parceiros: "
										+ e.getMessage());
					}
					return "";
				});

		put(Main.API_PROTECTED + "/parceiros/:codparceiro",
				(req, res) -> {
					ParceiroDAO parceiroDAO = new ParceiroDAO();
					try {
						int codParceiro = Integer.parseInt(req
								.params(":codparceiro"));
						String json = new String(Base64.getDecoder().decode(
								req.body()));
						Parceiro parceiro = new Gson().fromJson(json,
								Parceiro.class);
						ParceiroAuth parceiroLogado = (ParceiroAuth) req
								.attribute(Main.PARCEIRO_ATRIBUTE);
						if (codParceiro != parceiro.codParceiro) {
							if (parceiroLogado.indAdmin == null | !"S".equals(parceiroLogado.indAdmin)) {
								throw new Exception("Não é possível editar !!");
							}
						}
						int qtyUpdated = parceiroDAO.updateParceiro(parceiro);
						if (qtyUpdated > 0) {
							res.status(204);
						} else {
							res.status(404);
						}
					} catch (Exception e) {
						throw new RuntimeException(
								"Ocorreu um erro ao processar a requisição PUT para /codparceiro: "
										+ e.getMessage());
					}
					return "";
				});
	}
}

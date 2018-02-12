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

import static spark.Spark.before;
import static spark.Spark.get;
import static spark.Spark.halt;
import static spark.Spark.post;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.jetty.util.security.Credential.MD5;

import com.google.gson.Gson;

import casa.cheff.customerwebapp.Main;
import casa.cheff.customerwebapp.bean.db.ParceiroAuth;
import casa.cheff.customerwebapp.bean.db.Usuario;
import casa.cheff.customerwebapp.dao.PedidoDAO;
import casa.cheff.customerwebapp.dao.UsuarioDAO;
import casa.cheff.customerwebapp.json.DadosEntregaPedido;
import casa.cheff.customerwebapp.json.FacebookUserInfo;
import casa.cheff.customerwebapp.json.Pedido;
import casa.cheff.customerwebapp.json.Pedido.AppPedido;
import casa.cheff.customerwebapp.websockets.AppStatusPedidosWebSocket;
import casa.cheff.customerwebapp.websockets.PedidosWebSocket;
import spark.Request;
import spark.Response;

public class PedidoResource implements Resource {

	private void validateUserByToken(Request req, Response res) {
		String token = req.headers("Authorization");
		token = token == null ? "" : token.replace("Bearer ", "");
		if ("".equals(token)) {
			halt(401, "Não autenticado.");
		}
		String pedidoJson = req.body();
		Pedido.AppPedido appPedido = new Gson().fromJson(pedidoJson, Pedido.AppPedido.class);
		FacebookUserInfo fbUser = appPedido.fbUser;
		if (fbUser == null) {
			halt(401, "Não autenticado no FB.");
		}
		String mToken = MD5.digest("jfood:)" + fbUser.id);
		mToken = mToken.replace("MD5:", "");

		if (!token.equals(mToken)) {
			halt(401, "Não autorizado.");
		}

		UsuarioDAO usuarioDAO = new UsuarioDAO();
		Usuario usuario = usuarioDAO.getUsuarioByFbId(fbUser.id);
		if (usuario == null) {
			usuario = new Usuario();
			usuario.desFacebookId = fbUser.id;
			usuario.nomUsuario = fbUser.name;
			usuario.datCadastro = new Date();
			usuario.numFone = appPedido.phoneNumber;
			usuario.indAtivo = "S";
			usuario.codUsuario = usuarioDAO.insertUsuario(usuario);
		}
		req.attribute(Main.FB_USER_ID_ATRIBUTE, usuario);
	}

	public void addRoutes() {

		before(Main.API_PUBLIC + "/pedidos", (req, res) -> validateUserByToken(req, res));
		before(Main.API_PUBLIC + "/pedidos/fechar/:codPedido", (req, res) -> validateUserByToken(req, res));

		post(Main.API_PUBLIC + "/pedidos", (req, res) -> {
			try {
				String pedidoJson = req.body();
				Gson gson = new Gson();
				Pedido.AppPedido appPedido = gson.fromJson(pedidoJson, Pedido.AppPedido.class);
				Usuario usuario = req.attribute(Main.FB_USER_ID_ATRIBUTE);
				Pedido pedido = new Pedido();
				pedido.codEstabelecimento = appPedido.codEstabelecimento;
				pedido.codUsuario = usuario.codUsuario;
				pedido.indStatus = appPedido.status;
				pedido.datPedido = new Date();
				pedido.objDadosPedido = pedidoJson;
				pedido.codPedido = new PedidoDAO().insertPedido(pedido);
				pedido.appPedido = appPedido;
				pedido.objDadosPedido = null;
				String pedidoJsonObject = gson.toJson(pedido, Pedido.class);
				PedidosWebSocket.broadcastPedido(pedido.codEstabelecimento, pedidoJsonObject);
				res.status(201);
				res.header("codPedido", Integer.toString(pedido.codPedido));
			} catch (Exception e) {
				throw new RuntimeException(
						"Ocorreu um erro ao processar a requisição POST para /pedidos: " + e.getStackTrace());
			}

			res.type(Main.APPLICATION_TYPE_JSON_UTF8);
			return "{}";
		});

		get(Main.API_PUBLIC + "/pedidos/:fbUserId", (req, res) -> {
			res.type(Main.APPLICATION_TYPE_JSON_UTF8);
			String strStartDate = "", strEndDate = "";
			try {
				strStartDate = req.queryMap("dini").value();
				strEndDate = req.queryMap("dfin").value();
			} catch (Exception e) {
			}
			SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
			Date startDate = null, endDate = null;
			try {
				startDate = sdf.parse(strStartDate);
			} catch (Exception e) {
			}
			try {
				endDate = sdf.parse(strEndDate);
			} catch (Exception e) {
			}
			List<Pedido> pedidos = null;
			String fbUserId = req.params(":fbUserId");
			pedidos = new PedidoDAO().getPedidosByFacebookUserIdBetweenDateRange(fbUserId, startDate, endDate);
			Gson gson = new Gson();
			List<AppPedido> appPedidos = new ArrayList<AppPedido>();
			for (Pedido pedido : pedidos) {
				AppPedido appPedido = gson.fromJson(pedido.objDadosPedido, Pedido.AppPedido.class);
				appPedido.status = pedido.indStatus;
				appPedido.codPedido = pedido.codPedido;
				appPedido.datPedido = pedido.datPedido;
				if (!"".equals(pedido.nomEntregador)){
					appPedido.dadosEntrega = new DadosEntregaPedido();
					appPedido.dadosEntrega.nomEntregador = pedido.nomEntregador;
					appPedido.dadosEntrega.desVeiculo = pedido.desVeiculo;
					
				}
				appPedidos.add(appPedido);
				pedido.objDadosPedido = null;
			}
			res.status(200);
			return appPedidos;
		}, new Gson()::toJson);

		get(Main.API_PROTECTED + "/pedidos/:codestabelecimento", (req, res) -> {
			res.type(Main.APPLICATION_TYPE_JSON_UTF8);
			ParceiroAuth parceiro = (ParceiroAuth) req.attribute(Main.PARCEIRO_ATRIBUTE);
			List<Pedido> pedidos = null;
			int codEstabelecimento = Integer.valueOf(req.params(":codestabelecimento"));
			pedidos = new PedidoDAO().getPedidos(codEstabelecimento, parceiro);
			Gson gson = new Gson();
			for (Pedido pedido : pedidos) {
				pedido.appPedido = gson.fromJson(pedido.objDadosPedido, Pedido.AppPedido.class);
				pedido.objDadosPedido = null;
			}
			res.status(200);
			return pedidos;
		}, new Gson()::toJson);

		post(Main.API_PUBLIC + "/pedidos/fechar/:codPedido", (req, res) -> {
			try {

				int codPedido = Integer.valueOf(req.params(":codPedido"));
				int qtdRowsAffected = new PedidoDAO().fecharPedido(codPedido);
				if (qtdRowsAffected == 1) {
					res.status(201);
					res.header("newStatus", "C");
				} else {
					throw new Exception(qtdRowsAffected + " rows were affected");
				}
			} catch (Exception e) {
				throw new RuntimeException("Ocorreu um erro ao fechar o pedido: " + e.getMessage());
			}
			return "{}";
		});

		post(Main.API_PROTECTED + "/pedidos/conhecer/:codPedido", (req, res) -> {
			try {

				int codPedido = Integer.valueOf(req.params(":codPedido"));
				int qtdRowsAffected = new PedidoDAO().conhecerPedido(codPedido);
				if (qtdRowsAffected == 1) {
					res.status(201);
					res.header("newStatus", "R");
				} else {
					throw new Exception(qtdRowsAffected + " rows were affected");
				}
			} catch (Exception e) {
				throw new RuntimeException("Ocorreu um erro ao conhecer o pedido: " + e.getMessage());
			}
			return "";
		});

		post(Main.API_PROTECTED + "/pedidos/cancelar/:codPedido", (req, res) -> {
			try {

				int codPedido = Integer.valueOf(req.params(":codPedido"));
				int qtdRowsAffected = new PedidoDAO().cancelarPedido(codPedido, null);
				if (qtdRowsAffected == 1) {
					res.status(201);
					res.header("newStatus", "X");
				} else {
					throw new Exception(qtdRowsAffected + " rows were affected");
				}
			} catch (Exception e) {
				throw new RuntimeException("Ocorreu um erro ao fechar o pedido: " + e.getMessage());
			}
			return "";
		});

		post(Main.API_PROTECTED + "/pedidos/enviarparaentrega/:codPedido", (req, res) -> {
			try {
				String body = req.body();

				Gson gson = new Gson();
				DadosEntregaPedido dadosEnregaPedido = gson.fromJson(body, DadosEntregaPedido.class);

				int codPedido = Integer.valueOf(req.params(":codPedido"));
				int qtdRowsAffected = new PedidoDAO().enviarPedidoEntrega(codPedido, dadosEnregaPedido.nomEntregador,
						dadosEnregaPedido.desVeiculo);
				if (qtdRowsAffected == 1) {
					res.status(201);
					res.header("newStatus", "S");

					AppStatusPedidosWebSocket.broadcastMsg(dadosEnregaPedido.codUsuario, body);
				} else {
					throw new Exception(qtdRowsAffected + " rows were affected");
				}
			} catch (Exception e) {
				throw new RuntimeException("Ocorreu um erro ao enviarparaentrega o pedido: " + e.getMessage());
			}
			return "";
		});

	}

}
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

import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.options;
import static spark.Spark.before;
import static spark.Spark.after;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.axis.Message;

import com.google.gson.Gson;

import casa.cheff.customerwebapp.Main;
import casa.cheff.customerwebapp.dao.GeneralPurposeDAO;
import casa.cheff.customerwebapp.json.ApiInfo;
import casa.cheff.customerwebapp.json.Msg;

public class GeneralResource implements Resource {

	@Override
	public void addRoutes() {

		// options("/*",
		// (request, response) -> {
		//
		// String accessControlRequestHeaders = request
		// .headers("Access-Control-Request-Headers");
		// if (accessControlRequestHeaders != null) {
		// response.header("Access-Control-Allow-Headers",
		// accessControlRequestHeaders);
		// }
		//
		// String accessControlRequestMethod = request
		// .headers("Access-Control-Request-Method");
		// if (accessControlRequestMethod != null) {
		// response.header("Access-Control-Allow-Methods",
		// accessControlRequestMethod);
		// }
		//
		// return "OK";
		// });

		before((request, response) -> {
			response.header("Access-Control-Allow-Origin", "*");
		});

		get("/", (req, res) -> {
			res.redirect("/index.html");
			return "";
		});

		get(Main.API_PUBLIC + "/info", (req, res) -> {
			res.status(200);
			res.type(Main.APPLICATION_TYPE_JSON_UTF8);
			ApiInfo info = new ApiInfo();
			info.version = Main.API_VERSION;
			info.deploy = Main.API_DEPLOY_NUMBER;
			return info;
		}, new Gson()::toJson);

		get(Main.API_PUBLIC + "/resetflickr", (req, res) -> {
			res.status(200);
			res.type(Main.APPLICATION_TYPE_JSON_UTF8);
			Msg info = new Msg();
			info.message = "success";
			try {
				new ImageResource();
			} catch (Exception e) {
				info.message = "Fodeu o upload de imagens... " + e.getMessage();
			}
			return info;
		}, new Gson()::toJson);

		exception(RuntimeException.class, (e, req, res) -> {
			res.status(400);
			res.type(Main.APPLICATION_TYPE_JSON_UTF8);
			Msg msg = new Msg();
			msg.message = "Erro: " + e.getMessage();
			res.body(new Gson().toJson(msg));
		});

	}
}

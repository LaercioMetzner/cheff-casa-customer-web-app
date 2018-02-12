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
package casa.cheff.customerwebapp;

import static spark.Spark.externalStaticFileLocation;
import static spark.Spark.port;
import static spark.Spark.staticFileLocation;
//import static spark.Spark.staticFiles;
import static spark.Spark.webSocket;

import java.io.File;

import casa.cheff.customerwebapp.db.ConnectionManager;
import casa.cheff.customerwebapp.resource.CardapioResource;
import casa.cheff.customerwebapp.resource.EstabelecimentoResource;
import casa.cheff.customerwebapp.resource.FormaPgtoResource;
import casa.cheff.customerwebapp.resource.GeneralResource;
import casa.cheff.customerwebapp.resource.ImageResource;
import casa.cheff.customerwebapp.resource.ItemCardapioResource;
import casa.cheff.customerwebapp.resource.ParceiroResource;
import casa.cheff.customerwebapp.resource.PedidoResource;
import casa.cheff.customerwebapp.resource.PrecoItemCardapioResource;
import casa.cheff.customerwebapp.resource.SaborResource;
import casa.cheff.customerwebapp.resource.SignInResource;
import casa.cheff.customerwebapp.resource.TamanhoResource;
import casa.cheff.customerwebapp.websockets.AppStatusPedidosWebSocket;
import casa.cheff.customerwebapp.websockets.PedidosWebSocket;

public class Main {

	// APP CONSTANTS
	public static final String API_VERSION = "1.1";
	public static final String API_DEPLOY_NUMBER = "0603AC01";
	public static final String PARCEIRO_ATRIBUTE = "parceiro";
	public static final String FB_USER_ID_ATRIBUTE = "fb_user_id";
	public static final String APPLICATION_TYPE_JSON_UTF8 = "application/json; charset=UTF-8";
	public static final String API_PUBLIC = "/api/public";
	public static final String API_PROTECTED = "/api/protected";
	public static final String STATIC_FILES_LOCATION_PROD = "/staticfiles";
	public static final String STATIC_FILES_LOCATION_DEV;
	public static final boolean IS_PRODUCTION_SERVER;

	// FLICKR API CONSTANTS
	public static final String FLICKR_API_KEY;
	public static final String FLICKR_SHARED_SECRET;
	public static final String FLICKR_AUTH_TOKEN;
	public static final String FLICKR_AUTH_TOKEN_SECRET;

	// GOOGLE PLACES API CONSTANTS
	public static final String PLACES_API_KEY;
	public static final String IMAGE_LOADER_API_KEY;

	// INFRASTRUCTURE CONSTANTS
	public static final String IP_ADDRESS;
	public static final int PORT;
	public static final String DB_HOST;
	public static final int DB_PORT;
	public static final String DB_NAME;
	public static final String DB_USER;
	public static final String DB_PSSWD;

	public static void main(String[] args) {

	//	ipAddress(IP_ADDRESS);
		port(PORT);

		if (IS_PRODUCTION_SERVER) {
			staticFileLocation(STATIC_FILES_LOCATION_PROD);
		} else {
			externalStaticFileLocation(STATIC_FILES_LOCATION_DEV);
		}

		webSocket("/pedidos", PedidosWebSocket.class);
		webSocket("/appstatuspedido", AppStatusPedidosWebSocket.class);

		// Workaround, index has been renamed!
		

		new SignInResource().addRoutes();
		new GeneralResource().addRoutes();
		new EstabelecimentoResource().addRoutes();
		new CardapioResource().addRoutes();
		new SaborResource().addRoutes();
		new TamanhoResource().addRoutes();
		new ParceiroResource().addRoutes();
		new ItemCardapioResource().addRoutes();
		new PrecoItemCardapioResource().addRoutes();
		new PedidoResource().addRoutes();
		new FormaPgtoResource().addRoutes();
		try {
			new ImageResource().addRoutes();
		} catch (Exception e) {
			System.out.println("Fodeu o upload de imagens... " + e);
		}

		System.out.println("Api Version: " + API_VERSION);
		System.out.println("Deploy Number: " + API_DEPLOY_NUMBER);

	}

	private static String getSystemEnv(String varName) {
		String envVariable = System.getenv(varName);
		if (envVariable != null && !"".equals(envVariable)) {
			return envVariable;
		} else {
			throw new RuntimeException(
					String.format("It is mandatory to set the %s environment variable with a proper value", varName));
		}
	}
	
    private static int getAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567; //return default port if heroku-port isn't set (i.e. on localhost)
    }

	static {

		File f = new File("");
		STATIC_FILES_LOCATION_DEV = f.getAbsolutePath() + "/src/main/java/staticfiles";

		FLICKR_API_KEY = getSystemEnv("CHEFF_CASA_FLICKR_API_KEY");
		FLICKR_SHARED_SECRET = getSystemEnv("CHEFF_CASA_FLICKR_SHARED_SECRET"); 
		FLICKR_AUTH_TOKEN = getSystemEnv("CHEFF_CASA_FLICKR_AUTH_TOKEN");
		FLICKR_AUTH_TOKEN_SECRET = getSystemEnv("CHEFF_CASA_FLICKR_AUTH_TOKEN_SECRET");

		PLACES_API_KEY = getSystemEnv("CHEFF_CASA_PLACES_API_KEY");
		IMAGE_LOADER_API_KEY = getSystemEnv("CHEFF_CASA_IMAGE_LOADER_API_KEY");
		
		String launch_mode = getSystemEnv("CHEFF_CASA_LAUNCH_MODE");
		IS_PRODUCTION_SERVER = "PROD".equals(launch_mode);
		IP_ADDRESS = getSystemEnv("CHEFF_CASA_IP");
        PORT = getAssignedPort();

		DB_HOST = getSystemEnv("CHEFF_CASA_MYSQL_HOST");
		String dbPort = getSystemEnv("CHEFF_CASA_MYSQL_PORT");
		try {
			DB_PORT = Integer.parseInt(dbPort);
		} catch (Exception e) {
			throw new RuntimeException(
					"It is mandatory to set the CHEFF_CASA_MYSQL_PORT environment variable with a proper numeric value");
		}
		DB_NAME = getSystemEnv("CHEFF_CASA_MYSQL_DB_NAME");		
		DB_USER = getSystemEnv("CHEFF_CASA_MYSQL_USER");		
		DB_PSSWD = getSystemEnv("CHEFF_CASA_MYSQL_PSSWD");

		String connectionURL = String.format("jdbc:mysql://%1$s:%2$d/%3$s", DB_HOST, DB_PORT, DB_NAME);
		System.out.println(connectionURL);

		System.out.println(DB_USER);
		System.out.println(DB_PSSWD);
		ConnectionManager.set(connectionURL, "com.mysql.jdbc.Driver", DB_USER, DB_PSSWD);

	}
}
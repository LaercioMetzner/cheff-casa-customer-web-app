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
package casa.cheff.customerwebapp.websockets;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import casa.cheff.customerwebapp.bean.db.Usuario;
import casa.cheff.customerwebapp.dao.UsuarioDAO;

@WebSocket
public class AppStatusPedidosWebSocket {

	public static Map<Session, Usuario> usuarioWebSocketUsers = new HashMap<>();

	private String sender, msg;

	@OnWebSocketConnect
	public void onConnect(Session user) throws Exception {
		Map<String, List<String>> params = user.getUpgradeRequest().getParameterMap();
		String facebookId = params.get("facebookId").get(0);
		//int codEstabelecimento = Integer.parseInt(facebookId);
		Usuario usuario = new UsuarioDAO().getUsuarioByFbId(facebookId);
		usuarioWebSocketUsers.put(user, usuario);
	}

	@OnWebSocketClose
	public void onClose(Session user, int statusCode, String reason) {
		usuarioWebSocketUsers.remove(user);
	}

	@OnWebSocketMessage
	public void onMessage(Session user, String message) {
		// App.broadcastMessage(sender = App.userUsernameMap.get(user), msg =
		// message);
	}

	public static void broadcastMsg(int codUsuario, String msg) {
		Set<Session> k = usuarioWebSocketUsers.keySet();
		Stream<Session> s = k.stream();

		// .filter(session -> session.isOpen() &&
		// usuarioWebSocketUsers.get(session).codUsuario == codUsuario)
		s.forEach(session -> {
			try {
				session.getRemote().sendString(msg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
}

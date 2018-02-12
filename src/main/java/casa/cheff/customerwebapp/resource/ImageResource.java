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

import java.io.InputStream;
import java.util.Collection;
import java.util.stream.Stream;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;

import org.scribe.model.Token;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.RequestContext;
import com.flickr4java.flickr.auth.Auth;
import com.flickr4java.flickr.auth.AuthInterface;
import com.flickr4java.flickr.photos.Size;
import com.flickr4java.flickr.uploader.UploadMetaData;
import com.flickr4java.flickr.uploader.Uploader;
import com.google.gson.Gson;

import casa.cheff.customerwebapp.Main;
import casa.cheff.customerwebapp.bean.db.FlickrPicture;
import casa.cheff.customerwebapp.dao.FlickrPhotoDAO;
import spark.Request;
import spark.Response;

public class ImageResource implements Resource {

	public static final String SETUP_PROPERTIES = "setup.properties",
			APPLICATION_TYPE_JSON_UTF8 = "application/json; charset=UTF-8",
			JETTY_MULTIPART_CONFIG = "org.eclipse.jetty.multipartConfig";

	private String apiKey, sharedSecret, authToken, authTokenSecret;
	private Flickr flickr;
	private Auth auth;

	@Override
	public void addRoutes() {
		post(Main.API_PROTECTED + "/imageupload",
				(req, res) -> uploadImage(req, res), new Gson()::toJson);
		get(Main.API_PROTECTED + "/flickrphotos/:photoId/:label",
				(req, res) -> getFlickrPicture(req, res), new Gson()::toJson);
	}

	public ImageResource() throws Exception {
		apiKey = Main.FLICKR_API_KEY;
		sharedSecret = Main.FLICKR_SHARED_SECRET;
		authToken = Main.FLICKR_AUTH_TOKEN;
		authTokenSecret = Main.FLICKR_AUTH_TOKEN_SECRET;
		
		// Does not work after build to single jar
		//ClassLoader loader = Thread.currentThread().getContextClassLoader();
		//Properties setupProperties = new Properties();
		//try (InputStream setupStream = loader
		//		.getResourceAsStream(SETUP_PROPERTIES)) {
		//	setupProperties.load(setupStream);
		//	apiKey = setupProperties.getProperty("apiKey");
		//	sharedSecret = setupProperties.getProperty("sharedSecret");
		//	authToken = setupProperties.getProperty("authToken");
		//	authTokenSecret = setupProperties.getProperty("authTokenSecret");
	    //} catch (IOException e) {
		//	throw new Exception("Error while reading setup properties: " + e);
		//}
		
		flickr = new Flickr(apiKey, sharedSecret, new REST());
		AuthInterface authInterface = flickr.getAuthInterface();
		Token requestToken = new Token(authToken, authTokenSecret);
		auth = authInterface.checkToken(requestToken);
		flickr.setAuth(auth);
	}

	private FlickrPicture getFlickrPicture(Request req, Response res) {
		String photoId = req.params(":photoId");
		int label = Integer.parseInt(req.params(":label"));
		return new FlickrPhotoDAO().getFlickrPicture(photoId, label);
	}

	private FlickrPicture uploadImage(Request req, Response res)
			throws Exception {
		res.type(APPLICATION_TYPE_JSON_UTF8);
		if (req.raw().getAttribute(JETTY_MULTIPART_CONFIG) == null) {
			String tmpDir = System.getProperty("java.io.tmpdir");
			MultipartConfigElement multipartConfigElement = new MultipartConfigElement(
					tmpDir);
			req.raw().setAttribute(JETTY_MULTIPART_CONFIG,
					multipartConfigElement);
			Part file = req.raw().getPart("file");
			InputStream is = file.getInputStream();

			UploadMetaData metaData = new UploadMetaData();
			metaData.setPublicFlag(true);
			metaData.setAsync(false);
			RequestContext.getRequestContext().setAuth(auth);
			Uploader uploader = flickr.getUploader();
			String photoId = uploader.upload(is, metaData);
			System.out.println(photoId);
			Collection<Size> sizes = flickr.getPhotosInterface().getSizes(
					photoId);
			new FlickrPhotoDAO().insertPhotoSizes(photoId, sizes);
			FlickrPicture flickrPicture = new FlickrPicture();
			flickrPicture.codPhotoId = photoId;
			Stream<Size> pictureN2 = sizes.stream().filter(
					p -> p.getLabel() == 2);
			flickrPicture.numLabel = 2;
			flickrPicture.desSource = pictureN2.findFirst().get().getSource();
			return flickrPicture;
		}
		throw new Exception(JETTY_MULTIPART_CONFIG + " is not null!");
	}

}

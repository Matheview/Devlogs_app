package backend;

import com.google.gson.Gson;
import controllers.Controller;
import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RequestService {

	/**
	 * Metoda sluzaca do logowania sie do aplikacji
	 * @param jsonInputString -> email, password, domain
	 * @return ResponseObject - success, msg, privilege, username, user_id
	 */
	public ResponseObject requestLoginSuccess(String jsonInputString)
	{
		ResponseObject ro = new ResponseObject();
		try {
			URL url = new URL("http://ssh-vps.nazwa.pl:4742/users/login");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			conn.setConnectTimeout(5000);
			conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");

			OutputStream os = conn.getOutputStream();
			os.write(jsonInputString.getBytes("UTF-8"));
			os.close();

			InputStream in = new BufferedInputStream(conn.getInputStream());
			String result = IOUtils.toString(in, "UTF-8");

			System.out.println("Odpowiedz z serwera : " + result );

			Gson gson = new Gson();
			ro = gson.fromJson(result, ResponseObject.class);

			in.close();
			conn.disconnect();

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ro;
	}

	/**
	 * Metoda sluzaca do pobrania listy domen uzytkownika
	 * @param userId
	 * @return ResponseObject - success, msg, domains ( List )
	 */
	public ResponseObject requestListOfDomains(int userId)
	{
		String adresSerwera = "http://ssh-vps.nazwa.pl:4742/getinfo/domains";
		StringBuilder sb = new StringBuilder(adresSerwera);
		sb = sb.append("?user_id=").append(userId);
		String request = sb.toString();

		ResponseObject ro = new ResponseObject();
		try {

			URL url = new URL(request);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			conn.setRequestMethod("GET");
			conn.setConnectTimeout(5000);
			conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			conn.setDoInput(true);

			InputStream in = new BufferedInputStream(conn.getInputStream());
			String result = IOUtils.toString(in, "UTF-8");

			System.out.println("Odpowiedz z serwera : " + result);

			Gson gson = new Gson();
			ro = gson.fromJson(result, ResponseObject.class);

			in.close();
			conn.disconnect();

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ro;
	}

	/**
	 * Metoda sluzaca do pobrania listy uzytkownikow z obecnej domeny
	 * @param userId
	 * @return ResponseObject - success, msg, users ( List )
	 */
	public ResponseObject requestListOfUsers(int userId)
	{
		String adresSerwera = "http://ssh-vps.nazwa.pl:4742/getinfo/users";
		StringBuilder sb = new StringBuilder(adresSerwera);
		sb = sb.append("?user_id=").append(userId);
		String request = sb.toString();

		ResponseObject ro = new ResponseObject();
		try {

			URL url = new URL(request);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			conn.setRequestMethod("GET");
			conn.setConnectTimeout(5000);
			conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			conn.setDoInput(true);

			InputStream in = new BufferedInputStream(conn.getInputStream());
			String result = IOUtils.toString(in, "UTF-8");

			System.out.println("Odpowiedz z serwera : " + result);

			Gson gson = new Gson();
			ro = gson.fromJson(result, ResponseObject.class);

			in.close();
			conn.disconnect();

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ro;
	}

	/**
	 * Metoda sluzaca do stworzenia nowej domeny
	 * @param jsonInputString user_id, domain
	 * @return ResponseObject - sueccess, msg
	 */
	public ResponseObject requestCreateNewDomain(String jsonInputString)
	{
		ResponseObject ro = new ResponseObject();
		try {
			URL url = new URL("http://ssh-vps.nazwa.pl:4742/domains/register");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			System.out.println("Przesylany jSON = " + jsonInputString);
			conn.setConnectTimeout(5000);
			conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");

			OutputStream os = conn.getOutputStream();
			os.write(jsonInputString.getBytes("UTF-8"));
			os.close();

			InputStream in = new BufferedInputStream(conn.getInputStream());
			String result = IOUtils.toString(in, "UTF-8");

			Gson gson = new Gson();
			ro = gson.fromJson(result, ResponseObject.class);

			in.close();
			conn.disconnect();

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ro;
	}

	/**
	 * Metoda sluzaca do stworzenia nowego uzytkownika
	 * @param jsonInputString - email, password, domain, privilege, username
	 * @return ResponseObject success, msg, privilege
	 */
	public ResponseObject requestCreateNewUser(String jsonInputString)
	{
		ResponseObject ro = new ResponseObject();
		try {
			URL url = new URL("http://ssh-vps.nazwa.pl:4742/users/register");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			System.out.println("Przesylany jSON = " + jsonInputString);
			conn.setConnectTimeout(5000);
			conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");

			OutputStream os = conn.getOutputStream();
			os.write(jsonInputString.getBytes("UTF-8"));
			os.close();

			InputStream in = new BufferedInputStream(conn.getInputStream());
			String result = IOUtils.toString(in, "UTF-8");

			Gson gson = new Gson();
			ro = gson.fromJson(result, ResponseObject.class);

			in.close();
			conn.disconnect();

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ro;
	}

	/**
	 * Metoda aktualizujaca uprawnienia dla uzytkownika
	 * @param jsonInputString user_id, granted_to, domain, privilege
	 * @return ResponseObject success, msg
	 */
	public ResponseObject requestUpdatePermission(String jsonInputString)
	{
		ResponseObject ro = new ResponseObject();
		try {
			URL url = new URL("http://ssh-vps.nazwa.pl:4742/users/permission");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			conn.setConnectTimeout(5000);
			conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestMethod("PUT");

			OutputStream os = conn.getOutputStream();
			os.write(jsonInputString.getBytes("UTF-8"));
			os.close();

			InputStream in = new BufferedInputStream(conn.getInputStream());
			String result = IOUtils.toString(in, "UTF-8");

			System.out.println("Response code : " + conn.getResponseCode());

			System.out.println("Odpowiedz z serwera : " + result );

			Gson gson = new Gson();
			ro = gson.fromJson(result, ResponseObject.class);

			in.close();
			conn.disconnect();

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ro;
	}

	/**
	 * Metoda wycofujaca uprawnienia uzytkownika
	 * @param jsonInputString user_id, granted_to, domain
	 * @return ResponseObject success, msg
	 */
	public ResponseObject requestDeletePermission(String jsonInputString)
	{
		ResponseObject ro = new ResponseObject();
		try {
			URL url = new URL("http://ssh-vps.nazwa.pl:4742/users/permission");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			conn.setConnectTimeout(5000);
			conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestMethod("DELETE");

			OutputStream os = conn.getOutputStream();
			os.write(jsonInputString.getBytes("UTF-8"));
			os.close();

			InputStream in = new BufferedInputStream(conn.getInputStream());
			String result = IOUtils.toString(in, "UTF-8");

			System.out.println("Response code : " + conn.getResponseCode());

			System.out.println("Odpowiedz z serwera : " + result );

			Gson gson = new Gson();
			ro = gson.fromJson(result, ResponseObject.class);

			in.close();
			conn.disconnect();

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ro;
	}

	/**
	 * Metoda pobierajaca notyfikacje z Arraya
	 * @param userId
	 * @return ResponseObject success, msg, notifications ( List ) , qty
	 */
	public ResponseObject requestGetNotifications(int userId)
	{
		String adresSerwera = "http://ssh-vps.nazwa.pl:4742/users/notifications";
		StringBuilder sb = new StringBuilder(adresSerwera);
		sb = sb.append("?user_id=").append(userId);
		String request = sb.toString();

		ResponseObject ro = new ResponseObject();
		try {

			URL url = new URL(request);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			conn.setRequestMethod("GET");
			conn.setConnectTimeout(5000);
			conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			conn.setDoInput(true);

			InputStream in = new BufferedInputStream(conn.getInputStream());
			String result = IOUtils.toString(in, "UTF-8");

			System.out.println("Odpowiedz z serwera : " + result);
			System.out.println("Response code : " + conn.getResponseCode());

			Gson gson = new Gson();
			ro = gson.fromJson(result, ResponseObject.class);

			in.close();
			conn.disconnect();

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ro;
	}

	/**
	 * Metoda sluzaca do przeczytania notyfikacji
	 * @param jsonInputString user_id, notify_id
	 * @return ResponseObject success, msg
	 */
	public ResponseObject requestReadNotification(String jsonInputString)
	{
		ResponseObject ro = new ResponseObject();
		try {
			URL url = new URL("http://ssh-vps.nazwa.pl:4742/users/notifications");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			System.out.println("Przesylany jSON = " + jsonInputString);
			conn.setConnectTimeout(5000);
			conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestMethod("PUT");

			OutputStream os = conn.getOutputStream();
			os.write(jsonInputString.getBytes("UTF-8"));
			os.close();

			InputStream in = new BufferedInputStream(conn.getInputStream());
			String result = IOUtils.toString(in, "UTF-8");

			Gson gson = new Gson();
			ro = gson.fromJson(result, ResponseObject.class);

			in.close();
			conn.disconnect();

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ro;
	}

	public ResponseObject requestChangePassword(String jsonInputString)
	{
		ResponseObject ro = new ResponseObject();
		try {
			URL url = new URL("http://ssh-vps.nazwa.pl:4742/users/changepasswd");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			conn.setConnectTimeout(5000);
			conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");

			OutputStream os = conn.getOutputStream();
			os.write(jsonInputString.getBytes("UTF-8"));
			os.close();

			InputStream in = new BufferedInputStream(conn.getInputStream());
			String result = IOUtils.toString(in, "UTF-8");

			System.out.println("Odpowiedz z serwera : " + result );

			Gson gson = new Gson();
			ro = gson.fromJson(result, ResponseObject.class);

			in.close();
			conn.disconnect();

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ro;
	}
}

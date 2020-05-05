package backend;

import backend.requestObjects.RqNewProject;
import backend.responseObjects.RsDomains;
import backend.responseObjects.RsProject;
import backend.responseObjects.RsProjects;
import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RequestService {

    private static final String ADDRESS = "http://ssh-vps.nazwa.pl:4742"; // adres serwera
	private static final String CHARSET = "UTF-8";	// Kodowanie tekstu używane przy zapytaniach i odpowiedziach Http

	/**
	 * Metoda ustawiająca połączenie z serwerem, z którego pobierane są dane.
	 * Zwraca obiekt HttpURLConnection, z którego można pobierać strumienie IO.
	 *
	 * @param endAddress końcówka adresu serwera (podstawowy adres jest zapisany w funkcji)
	 * @param method jaka metoda zostanie użyta przy zapytaniu
	 * @return obiekt połączenia z serwerem (HttpURLConnection)
	 */
	private HttpURLConnection getConnection(String endAddress, String method) throws IOException {
		URL url = new URL(ADDRESS + endAddress);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		connection.setRequestMethod(method);
		connection.setConnectTimeout(5000);
		connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
		connection.setDoInput(true);
		connection.setDoOutput(true);

		return connection;
	}

	/**
	 * Metoda do wysyłania pliku JSON, przez strumień, do serwera
     * @param connection Ustawione już połączenie z serwerem
	 * @param jsonInputString plik JSON
	 * @return JSON w postaci Stringa
	 */
	private void sendJSON (HttpURLConnection connection, String jsonInputString) throws IOException {
        OutputStream out = connection.getOutputStream();
        out.write(jsonInputString.getBytes(CHARSET));
        out.close();
    }

    /**
     * Metoda do pobierania odpowiedzi z serwera przez strumień
     * @param connection Ustawione już połączenie z serwerem
     * @return JSON w postaci Stringa
     * @throws IOException
     */
    private String getServerResponse (HttpURLConnection connection) throws IOException {
        InputStream in = new BufferedInputStream(connection.getInputStream());
        String result = IOUtils.toString(in, CHARSET);
        in.close();
        return result;
    }

    /**
	 * Metoda sluzaca do logowania sie do aplikacji
	 * @param jsonInputString -> email, password, domain
	 * @return ResponseObject - success, msg, privilege, username, user_id
	 */
	public ResponseObject requestLoginSuccess(String jsonInputString)
	{
		ResponseObject ro = new ResponseObject();
		try {
			HttpURLConnection conn = this.getConnection("/users/login", "POST");
			OutputStream os = conn.getOutputStream();
			os.write(jsonInputString.getBytes(CHARSET));
			os.close();

			InputStream in = new BufferedInputStream(conn.getInputStream());
			String result = IOUtils.toString(in, CHARSET);

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
			String result = IOUtils.toString(in, CHARSET);

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
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ro;
	}

    /**
     * Metoda służżca do pobierania listy projektów
     * @param userId Id użytkownika
     * @return Obiekt listy projektów
     */
    public RsProjects getUserProjects(int userId) throws IOException {
        String addressEnd = "/getinfo/projects?user_id=" + userId;

		HttpURLConnection connection = getConnection(addressEnd, "GET");

		String result = getServerResponse(connection);

		Gson gson = new Gson();
		RsProjects responseObject = gson.fromJson(result, RsProjects.class);

		connection.disconnect();

        return responseObject;
    }

	/**
	 * Metoda sluzaca do stworzenia nowego projektu
	 * @param newProject obiekt zawierający dane, które zostaną wysłane w body requesta
	 * @return ResponseObject success, msg, privilege
	 */
	public RsProject createNewProject(RqNewProject newProject) throws IOException {
		HttpURLConnection connection = getConnection("/projects/register", "POST");

		Gson gson = new Gson();
		String jsonInputString = gson.toJson(newProject);

		sendJSON(connection, jsonInputString);

		String result = getServerResponse(connection);

		RsProject responseObject = gson.fromJson(result, RsProject.class);

		connection.disconnect();

		return responseObject;
	}


	/**
	 * Metoda służaca do pobierania listy domen
	 * @param userId Id użytkownika
	 * @return obiekt listy domen
	 */
	public RsDomains getUserDomains(int userId) throws IOException {
		String addressEnd = "/getinfo/domains?user_id=" + userId;

		HttpURLConnection connection = getConnection(addressEnd, "GET");

		String result = getServerResponse(connection);

		Gson gson = new Gson();
		RsDomains responseObject = gson.fromJson(result, RsDomains.class);

		connection.disconnect();

		return responseObject;
	}

}

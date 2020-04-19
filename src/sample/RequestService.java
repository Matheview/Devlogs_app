package sample;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RequestService {

	public ResponseObject request(String jsonInputString)
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

			System.out.println(ro.isSuccess());
			System.out.println(ro.getPrivilege());
			System.out.println(ro.getMsg());

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

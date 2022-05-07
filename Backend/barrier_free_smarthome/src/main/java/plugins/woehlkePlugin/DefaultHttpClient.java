package plugins.woehlkePlugin;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class DefaultHttpClient {
	
	public static final int HTTP_STATUS_CODE_200_OK = 200;

	public static int sendSimpleRequest(String urlAsString, int connectTimeoutInMilliseconds, int readTimeoutInMilliseconds) throws IOException {
		int responseCode = -1;
		
		URL url = new URL(urlAsString);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		
		responseCode = con.getResponseCode();
		
		con.disconnect();
		
		return responseCode;
	}
}

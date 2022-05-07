package plugins.woehlkePlugin;
import de.ableitner.barrierfreeSmarthome.common.BarrierfreeSmarthomeException;
import de.ableitner.barrierfreeSmarthome.common.SharedAttribute;
import de.ableitner.barrierfreeSmarthome.common.plugin.AbstractPlugin;
import de.ableitner.barrierfreeSmarthome.common.plugin.PluginApiVersionEnum;

import java.io.IOException;
import java.util.concurrent.ConcurrentMap;





public class WoehlkePlugin extends AbstractPlugin {

	// ============================================================================================================================================
	// ============================================================================================================================================
	// Attributes
	// ============================================================================================================================================
	// ============================================================================================================================================
	
	
	
	
	// ============================================================================================================================================
	// ============================================================================================================================================
	// Constructors
	// ============================================================================================================================================
	// ============================================================================================================================================
	
	public WoehlkePlugin(){
		super();
	}
	
	
	
	
	// ============================================================================================================================================
	// ============================================================================================================================================
	// Getters and Setters
	// ============================================================================================================================================
	// ============================================================================================================================================
	
	// ============================================================================================================================================
	// ============================================================================================================================================
	// Definition of abstract Methods
	// ============================================================================================================================================
	// ============================================================================================================================================
	
	// ============================================================================================================================================
	// ============================================================================================================================================
	// override Methods
	// ============================================================================================================================================
	// ============================================================================================================================================
	
	
	
	
	// ============================================================================================================================================
	// ============================================================================================================================================
	// public Methods
	// ============================================================================================================================================
	// ============================================================================================================================================

	@Override
	public String getPluginId() {
		return "woehlke";
	}
	
	@Override
	public void action(ConcurrentMap<String, SharedAttribute> sharedAttributes, ConcurrentMap<String, String> parameters) {
		String socketNumberAsString = parameters.get("socketNumber");
		String newStateAsString = parameters.get("newState");
		String ipAddress = (String) sharedAttributes.get("/woehlke/ip").getValue();
		
		try {
			this.checkSocketNumber(socketNumberAsString);
			this.checkNewState(newStateAsString);
			int newStateAsInt;
			if (newStateAsString.equals("on")) {
				newStateAsInt = 1;
			} else {
				newStateAsInt = 0;
			}
			try {
				String url = this.createUrl(socketNumberAsString, newStateAsInt, ipAddress);
				int responseCode = DefaultHttpClient.sendSimpleRequest(url, 5000, 5000);
				if (responseCode != DefaultHttpClient.HTTP_STATUS_CODE_200_OK) {
					String errorMessage = "The request to enable / disable the socket " + socketNumberAsString + " failed (http responce code: "
							+ responseCode + ")!";
					String userErrorMessage = "The request to enable / disable the socket " + socketNumberAsString + " failed!";
					throw new BarrierfreeSmarthomeException(errorMessage, null, userErrorMessage);
				}
			} catch (IOException exception) {
				exception.printStackTrace();
				String errorMessage = "The request to enable / disable the socket " + socketNumberAsString + " failed!";
				String userErrorMessage = "The request to enable / disable the socket " + socketNumberAsString + " failed!";
				throw new BarrierfreeSmarthomeException(errorMessage, exception, userErrorMessage);

			}// end of inner try catch block
			
		} catch (BarrierfreeSmarthomeException exception) {
			exception.printStackTrace();
		}// end of outer try catch block

	}

	private void checkSocketNumber(String socketNumberAsString) throws BarrierfreeSmarthomeException {
		if (socketNumberAsString.equals("1")) {
			return;
		} else if (socketNumberAsString.equals("2")) {
			return;
		} else if (socketNumberAsString.equals("3")) {
			return;
		} else {
			String errorMessage = "The request to enable / disable the socket could not be executed, because the socket number ("
					+ socketNumberAsString + ") is invalid! It must be \"1\", \"2\" or \"3\".";
			String userErrorMessage = "The request to enable / disable the socket could not be executed, because the socket number ("
					+ socketNumberAsString + ") is invalid!";
			throw new BarrierfreeSmarthomeException(errorMessage, null, userErrorMessage);
		}
	}
	
	private void checkNewState(String newStateAsString) throws BarrierfreeSmarthomeException {
		if (!newStateAsString.equals("on") && !newStateAsString.equals("off")) {
			String errorMessage = "The request to enable / disable the socket could not be executed, because the new state (" + newStateAsString
					+ ") is invalid! It must be \"on\" or \"off\".";
			String userErrorMessage = "The request to enable / disable the socket could not be executed, because the new state ("
					+ newStateAsString + ") is invalid!";
			throw new BarrierfreeSmarthomeException(errorMessage, null, userErrorMessage);
		}
	}
	
	private String createUrl(String socketNumberAsString, int newStateAsInt, String ipAddress){
		String url = "http://" + ipAddress;
		url += "/cgi-bin/schalten?steckdose_nr=" + socketNumberAsString;
		url += "&steckdose_soll=" + newStateAsInt;
		return url;
	}

	@Override
	public PluginApiVersionEnum getApiVersion() {
		return PluginApiVersionEnum.API_VERSION_1;
	}
	
	

	// ============================================================================================================================================
	// ============================================================================================================================================
	// protected Methods
	// ============================================================================================================================================
	// ============================================================================================================================================
	
	// ============================================================================================================================================
	// ============================================================================================================================================
	// private Methods
	// ============================================================================================================================================
	// ============================================================================================================================================
	

	
	
	
}

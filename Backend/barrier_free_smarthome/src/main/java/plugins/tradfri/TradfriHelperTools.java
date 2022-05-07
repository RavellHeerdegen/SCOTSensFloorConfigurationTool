package plugins.tradfri;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.config.NetworkConfig;
import org.eclipse.californium.scandium.DTLSConnector;
import org.eclipse.californium.scandium.config.DtlsConnectorConfig;
import org.eclipse.californium.scandium.dtls.pskstore.StaticPskStore;

/**
 * @author Florian Jungermann
 *
 *
 * This class provides helper Tools to work with the IKEA Tradfri.
 *  For example to gather information about the connected bulbs or set a new identity on the gateway.
 */
public class TradfriHelperTools {

	public static void main(String[] args) {

	}
	
	
	/**
	 * Creates a new identity on the gateway and returns the preshared key which can be used to access the gateway.
	 * Note: If the identity already exists this will fail. You have to create a new identity.
	 * 
	 * @param gateway_ip IP address of the gateway
	 * @param security_key Security key which is printed on the back of the gateway.
	 * @param identity The identity you want to create (this can be basically any String. You will need it later to access the gateway.)
	 * @return
	 */
	public static String getPresharedKey(String gateway_ip, String security_key, String identity) {
		CoapClient cc = new CoapClient("coaps://" + gateway_ip + "/" + "15011/9063"); //Note coaps port defined in californium.properties

		DtlsConnectorConfig.Builder builder = new DtlsConnectorConfig.Builder(); //new InetSocketAddress(0)
		builder.setPskStore(new StaticPskStore("Client_identity", security_key.getBytes()));
		CoapEndpoint coap_endpoint = new CoapEndpoint(new DTLSConnector(builder.build()), NetworkConfig.getStandard());
		cc.setEndpoint(coap_endpoint);

		CoapResponse response = cc.post("{\"9090\":\"" + identity + "\"}", MediaTypeRegistry.APPLICATION_JSON);

		if (response != null && response.isSuccess()){
			return response.getResponseText();
		} else return "";
	}
	
	
	/** Discovers all bulbs connected to the gateway and prints them out in the console. This can be useful to get the IDs of the bulbs.
	 * 
	 * @param gateway_ip The IP address of the gateway
	 * @param security_identity The identity used to access the gateway
	 * @param security_key The preshared key associated with the identity
	 */
	public static void discoverBulbs (String gateway_ip, String security_identity, String security_key) {
		TradfriGateway gw = new TradfriGateway(gateway_ip, security_identity, security_key);
        gw.initCoap();
		gw.dicoverBulbs();
		
		for (LightBulb b : gw.bulbs) {
			System.out.println(b.toString());
		}
	}

}

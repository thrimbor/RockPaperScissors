package rps.network;

import static java.net.NetworkInterface.getNetworkInterfaces;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

/**
 * Utility methods to simplify the networking part of this project. The main
 * task of this class is to manage the creation of a {@code GameRegistry}
 * <p>
 * You can use this utility class to host local games, to host a
 * {@code GameRegistry} over network or to connect to a remote one, but you can
 * also request all available IP addresses, because it is a related task.
 */
public class NetworkUtil {

	private static final String GAME_SERVICE_NAME = "RPS_GAME_REGISTRY";
	private static final int PORT = 1099;
	private static boolean isRegistryCreated = false;

	public static GameRegistry hostLocalGame() {
		try {
			return new GameRegistryImpl();
		} catch (RemoteException e) {
			throw newRuntimeException(e);
		}
	}

	public static GameRegistry hostNetworkGame(String host) {

		System.setProperty("java.net.preferIPv4Stack", "true");
		System.setProperty("java.rmi.server.hostname", host);

		try {
			GameRegistry gameRegistry = new GameRegistryImpl();
			createRegistryIfNotRunning();
			Naming.rebind(createUrlWithoutSchemeComponent(host), gameRegistry);

			return gameRegistry;
		} catch (Exception e) {
			throw newRuntimeException(e);
		}
	}

	private static void createRegistryIfNotRunning() throws RemoteException {
		if (!isRegistryCreated) {
			LocateRegistry.createRegistry(PORT);
			isRegistryCreated = true;
		}
	}

	public static GameRegistry requestRegistry(String host) {
		try {
			System.setProperty("java.net.preferIPv4Stack", "true");
			String url = createUrl(host);
			return (GameRegistry) Naming.lookup(url);
		} catch (Exception e) {
			throw newRuntimeException(e);
		}
	}

	private static String createUrl(String host) {
		return "rmi://" + host + ":" + PORT + "/" + GAME_SERVICE_NAME;
	}

	private static String createUrlWithoutSchemeComponent(String host) {
		return "//" + host + ":" + PORT + "/" + GAME_SERVICE_NAME;
	}

	/**
	 * The IPv4 addresses of this machine (on all interfaces), including
	 * localhost.
	 */
	public static String[] getIPV4Addresses() {
		List<String> ret = new LinkedList<String>();

		for (NetworkInterface cur : getInterfaces()) {
			for (InterfaceAddress addr : cur.getInterfaceAddresses()) {
				InetAddress inet_addr = addr.getAddress();
				if (!(inet_addr instanceof Inet4Address)) {
					continue;
				}
				ret.add(inet_addr.getHostAddress());
			}
		}
		return ret.toArray(new String[] {});
	}

	private static Iterable<NetworkInterface> getInterfaces() {

		List<NetworkInterface> ifs = new ArrayList<NetworkInterface>();

		try {
			Enumeration<NetworkInterface> networkInterfaces = getNetworkInterfaces();
			while (networkInterfaces.hasMoreElements()) {
				ifs.add(networkInterfaces.nextElement());
			}
			return ifs;
		} catch (SocketException e) {
			throw newRuntimeException(e);
		}
	}

	private static RuntimeException newRuntimeException(Exception e) {
		return new RuntimeException("Ouch, something went wrong", e);
	}
}
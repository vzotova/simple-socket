package ru.szotov.socket;

/**
 * Main class for starting server
 * 
 * @author szotov
 *
 */
public class Server {
	
	private static final int DEFAULT_PORT = 8080;
	
	private static Listener _listener;
	
	/**
	 * @return running {@link Listener}
	 */
	public static Listener getListener() {
		return _listener;
	}
	
	/**
	 * Starts {@link Listener} and adds shutdown hook
	 * 
	 * @param args 
	 *            port number and maximum number of sessions. 
	 *            If values not set then will be used default values
	 */
	public static void main(String... args) {
		int port = DEFAULT_PORT;
		if (args.length > 0) {
			port = Integer.parseInt(args[0]);
		}
		int maxThreads = Runtime.getRuntime().availableProcessors();
		if (args.length > 1) {
			maxThreads = Integer.parseInt(args[1]);
		}
		
		_listener = new Listener(port, maxThreads);
		_listener.start();
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				_listener.close();			
			};
		});
	}

}

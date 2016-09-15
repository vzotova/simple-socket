package ru.szotov.socket;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Socket client
 * 
 * @author szotov
 *
 */
public class Client implements AutoCloseable {
	
	private static final Logger LOG = LogManager.getLogger(Client.class);
	private static final String DEFAULT_HOST = "localhost";
	private static final int DEFAULT_PORT = 8080;
	
	private String _host;
	private int _port;
	private Socket _socket;
	private Thread _inputPipe;
	private Thread _outputPipe;
	
	/**
	 * @param host
	 *            server host
	 * @param port
	 *            server port
	 */
	public Client(String host, int port) {
		_host = host;
		_port = port;
	}
	
	/**
	 * Connect to the server
	 * 
	 * @throws IOException
	 * @throws UnknownHostException
	 */
	public void startSession() throws UnknownHostException, IOException {
		_socket = new Socket(_host, _port);
		LOG.info("Connection to [{}:{}] established", _host, _port);
		_inputPipe = new PipedThread(System.in, _socket.getOutputStream());
		_inputPipe.start();
		_outputPipe = new PipedThread(_socket.getInputStream(), System.out);
		_outputPipe.start();
	}

	@Override
	public void close() {
		if (_socket != null && !_socket.isClosed()) {
			try {
				_socket.close();
			} catch (IOException e) {
				LOG.error("Error while closing socket", e);
			}
		}
		if (_inputPipe != null && !_inputPipe.isInterrupted()) {
			_inputPipe.interrupt();
		}
		if (_outputPipe != null && !_outputPipe.isInterrupted()) {
			_outputPipe.interrupt();
		}
		LOG.info("Connection closed");
	}
	
	/**
	 * Starts session with server
	 * 
	 * @param args
	 *            host and port. If values not set then will be used default
	 *            values
	 * @throws IOException
	 * @throws UnknownHostException
	 */
	public static void main(String... args) throws UnknownHostException, IOException {
		String host = DEFAULT_HOST;
		if (args.length > 0) {
			host = args[0];
		}
		
		int port = DEFAULT_PORT;
		if (args.length > 1) {
			port = Integer.parseInt(args[1]);
		}
		
		Client client = new Client(host, port);
		client.startSession();
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				client.close();			
			};
		});
	}

}

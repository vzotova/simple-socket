package ru.szotov.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Socket listener
 * 
 * @author szotov
 *
 */
public class Listener extends Thread implements AutoCloseable {
	
	private static final Logger LOG = LogManager.getLogger(Listener.class);
	
	private ExecutorService _threadPool;
	private int _port;
	private ServerSocket _server;
	private int _maxSessions;
	
	/**
	 * @param port
	 *            port for listening
	 * @param maxSessions
	 *            maximum number of sessions
	 */
	public Listener(int port, int maxSessions) { 
		_port = port;
		_maxSessions = maxSessions;
	}
	
	/**
	 * Listen on port until thread will not interrupted. For each new client
	 * session listener starts new thread
	 */
	@Override
	public void run() {
		try {
			_server = new ServerSocket(_port);
			if (_port == 0) {
				_port = _server.getLocalPort();
			}
			_threadPool = getThreadPool(_maxSessions);
			LOG.info("Started to listen port [{}] with maximum number of active sessions [{}]", 
					_port, _maxSessions);			
			while (!isInterrupted()) {
				Socket socket = _server.accept();
				LOG.info("Incoming connection from [{}]", socket.getInetAddress());
				_threadPool.execute(getRequestHandler(socket));
			}						
		} catch (IOException e) {
			LOG.error("Error while listening port [{}]", _port, e);
		} finally {
			close();
		}
	}

	/**
	 * Create new thread pool. Used for testing
	 * 
	 * @param maxSessions
	 *            maximum number of sessions
	 * @return thread pool
	 */
	protected ExecutorService getThreadPool(int maxSessions) {
		return Executors.newFixedThreadPool(maxSessions);
	}

	/**
	 * Create new {@link RequestHandler}. Used for testing
	 * 
	 * @param socket
	 *            {@link Socket}
	 * @return new instance of {@link RequestHandler}
	 */
	protected RequestHandler getRequestHandler(Socket socket) {
		return new RequestHandler(socket);
	}
	
	/**
	 * @return listening port. 0 if server is not started
	 */
	public int getLocalPort() {
		return _port;
	}

	/**
	 * Shutdown all active sessions
	 */
	@Override
	public synchronized void close() {
		if (_server != null && !_server.isClosed()) {
			try {
				_server.close();
			} catch (IOException e) {
				LOG.error("Error while closing socket", e);
			}
			LOG.info("Stopped to listen port [{}]", _port);
		}
		
		if (_threadPool != null && !_threadPool.isShutdown()) {
			 _threadPool.shutdownNow();
		}
		
		if (!isInterrupted()) {
			interrupt();
		}
	}
}

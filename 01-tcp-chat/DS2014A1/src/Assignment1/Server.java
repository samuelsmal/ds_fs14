/*
 * Samuel von Baussnern
 * S09-914-623
 * samuel.vonbaussnern@uzh.ch
 * 
 * (c) University of Zurich 2014
 */

package Assignment1;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
	static List<String> messageStore = new CopyOnWriteArrayList<>();
	static List<Socket> listenerSockets = new CopyOnWriteArrayList<>();

	// Listen for incoming client connections and handle them
	public static void main(String[] args) {
		if (args.length != 1) {
			System.err.println("Usage: java Server <port number>");
			System.exit(1);
		}

		int port = Integer.parseInt(args[0]);

		ServerSocket serverSocket;

		// the server listens to incoming connections
		// this is a blocking operation
		// which means the server listens to connections infinitely
		// when a new request is accepted, spawn a new thread to handle the
		// request
		// keep listening to further requests
		// if you want, you can use the class HandleClient to process client
		// requests
		// the first message for each new client connection is either "PRODUCER"
		// or "LISTENER"

		try {
			serverSocket = new ServerSocket(port);

			// Threads that have not been used for sixty seconds are terminated
			// and removed from the cache.
			ExecutorService executorService = Executors.newCachedThreadPool();

			while (true) {
				executorService.submit(new HandleClient(serverSocket.accept()));
			}
		} catch (UnknownHostException e) {
			System.out.println("UnkownHostException: " + e.getMessage());
		} catch (IOException e) {
			System.out
					.println("Exception caught when trying to listen on port "
							+ port + " or listening for a connection");
			System.out.println(e.getMessage());
		}
	}
}

class HandleClient implements Runnable {
	private Socket socket;
	private BufferedReader in;
	private BufferedWriter out;
	private static final String listenerIdentificationString = "LISTENER";
	private static final String producerIdentificationString = "PRODUCER";

	HandleClient(Socket socket) {
		this.socket = socket;
	}

	public void run() {
		String clientType;

		try {
			in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));

			// Type recognition
			clientType = in.readLine();
			// Type logging
			System.out.println(clientType + " registered");

			if (clientType.equals(listenerIdentificationString)) {
				handleListener();
			} else if (clientType.equals(producerIdentificationString)) {
				handleProducer();
			} else {
				throw new Exception("NO SUCH CLIENT TYPE! " + clientType);
			}

		} catch (IOException e) {
			System.out.println("HandleClient IOException: ");
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println("HandleClient Exception: ");
			System.out.println(e.getMessage());
		}
	}

	private void handleListener() {
		try {
			// Registering new listener
			Server.listenerSockets.add(socket);
			
			out = new BufferedWriter(new OutputStreamWriter(
					socket.getOutputStream()));
			
			for (String message : Server.messageStore) {
				out.write(message);
				out.newLine();
				out.flush();
			}
		} catch (IOException e) {
			System.out.println("HandleClient.handleListener IOException: ");
			System.out.println(e.getMessage());
		}
	}

	private void handleProducer() {
		try {
			// Producers need not be registered
			List<BufferedWriter> bufferedWriters = new ArrayList<BufferedWriter>(
					Server.listenerSockets.size());
			
			for (Socket listener : Server.listenerSockets) {
				bufferedWriters.add(new BufferedWriter(new OutputStreamWriter(
						listener.getOutputStream())));
			}

			for (String message = in.readLine(); message != null; message = in
					.readLine()) {
				Server.messageStore.add(message);

				for (BufferedWriter bufferedWriter : bufferedWriters) {
					bufferedWriter.write(message);
					bufferedWriter.newLine();
					bufferedWriter.flush();
				}
			}
		} catch (IOException e) {
			System.out.println("HandleClient.handleProducer IOException: ");
			System.out.println(e.getMessage());
		}
	}
}

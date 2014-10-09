/*
 * The implementation of Listener is inside Listener.java. 
 * It takes two command line parameters: the name and the port number of the server. 
 * It first also sends the string “LISTENER” to server. 
 * It then continuously receives the messages sent from the server infinitely. 
 * It has to print the messages to stdout as they are received.
 * 
 * Samuel von Baussnern
 * S09-914-623
 * samuel.vonbaussnern@uzh.ch
 * 
 * (c) University of Zurich 2014
 */

package Assignment1;

import java.net.*;
import java.io.*;

public class Listener {
	public static void main(String[] args) {
		if (args.length != 2) {
			System.err
					.println("Usage: java Listener <server name> <port number>");
			System.exit(1);
		}

		String serverName = args[0];
		int port = Integer.parseInt(args[1]);

		Socket clientSocket;
		BufferedWriter out;
		BufferedReader in;

		try {
			clientSocket = new Socket(serverName, port);
			out = new BufferedWriter(new OutputStreamWriter(
					clientSocket.getOutputStream()));

			out.write("LISTENER");
			out.newLine();
			out.flush();

			in = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()));

			while (true) {
				String message = in.readLine();

				if (message != null) {
					System.out.println(message);
				}
			}
		} catch (IOException e) {
			System.out.println("Listener exception: ");
			System.out.println(e.getMessage());
		}
	}
}

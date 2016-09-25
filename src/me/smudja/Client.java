package me.smudja;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Client extends JFrame {

	private JTextField userText;
	private JTextArea chatDisplay;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private String msg = "";
	private String serverIP;
	private Socket connection;

	// constructor
	public Client(String host) {
		super("IM Client");
		serverIP = host;
		userText = new JTextField();
		userText.setEditable(false);
		userText.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				sendMessage(event.getActionCommand());
				userText.setText("");
			}
		});
		add(userText, BorderLayout.NORTH);

		chatDisplay = new JTextArea();
		chatDisplay.setEditable(false);
		add(new JScrollPane(chatDisplay), BorderLayout.CENTER);
		setSize(450, 300);
		setVisible(true);
	}

	// start client functionality
	public void startClient() {
		try {
			establishConnection();
			setupStreams();
			whileChatting();
		} catch (EOFException eofExc) {
			showMessage("\n Client ended the connection... ");
		} catch (IOException ioExc) {
			ioExc.printStackTrace();
		} finally {
			cleanup();
		}
	}

	// connect to server
	private void establishConnection() throws IOException {
		showMessage(" Attempting to connect... \n");
		connection = new Socket(InetAddress.getByName(serverIP), 6789);
		showMessage(" Connection to " + connection.getInetAddress().getHostName() + " established \n");
	}

	// setup data streams
	private void setupStreams() throws IOException {
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\n Connection streams established \n");
	}

	// while chatting
	private void whileChatting() throws IOException {
		ableToType(true);
		do {
			try {
				msg = (String) input.readObject();
				showMessage("\n " + msg);
			} catch (ClassNotFoundException exc) {
				showMessage("\n ERROR: Server sent invalid message! ");
			}
		} while (!msg.equals("SERVER - END"));
	}

	// close streams and sockets, ending connection
	private void cleanup() {
		showMessage("\n Closing connection... ");
		ableToType(false);
		try {
			output.close();
			input.close();
			connection.close();
		} catch (IOException exc) {
			exc.printStackTrace();
		}
	}

	// send message to server
	private void sendMessage(String message) {
		try {
			output.writeObject("CLIENT - " + message);
			output.flush();
			showMessage("\n CLIENT - " + message);
		} catch (IOException exc) {
			showMessage("\n ERROR: Unable to send message ");
		}
	}

	// show message
	private void showMessage(final String message) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				chatDisplay.append(message);
			}
		});
	}
	
	// changes whether user is able to type in chat box
	private void ableToType(final Boolean state) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				userText.setEditable(state);
			}
		});
	}
}

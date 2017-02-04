package me.smudja;

import java.io.*;
import java.net.*;
import javax.swing.*;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

public class Client extends Application {

	private String name;
	private JTextField userText;
	private JTextArea chatDisplay;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private String msg = "";
	private String serverIP;
	private Socket connection;
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void init() {
		this.name = "Charlie";
		this.serverIP = "127.0.0.1";
	}
	
	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("IM Client - " + name);
		
		FlowPane rootNode = new FlowPane(20,20);
		
		primaryStage.setScene(new Scene(rootNode, 450, 300));	
		
		primaryStage.show();
	}
	
//	userText = new JTextField();
//	userText.setEditable(false);
//	userText.addActionListener(new ActionListener() {
//		public void actionPerformed(ActionEvent event) {
//			sendMessage(event.getActionCommand());
//			userText.setText("");
//		}
//	});
//	add(userText, BorderLayout.NORTH);
//
//	chatDisplay = new JTextArea();
//	chatDisplay.setEditable(false);
//	add(new JScrollPane(chatDisplay), BorderLayout.CENTER);
//	setSize(450, 300);
//	setVisible(true);

	// start client functionality
	public void startClient() {
		try {
			establishConnection();
			setupStreams();
			whileChatting();
		} catch (EOFException eofExc) {
		} catch (IOException ioExc) {
			ioExc.printStackTrace();
		} finally {
			cleanup();
		}
	}

	// connect to server
	private void establishConnection() throws IOException {
		showMessage("\n Attempting to connect... ");
		connection = new Socket(InetAddress.getByName(serverIP), 6789);
		showMessage("\n Connection to " + connection.getInetAddress().getHostName() + " established ");
	}

	// setup data streams
	private void setupStreams() throws IOException {
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\n Connection streams established ");
	}

	// while chatting
	private void whileChatting() throws IOException {
		ableToType(true);
		do {
			try {
				msg = (String) input.readObject();
				showMessage(msg);
			} catch (ClassNotFoundException exc) {
				showMessage("\n ERROR: Server sent invalid message! ");
			}
		} while (!msg.equals("END"));
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
			output.writeObject(message);
			output.flush();
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

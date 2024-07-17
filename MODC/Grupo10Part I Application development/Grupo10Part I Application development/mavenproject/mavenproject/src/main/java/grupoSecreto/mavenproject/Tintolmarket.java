package grupoSecreto.mavenproject;

/**
 * @author Grupo 10
 * @author Henrique Catarino - 56278
 * @author JoÃ£o OsÃ³rio - 56353
 * @author Vasco Maria - 56374
 */

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Tintolmarket {
	
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		
		String[] address = args[0].split(":");
		String host = address[0];
		int port = 12345;
		if (address.length > 1) {
			try {
				port = Integer.parseInt(address[1]);
			} catch (NumberFormatException e) {
				System.out.println("Wrong format used. Please use \"Tintolmarket "
						+ "<IP/hostname>[:port] <userID> [password]\"");
				System.exit(-1);
			}
		}

		Scanner input = new Scanner(System.in);
		String user = "";
        String password = "";

        // Solicita o nome de usuário até que um valor válido seja inserido
        while (user.isEmpty()) {
            System.out.println("Introduz o teu username: ");
            user = input.nextLine();
            if (user.isEmpty()) {
                System.out.println("Por favor, insira um nome de usuário válido.");
            }
        }

        // Solicita a senha até que um valor válido seja inserido
        while (password.isEmpty()) {
            System.out.print("Password: ");
            password = input.nextLine();
            if (password.isEmpty()) {
                System.out.println("Por favor, insira uma senha válida.");
            }
        }

		Socket socket = new Socket(host, port);
		ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());
		ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());
		outStream.writeObject(user);
		outStream.writeObject(password);
		outStream.flush();
		boolean authenticated = inStream.readBoolean();
		if (!authenticated) {
			System.out.println("Authentication error: Password doesn't match existing one!");
			input.close();
			inStream.close();
			outStream.close();
			socket.close();
			System.exit(-1);
		}
		System.out.println("Authentication successful.");
		System.out.println("Hello " + user + "!");
		System.out.println();
		System.out.println("Menu:");
		System.out.println("talk");
		System.out.println("read");
		System.out.println("quit");
		System.out.println();
		boolean stop = false;
		
		Loop:
		while (!stop) {
			System.out.print("Command: ");
			String[] command = input.nextLine().split(" ");
			if ((command[0].equals("talk") || command[0].equals("t")) && command.length == 1) {
				System.out.print("Digite o usuário destinatário da mensagem: \n");
				String destinatario = input.nextLine();
				System.out.print("Digite a mensagem: \n");
				String mensagem = input.nextLine();
				outStream.writeObject(command[0]);
				outStream.writeObject(destinatario);
				outStream.writeObject(mensagem);
				outStream.flush();
				boolean successful = inStream.readBoolean();
				if (successful) {
					System.out.println("Message sent successfully.");
				}
				else {
					System.out.println("Error: User \"" + destinatario + "\" does not exist.");
				}
			}
			else if ((command[0].equals("read") || command[0].equals("r")) && command.length == 1) {
				outStream.writeObject(command[0]);
				outStream.flush();
				String messages = (String) inStream.readObject();
				System.out.print(messages);
			}
			else if ((command[0].equals("quit") || command[0].equals("q")) && command.length == 1) {
				System.out.println("Quitting...");
				stop = true;
			}
			else {
				System.out.println("Error: Wrong command or wrong number of arguments.");
			}
		}
		input.close();
		inStream.close();
		outStream.close();
		socket.close();
	}
	
}


















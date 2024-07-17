package grupoSecreto.mavenproject;

/**
 * @author Grupo 10
 * @author Henrique Catarino - 56278
 * @author JoÃ£o OsÃ³rio - 56353
 * @author Vasco Maria - 56374
 */


import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;


public class TintolmarketServer {
	
	private static final String URL = "jdbc:sqlite:database.db";
    // Definir consultas SQL para autenticação e obtenção de senha
    private static final String QUERY_AUTHENTICATE = "SELECT password FROM users WHERE username = ?";

	
	
	public static void main(String[] args) throws IOException {
		int port = 12345;
		if (args.length != 0 ) {
			port = Integer.parseInt(args[0]);
		}
		TintolmarketServer server = new TintolmarketServer();
		server.startServer(port);
	}

	@SuppressWarnings("resource")
	public void startServer(int port) {
		ServerSocket sSoc = null;
		try {
			sSoc = new ServerSocket(port);
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(-1);
		}	
		while (true) {
			try {
				Socket inSoc = sSoc.accept();
				ServerThread newServerThread = new ServerThread(inSoc);
				newServerThread.start();
		    }
		    catch (IOException e) {
		        e.printStackTrace();
		    }
		}
	}
	
	private class ServerThread extends Thread {

		private Socket socket;

		ServerThread(Socket inSoc) {
			socket = inSoc;
		}
 
		public void run() {
			boolean stop = false;
			try {
				ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());
				String user = (String) inStream.readObject();
				String password = (String) inStream.readObject();
				if (!authenticate(user, password)) {
					stop = true;
					outStream.writeBoolean(false);
				}
				else {
					outStream.writeBoolean(true);
				}
				outStream.flush();
				while (!stop) {
					String command = (String) inStream.readObject();
					 if (command.equals("talk") || command.equals("t")) {
						boolean success = saveMessage(user, (String) inStream.readObject(),
								(String) inStream.readObject());
						outStream.writeBoolean(success);
					}
					else if (command.equals("read") || command.equals("r")) {
						String messages = getMessages(user);
						outStream.writeObject(messages);
					}
					outStream.flush();
				}
				outStream.close();
				inStream.close();
				socket.close();
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
 
	}
	
	private static boolean authenticate(String username, String password) {
		String url = "jdbc:sqlite:database.db";
	
		// Primeiro, verifique se a tabela users existe e crie-a se não existir
		String sqlCreateTable = """
			CREATE TABLE IF NOT EXISTS users (
				id INTEGER PRIMARY KEY AUTOINCREMENT,
				username TEXT NOT NULL UNIQUE,
				password TEXT NOT NULL,
				messages TEXT
			);
		""";

	
		try (Connection conn = DriverManager.getConnection(url);
			 Statement stmt = conn.createStatement()) {
			
			// Cria a tabela users, se não existir
			stmt.execute(sqlCreateTable);
		} catch (SQLException e) {
			System.out.println("Erro ao criar a tabela users: " + e.getMessage());
			return false;
		}
	
		// Lógica para verificar a senha ou inserir um novo usuário
		String queryCheck = "SELECT password FROM users WHERE username = '" + username + "' AND password = '" + password + "'";
		String insertUser = "INSERT INTO users(username, password) VALUES(?, ?)";
	
		// Após verificar ou inserir, imprima todos os usuários para teste
		try (Connection conn = DriverManager.getConnection(url);
			 Statement stmt = conn.createStatement();
			 ResultSet rs = stmt.executeQuery("SELECT * FROM users")) {
	
			System.out.println("Usuários atualmente no banco de dados:");
			while (rs.next()) {
				// Supondo que sua tabela tenha as colunas 'id', 'username', e 'password'
				int id = rs.getInt("id");
				String foundUser = rs.getString("username");
				String foundPassword = rs.getString("password"); // Lembre-se: Armazenar senhas em texto plano não é seguro!
				System.out.println("ID: " + id + ", Username: " + foundUser + ", Password: " + foundPassword);
			}
		} catch (SQLException e) {
			System.out.println("Erro ao buscar usuários: " + e.getMessage());
		}
	
		try (Connection conn = DriverManager.getConnection(url);
			 Statement stmt = conn.createStatement();
			 ResultSet rs = stmt.executeQuery(queryCheck)) {
	
			if (rs.next()) { // Se houver uma linha correspondente na consulta, o usuário é autenticado
				System.out.println("Usuário autenticado com sucesso.");
				return true;
			} else { 
				try (PreparedStatement pstmtInsert = conn.prepareStatement(insertUser)) {
	                pstmtInsert.setString(1, username);
	                pstmtInsert.setString(2, password);
	                pstmtInsert.executeUpdate();
	                System.out.println("Novo usuário criado com sucesso.");
	                return true; // Novo usuário criado
	            }
			}
		} catch (SQLException e) {
			System.out.println("Erro no banco de dados: " + e.getMessage());
			return false; // Retorna false em caso de erro
		}
	}
	

	
	private synchronized boolean saveMessage(String user, String username, String message) {
		Connection connection = null;
		Statement statement = null;
		try {
			// Estabelecer conexão com o banco de dados SQLite
			connection = DriverManager.getConnection("jdbc:sqlite:database.db");
	
			// Verificar se o usuário existe na tabela 'users' (susceptível a SQL injection)
			statement = connection.createStatement();
			
	
			// Consulta SQL para obter a mensagem atual na coluna 'messages' para o usuário correspondente
			String selectQuery = "SELECT messages FROM users WHERE username = '" + username + "'";
			ResultSet resultSet = statement.executeQuery(selectQuery);
	
			String currentMessage = "";
			if (resultSet.next()) {
				currentMessage = resultSet.getString("messages");
			}
	
			// Consulta SQL para atualizar a coluna 'messages' na tabela 'users' para adicionar a nova mensagem
			String updateQuery;
			if (currentMessage != null) {
				updateQuery = "UPDATE users SET messages = '" + currentMessage +  user + ":" + message + "; \n' WHERE username = '" + username + "'";
			} else {
				updateQuery = "UPDATE users SET messages = '" + user + ":" + message + "; \n' WHERE username = '" + username + "'";
			}
			int rowsAffected = statement.executeUpdate(updateQuery);
	
			// Verificar se a atualização foi bem-sucedida
			if (rowsAffected > 0) {
				return true;
			}
		} catch (SQLException e) {
			// Lidar com exceções SQL
			e.printStackTrace();
		} finally {
			// Fechar recursos
			try {
				if (statement != null) {
					statement.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				// Lidar com exceções de fechamento de recursos
				e.printStackTrace();
			}
		}
	
		return false;
	}
	
	
	private synchronized String getMessages(String user) {
		StringBuilder messages = new StringBuilder();
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			// Estabelecer conexão com o banco de dados SQLite
			connection = DriverManager.getConnection("jdbc:sqlite:database.db");
	
			// Consulta SQL para obter as mensagens da coluna 'messages' para o usuário correspondente
			String selectQuery = "SELECT messages FROM users WHERE username = ?";
			preparedStatement = connection.prepareStatement(selectQuery);
			preparedStatement.setString(1, user);
			ResultSet resultSet = preparedStatement.executeQuery();
			
			// Verificar se o usuário existe na tabela 'users'
			if (resultSet.next()) {
				// Obter as mensagens da coluna 'messages'
				String messagesColumn = resultSet.getString("messages");
				
				messages.append(messagesColumn);
				
				// Atualizar a coluna 'messages' para vazia
				String updateQuery = "UPDATE users SET messages = '' WHERE username = ?";
				preparedStatement = connection.prepareStatement(updateQuery);
				preparedStatement.setString(1, user);
				preparedStatement.executeUpdate();
			}
		} catch (SQLException e) {
			// Lidar com exceções SQL
			e.printStackTrace();
		} finally {
			// Fechar recursos
			try {
				if (preparedStatement != null) {
					preparedStatement.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				// Lidar com exceções de fechamento de recursos
				e.printStackTrace();
			}
		}
	
		if (messages.toString().isEmpty()) {
			return "No Messages!\n";
		} else {
			return messages.toString();
		}
	}
	
}
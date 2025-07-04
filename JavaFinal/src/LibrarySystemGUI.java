import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LibrarySystemGUI {
	private Admin admin;
	private Student student;
	private Library library;
	private JFrame frame;
	private JPanel mainPanel;
	private JLabel statusLabel;
	private JTextField usernameField;
	private JPasswordField passwordField;
	private DefaultListModel<String> bookListModel;
	private DefaultListModel<String> requestedBooksModel;
	private JTextField searchField;
	private String username;

	public LibrarySystemGUI(Admin admin, Student student, Library library) {
		this.admin = admin;
		this.student = student;
		this.library = library;
		this.bookListModel = new DefaultListModel<>();
		this.requestedBooksModel = new DefaultListModel<>();

		frame = new JFrame("Library Management System");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setPreferredSize(new Dimension(600, 400));

		mainPanel = new JPanel(new BorderLayout(10, 10));
		mainPanel.setBackground(new Color(220, 240, 255));

		addWelcomePanel();
		frame.setContentPane(mainPanel);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	// Welcome/Login Panel
	private void addWelcomePanel() {
		JPanel loginPanel = new JPanel();
		loginPanel.setLayout(new GridBagLayout());
		loginPanel.setBackground(new Color(220, 240, 255));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);

		JLabel welcomeLabel = new JLabel("Welcome to Library Management System");
		welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
		welcomeLabel.setHorizontalAlignment(JLabel.CENTER);

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		loginPanel.add(welcomeLabel, gbc);

		gbc.gridwidth = 1;
		gbc.gridx = 0;
		gbc.gridy = 1;
		loginPanel.add(new JLabel("Username:"), gbc);

		usernameField = new JTextField(15);
		gbc.gridx = 1;
		loginPanel.add(usernameField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		loginPanel.add(new JLabel("Password:"), gbc);

		passwordField = new JPasswordField(15);
		gbc.gridx = 1;
		loginPanel.add(passwordField, gbc);

		JButton loginButton = new JButton("Login");
		loginButton.setBackground(new Color(30, 144, 255));
		loginButton.setForeground(Color.WHITE);
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 2;
		loginPanel.add(loginButton, gbc);

		statusLabel = new JLabel(" ");
		statusLabel.setHorizontalAlignment(JLabel.CENTER);
		statusLabel.setForeground(Color.RED);
		gbc.gridy = 4;
		loginPanel.add(statusLabel, gbc);

		loginButton.addActionListener(e -> handleLogin());

		mainPanel.add(loginPanel, BorderLayout.CENTER);
	}

	private void handleLogin() {
		username = usernameField.getText();
		String password = new String(passwordField.getPassword());
		User currentUser = null;

		if (admin.login(username, password, "1")) {
			currentUser = admin;
		} else if (student.login(username, password, "2")) {
			currentUser = student;
		} else {
			statusLabel.setText("Invalid username or password.");
			return;
		}

		if (currentUser instanceof Admin) {
			showAdminUI();
		} else if (currentUser instanceof Student) {
			showStudentUI();
		}
	}

	// Admin UI
	private void showAdminUI() {
		library.loadBooksFromDatabase();
		bookListModel.clear();
		for (Book book : library.getBooks()) {
			bookListModel.addElement(formatBook(book));
		}

		JPanel adminPanel = new JPanel(new BorderLayout(10, 10));
		adminPanel.setBackground(new Color(220, 240, 255));

		JLabel adminLabel = new JLabel("Admin Panel", JLabel.CENTER);
		adminLabel.setFont(new Font("Arial", Font.BOLD, 16));
		adminPanel.add(adminLabel, BorderLayout.NORTH);

		JList<String> bookList = new JList<>(bookListModel);
		JScrollPane scrollPane = new JScrollPane(bookList);
		adminPanel.add(scrollPane, BorderLayout.CENTER);

		JButton addBookButton = new JButton("Add Book");
		addBookButton.addActionListener(e -> showAddBookDialog());
		JPanel btnPanel = new JPanel();
		btnPanel.add(addBookButton);
		adminPanel.add(btnPanel, BorderLayout.SOUTH);

		mainPanel.removeAll();
		mainPanel.add(adminPanel, BorderLayout.CENTER);
		mainPanel.revalidate();
		mainPanel.repaint();
	}

	private void showAddBookDialog() {
		JTextField titleField = new JTextField();
		JTextField authorField = new JTextField();
		JPanel panel = new JPanel(new GridLayout(2, 2));
		panel.add(new JLabel("Book Title:"));
		panel.add(titleField);
		panel.add(new JLabel("Author:"));
		panel.add(authorField);

		int result = JOptionPane.showConfirmDialog(frame, panel, "Add Book", JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.OK_OPTION) {
			String title = titleField.getText().trim();
			String author = authorField.getText().trim();
			if (title.isEmpty() || author.isEmpty()) {
				JOptionPane.showMessageDialog(frame, "Please fill in all fields.");
				return;
			}
			Book newBook = new Book(library.getNextBookId(), title, author);
			library.addBook(newBook);
			library.addBookToDatabase(newBook);
			bookListModel.addElement(formatBook(newBook));
			JOptionPane.showMessageDialog(frame, "Book added successfully!");
		}
	}

	// Student UI
	private void showStudentUI() {
		requestedBooksModel.clear();
		requestedBooksModel.addAll(library.getAllRequestedBooks(username));

		JPanel studentPanel = new JPanel(new BorderLayout(10, 10));
		studentPanel.setBackground(new Color(255, 255, 220));

		JLabel studentLabel = new JLabel("Student Panel", JLabel.CENTER);
		studentLabel.setFont(new Font("Arial", Font.BOLD, 16));
		studentPanel.add(studentLabel, BorderLayout.NORTH);

		// Search Panel
		JPanel searchPanel = new JPanel();
		searchField = new JTextField(15);
		JButton searchButton = new JButton("Search");
		searchPanel.add(new JLabel("Search Book:"));
		searchPanel.add(searchField);
		searchPanel.add(searchButton);
		studentPanel.add(searchPanel, BorderLayout.CENTER);

		// Requested Books
		JList<String> requestedBooksList = new JList<>(requestedBooksModel);
		JScrollPane requestedBooksScrollPane = new JScrollPane(requestedBooksList);
		requestedBooksScrollPane.setBorder(BorderFactory.createTitledBorder("Requested Books"));
		studentPanel.add(requestedBooksScrollPane, BorderLayout.SOUTH);

		searchButton.addActionListener(e -> handleBookSearch());

		mainPanel.removeAll();
		mainPanel.add(studentPanel, BorderLayout.CENTER);
		mainPanel.revalidate();
		mainPanel.repaint();
	}

	private void handleBookSearch() {
		String query = searchField.getText().trim();
		if (query.isEmpty()) {
			JOptionPane.showMessageDialog(frame, "Please enter a search query.");
			return;
		}
		List<Book> searchResults = library.searchBooks(query);
		showSearchResultsDialog(searchResults);
	}

	private void showSearchResultsDialog(List<Book> searchResults) {
		DefaultListModel<String> searchResultsModel = new DefaultListModel<>();
		for (Book book : searchResults) {
			searchResultsModel.addElement(formatBook(book));
		}
		JList<String> searchResultsList = new JList<>(searchResultsModel);
		JScrollPane scrollPane = new JScrollPane(searchResultsList);

		JButton requestButton = new JButton("Request Book");
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(new JLabel("Search Results:"), BorderLayout.NORTH);
		panel.add(scrollPane, BorderLayout.CENTER);
		panel.add(requestButton, BorderLayout.SOUTH);

		JDialog dialog = new JDialog(frame, "Search Results", true);
		dialog.setContentPane(panel);
		dialog.setSize(350, 250);
		dialog.setLocationRelativeTo(frame);

		requestButton.addActionListener(e -> {
			String selectedBook = searchResultsList.getSelectedValue();
			if (selectedBook == null) {
				JOptionPane.showMessageDialog(dialog, "Please select a book to request.");
				return;
			}
			if (requestedBooksModel.contains(selectedBook)) {
				JOptionPane.showMessageDialog(dialog, "Book already requested.");
			} else {
				requestedBooksModel.addElement(selectedBook);
				library.requestBook(selectedBook, username);
				JOptionPane.showMessageDialog(dialog, "Book requested successfully!");
			}
		});

		dialog.setVisible(true);
	}

	private String formatBook(Book book) {
		return String.format("%d - %s by %s", book.getId(), book.getTitle(), book.getAuthor());
	}

	// --- Optionally keep this if you want to run GUI directly ---
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			Admin admin = new Admin("admin", "admin123", "admin");
			Student student = new Student("student", "student123", "student");
			Library library = new Library();
			new LibrarySystemGUI(admin, student, library);
		});
	}
}
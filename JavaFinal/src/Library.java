import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Library {
    private List<Book> books = new ArrayList<>();
    private int nextBookId = 1; 

    public Library() {
        loadBooksFromDatabase();
    }

    public List<Book> searchBooks(String query) {
        List<Book> searchResults = new ArrayList<>();

        for (Book book : books) {
            if (book.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                book.getAuthor().toLowerCase().contains(query.toLowerCase())) {
                searchResults.add(book);
            }
        }

        return searchResults;
    }

    public void addBook(Book book) {
        book.setId(nextBookId++);
        books.add(book);
        addBookToDatabase(book);
    }

    public void addBookToDatabase(Book book) {
        String sql = "INSERT INTO books (title, author) VALUES (?, ?)";
        
        try (Connection connection = Database.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, book.getTitle());
                statement.setString(2, book.getAuthor());
                statement.executeUpdate();
                connection.commit();
            } catch (SQLException e) {
                e.printStackTrace();
                
                connection.rollback();
            } finally {
               
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadBooksFromDatabase() {
        
        books.clear();

        String sql = "SELECT * FROM books";

        try (Connection connection = Database.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");
                books.add(new Book(id, title, author));
                nextBookId = Math.max(nextBookId, id + 1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void displayBooks() {
        for (Book book : books) {
            System.out.println("ID: " + book.getId() + ", Title: " + book.getTitle() + ", Author: " + book.getAuthor());
        }
    }

    public List<String> getAllRequestedBooks(String username) {
        List<String> requestedBooks = new ArrayList<>();
        try (Connection connection = Database.getConnection()) {
            String sql = "SELECT book_title FROM requested_books WHERE username = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, username);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        requestedBooks.add(resultSet.getString("book_title"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            
        }
        return requestedBooks;
    }
    public void requestBook(String title, String username) {
        try (Connection connection = Database.getConnection()) {
            
            String sql = "INSERT INTO requested_books (book_title, username) VALUES (?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, title);
                statement.setString(2, username);
                // Execute the statement
                statement.executeUpdate();
            }


            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace(); 
        }
    }



   

 
    public List<Book> getBooks() {
        return books;
    }

    public int getNextBookId() {
        return nextBookId++;
    }

    public boolean containsBook(String title) {
        for (Book book : books) {
            if (book.getTitle().equals(title)) {
                return true;
            }
        }
        return false;
    }
}

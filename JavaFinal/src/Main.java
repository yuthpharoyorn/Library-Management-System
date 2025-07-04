import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
           

            
            Admin admin = new Admin("admin", "admin123", "admin");
            Student student = new Student("student", "student123", "student");

            
            Library library = new Library();


            Book book1 = new Book(1, "Book1", "Author1");
            Book book2 = new Book(2, "Book2", "Author2");

            
            library.addBook(book1);
            library.addBook(book2);

        
            JFrame frame = new JFrame("Library System");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

           
            LibrarySystemGUI librarySystemGUI = new LibrarySystemGUI(admin, student, library);

            
            frame.getContentPane().add(librarySystemGUI.getMainPanel());

            
            frame.pack();
            frame.setLocationRelativeTo(null); 
            frame.setVisible(true);
        });
    }
}

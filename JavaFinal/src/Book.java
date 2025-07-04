public class Book {
    private String title;
    private String author;
    private int id;
    

    public Book(int id, String title, String author) {
        this.id = id;
        this.title = title;
        this.author = author;
    }

  

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getId() {
        return id;
    }

}

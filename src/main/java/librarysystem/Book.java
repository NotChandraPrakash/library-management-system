package librarysystem;                 //Usage of librarysystem package


//public class Book is defined
public class Book {
    private int bookId;
    private String title;
    private String publisher;
    private String edition;
    private int copies;

    //Constructors
    public Book(int bookId, String title, String publisher, String edition, int copies) {
        this.bookId = bookId;
        this.title = title;
        this.publisher = publisher;
        this.edition = edition;
        this.copies = copies;
    }

    public int getBookId() {
        return bookId;
    }

    public String getTitle() {
        return title;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getEdition() {
        return edition;
    }

    public int getCopies() {
        return copies;
    }

    public void setCopies(int copies) {
        this.copies = copies;
    }

    @Override
    public String toString() {
        return "Book ID: " + bookId + ", Title: " + title +
                ", Publisher: " + publisher +
                ", Edition: " + edition +
                ", Copies: " + copies;
    }
}



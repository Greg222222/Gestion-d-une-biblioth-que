package fr.greg;

public class Book {
    private String title;
    private String author;
    private int pageNumber;
    private boolean isBorrowed;


    // Default constructor
    public Book() {
        this.title = "";
        this.author = "";
        this.pageNumber = 0;
        this.isBorrowed = false;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public boolean isBorrowed() {
        return isBorrowed;
    }
    public String getBorrowedStatus() {
        if (isBorrowed) {
            return "Emprunté";
        } else {
            return "Disponible";
        }
    }

    public void setBorrowed(boolean borrowed) {
        isBorrowed = borrowed;
    }
}

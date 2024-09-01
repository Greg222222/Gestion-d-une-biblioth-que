import java.util.ArrayList;

public class Library {
    private ArrayList<Book> books;

    public Library() {
        this.books = new ArrayList<>();
    }
    public void addBook(Book book) {
        books.add(book);
    }

    // Méthode pour afficher tous les livres dans la bibliothèque
    public void displayBooks() {
        if (books.isEmpty()) {
            System.out.println("La bibliothèque est vide.");
        } else {
            System.out.println("Liste des livres dans la bibliothèque :");
            for (int i = 0; i < books.size(); i++) {
                Book book = books.get(i);
                System.out.println(book.getTitle() + " par " + book.getAuthor() + ", " + book.getPageNumber() + "pages. " + "(" + book.getBorrowedStatus() + ")");
            }
        }
    }
    // Méthode pour obtenir le nombre total de livres
    public int getNumberOfBooks() {
        return books.size();
    }
}

import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {

    public static void menu (Scanner s, Library library) {

        int choice = 0;
        int min = 1;
        int max = 6;

        System.out.println("Bienvenue dans la bibliothèque ! Que voulez vous faire ?");
        System.out.println("1 : Ajouter un livre");
        System.out.println("2 : Afficher les livres disponibles");
        System.out.println("3 : Rechercher un livre par titre");
        System.out.println("4 : Emprunter un livre");
        System.out.println("5 : Retourner un livre");
        System.out.println("6 : Quitter");

        boolean validInput = false;


        while (!validInput) {
            try {
                choice = s.nextInt();
                s.nextLine(); // Consomme le caractère de nouvelle ligne après nextInt() (à toujours faire après un nextInt())
                if (choice >= min && choice <= max) {
                    validInput = true;
                }
                else {
                    System.out.println("Veuillez faire un choix compris entre " + min + "et " + max);
                }
            }
            catch (InputMismatchException e) {
                System.out.println("Veuillez faire un choix compris entre " + min + "et " + max);
                s.next();
            }
            switch (choice) {
                case 1:
                    String anotherBook = "oui";
                    while (anotherBook.equalsIgnoreCase("oui")) {
                        Book book = addBook(s);
                        library.addBook(book);

                        System.out.println("Voulez-vous ajouter un autre livre ? (oui/non)");
                        anotherBook = s.nextLine();
                    }
                    System.out.println("Retour au menu");
                    menu(s, library);
                    break;
                case 2:
                    // Afficher tous les livres dans la bibliothèque
                    library.displayBooks();
                    menu(s, library);
                    break;
                case 3:
                    // Logique de recherche d'un livre par titre (à implémenter)
                    menu(s, library);
                    break;
                case 4:
                    // Logique d'emprunt d'un livre (à implémenter)
                    menu(s, library);
                    break;
                case 5:
                    // Logique de retour d'un livre (à implémenter)
                    menu(s, library);
                    break;
                case 6:
                    System.out.println("Au revoir !");
                    s.close();
                    break;
                default:
                    System.out.println("Choix invalide.");
            }
        }

    }


    public static Book addBook (Scanner s){
        System.out.println("Quel est le nom du livre à ajouter ?");
        String title = s.nextLine();

        System.out.println("Quel est le nom de l'auteur ?");
        String author = s.nextLine();

        int pageNumber = 0;
        // Boucle pour obtenir un nombre positif
        boolean validInput = false;
        while (!validInput) {
            try {
                System.out.println("Entrez le nombre de pages");
                pageNumber = s.nextInt();
                if (pageNumber > 0) {
                    validInput = true;
                } else {
                    System.out.println("Veuillez entrer un nombre positif");
                }
            } catch (InputMismatchException e) {
                System.out.println("Veuillez entrer un nombre valide");
                s.next(); // Consomme l'entrée invalide
            }
        }
        // Consommer le caractère de nouvelle ligne restant après nextInt()
        s.nextLine();
        // Par défaut, le livre n'est pas emprunté
        boolean isBorrowed = false;

        // Création du livre
        Book newBook = new Book();
        newBook.setTitle(title);
        newBook.setAuthor(author);
        newBook.setPageNumber(pageNumber);
        newBook.setBorrowed(isBorrowed);

        return newBook;
    }


    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        Library library = new Library();  // Créer une instance de la bibliothèque
        menu(s, library);





        s.close();

    }
}
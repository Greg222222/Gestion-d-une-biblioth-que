import fr.greg.*;

import java.util.*;
import java.text.Normalizer;
import java.util.regex.Pattern;

public class Main {

    public static class StringUtils {
        public static String removeAccents(String text) {
            String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
            Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
            return pattern.matcher(normalized).replaceAll("");
        }
    }

    public static void menu(Scanner s, Library library) {

        int choice = 0;
        int min = 1;
        int max = 7;

        boolean running = true;
        while (running) {
            System.out.println("Bienvenue dans la bibliothèque ! Elle contient "
                    + library.getNumberOfBooks() + " livre(s) ! Que voulez vous faire ?");
            System.out.println("1 : Ajouter un livre");
            System.out.println("2 : Afficher les livres disponibles");
            System.out.println("3 : Rechercher un livre par titre");
            System.out.println("4 : Emprunter un livre");
            System.out.println("5 : Retourner un livre");
            System.out.println("6 : Supprimer un livre de la bibliothèque");
            System.out.println("7 : Quitter");


            boolean validInput = false;


            while (!validInput) {
                try {
                    choice = s.nextInt();
                    s.nextLine(); // Consomme le caractère de nouvelle ligne après nextInt() (à toujours faire après un nextInt())
                    if (choice >= min && choice <= max) {
                        validInput = true;
                    } else {
                        System.out.println("Veuillez faire un choix compris entre " + min + "et " + max);
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Veuillez faire un choix compris entre " + min + "et " + max);
                    s.next();
                }

            }switch (choice) {
                case 1:
                    String anotherBook = "oui";
                    while (anotherBook.equalsIgnoreCase("oui")) {
                        Book book = addBook(s);
                        library.addBook(book);

                        System.out.println("Voulez-vous ajouter un autre livre ? (oui/non)");
                        anotherBook = s.nextLine();
                    }
                    System.out.println("Retour au menu");
                    break;
                case 2:
                    // Afficher tous les livres dans la bibliothèque
                    library.displayBooks();
                    break;
                case 3:
                    // Logique de recherche d'un livre par titre
                    searchBook(s, library);
                    break;
                case 4:
                    // Logique d'emprunt d'un livre
                    borrowBook(s, library);
                    break;
                case 5:
                    // Logique de retour d'un livre
                    returnBook(s, library);
                    break;
                case 6:
                    // Logique de suppression d'un livre de la Bibliothèque
                    removeBook(s, library);
                    break;
                case 7:
                    System.out.println("Au revoir !");
                    running = false;
                    break;
                default:
                    System.out.println("Choix invalide.");
            }
        }
    }

    public static Book addBook(Scanner s) {
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

    public static void borrowBook(Scanner s, Library library) {

        System.out.println("Quel livre souhaitez-vous emprunter ?");
        // créer une variable de sa réponse
        String answer = StringUtils.removeAccents(s.nextLine().toLowerCase());

        Map<String, Integer> authorsCountMap = new HashMap<>();  // Map pour stocker le nombre d'exemplaires par auteur
        Map<String, Integer> availableCountMap = new HashMap<>(); // Map pour stocker le nombre d'exemplaires disponibles par auteur
        Map<String, Book> firstAvailableBookMap = new HashMap<>();  // Map pour stocker le premier exemplaire non emprunté par auteur
        for (Book book : library.getBooks()) {
            String normalizedTitle = StringUtils.removeAccents(book.getTitle().toLowerCase());
            if (normalizedTitle.equalsIgnoreCase(answer)) {
                String author = book.getAuthor();
                // Compter les exemplaires par auteur (par défaut clef : author, valeur = 0) et si on trouve, on incrémente de 1.
                authorsCountMap.put(author, authorsCountMap.getOrDefault(author, 0) + 1);
                // Stocker le premier exemplaire non emprunté par auteur
                if (!book.isBorrowed()) {
                    availableCountMap.put(author, availableCountMap.getOrDefault(author, 0) + 1);
                    if (!book.isBorrowed() && !firstAvailableBookMap.containsKey(author)) {
                        firstAvailableBookMap.put(author, book);
                    }
                }
            }
        }

        if (authorsCountMap.isEmpty()) {
            System.out.println("Aucun exemplaire disponible de " + answer + " n'a été trouvé.");
            return;
        }

        Book selectedBook = null;

        if (authorsCountMap.size() > 1) {
            System.out.println("Plusieurs auteurs ont écrit un livre avec ce titre. Veuillez choisir un auteur :");
            int index = 1;
            List<String> authors = new ArrayList<>(authorsCountMap.keySet());

            for (Map.Entry<String, Integer> entry : authorsCountMap.entrySet()) {
                String author = entry.getKey();
                int count = authorsCountMap.get(author);
                int availableCount = availableCountMap.getOrDefault(author, 0);
                System.out.println(index + " : " + author + " (Il existe " + count + " exemplaire(s) dont " + availableCount + " de disponible(s))");
                index++;
            }
            int choice = -1;
            while (choice < 1 || choice > authors.size()) {
                System.out.println("Entrez le numéro correspondant à l'auteur que vous voulez choisir");
                try {
                    choice = s.nextInt();
                    s.nextLine();
                }
                catch (InputMismatchException e) {
                    System.out.println("Veuillez entrer un nombre valide");
                    s.next();
                }
            }String selectedAuthor = authors.get(choice - 1);
            selectedBook = firstAvailableBookMap.get(selectedAuthor);
        }
        else {
            // Si un seul auteur ou plusieurs exemplaires du même auteur, prendre le premier non emprunté
            String author = authorsCountMap.keySet().iterator().next();
            selectedBook = firstAvailableBookMap.get(author);
            System.out.println("Il existe " + authorsCountMap.get(author) + " exemplaire(s) de " + author + ".");
        }

        // Si aucun exemplaire disponible n'a été trouvé
        if (selectedBook == null) {
            System.out.println("Tous les exemplaires de " + answer + " sont actuellement empruntés.");
            return;
        }

        // Vérifier que le livre sélectionné n'est pas déjà emprunté
        System.out.println("Voulez-vous emprunter " + selectedBook.getTitle() + " de " + selectedBook.getAuthor() + " ? (oui/non)");
        String confirmation = s.nextLine();
        if (confirmation.equalsIgnoreCase("oui")) {
            selectedBook.setBorrowed(true);  // Marquer le livre comme emprunté
            System.out.println("Vous avez emprunté " + selectedBook.getTitle() + " de " + selectedBook.getAuthor() + ".");
        } else {
            System.out.println("Emprunt annulé.");
        }
    }

    public static class BookCounts {
        Map<String, Integer> authorsCountMap = new HashMap<>();
        Map<String, Integer> unavailableCountMap = new HashMap<>();
        Map<String, Book> firstUnavailableBookMap = new HashMap<>();
        Map<String, Integer> availableCountMap = new HashMap<>();
        Map<String, Book> firstAvailableBookMap = new HashMap<>();
    }

    public static BookCounts countUnavailableBook(Scanner s, Library library) {
        String answer = StringUtils.removeAccents(s.nextLine().toLowerCase());
        BookCounts counts = new BookCounts();
        Map<String, Integer> authorsCountMap = new HashMap<>();  // Map pour stocker le nombre d'exemplaires par auteur
        Map<String, Integer> unavailableCountMap = new HashMap<>(); // Map pour stocker le nombre d'exemplaires disponibles par auteur
        Map<String, Book> firstUnavailableBookMap = new HashMap<>();  // Map pour stocker le premier exemplaire non emprunté par auteur
        for (Book book : library.getBooks()) {
            String normalizedTitle = StringUtils.removeAccents(book.getTitle().toLowerCase());
            if (normalizedTitle.equalsIgnoreCase(answer)) {
                String author = book.getAuthor();
                // Compter les exemplaires par auteur (par défaut clef : author, valeur = 0) et si on trouve, on incrémente de 1.
                authorsCountMap.put(author, authorsCountMap.getOrDefault(author, 0) + 1);
                // Stocker le premier exemplaire non emprunté par auteur
                if (book.isBorrowed()) {
                    unavailableCountMap.put(author, unavailableCountMap.getOrDefault(author, 0) + 1);
                    if (!firstUnavailableBookMap.containsKey(author)) {
                        firstUnavailableBookMap.put(author, book);
                    }
                }
            }
        }
        if (authorsCountMap.isEmpty()) {
            System.out.println("Aucun exemplaire de " + answer + " n'a été trouvé.");
        }
        return counts;
    }

    public static BookCounts countAvailableBook(String answer, Library library) {
        BookCounts counts = new BookCounts();
        // Initialisation des variables
        Map<String, Integer> authorsCountMap = new HashMap<>();
        Map<String, Integer> availableCountMap = new HashMap<>();
        Map<String, Book> firstAvailableBookMap = new HashMap<>();

        // Parcourir tous les livres de la bibliothèque
        for (Book book : library.getBooks()) {
            String normalizedTitle = StringUtils.removeAccents(book.getTitle().toLowerCase());
            if (normalizedTitle.contains(answer)) {
                String author = book.getAuthor();

                // Mettre à jour le nombre total de livres par auteur
                authorsCountMap.put(author, authorsCountMap.getOrDefault(author, 0) + 1);

                // Mettre à jour le nombre de livres disponibles par auteur
                if (!book.isBorrowed()) {
                    availableCountMap.put(author, availableCountMap.getOrDefault(author, 0) + 1);
                    if (!firstAvailableBookMap.containsKey(author)) {
                        firstAvailableBookMap.put(author, book);
                    }
                }
            }
        }
        if (authorsCountMap.isEmpty()) {
            System.out.println("Aucun exemplaire de " + answer + " n'a été trouvé.");
        }
        // Affichage pour debug
        System.out.println("Debug: authorsCountMap = " + authorsCountMap);
        System.out.println("Debug: availableCountMap = " + availableCountMap);
        System.out.println("Debug: firstAvailableBookMap = " + firstAvailableBookMap);

        // Assigner les maps à l'objet counts
        counts.authorsCountMap = authorsCountMap;
        counts.availableCountMap = availableCountMap;
        counts.firstAvailableBookMap = firstAvailableBookMap;
        return counts;
    }

    public static void returnBook(Scanner s, Library library) {

        System.out.println("Quel livre souhaitez-vous rendre ?");
        // créer une variable de sa réponse
        String answer = StringUtils.removeAccents(s.nextLine().toLowerCase());
        BookCounts counts = countUnavailableBook(s, library);

        Book selectedBook = null;

        // Si aucun exemplaire disponible n'a été trouvé
        if (selectedBook == null) {
            System.out.println("Aucun exemplaire de " + answer + " n'est actuellement emprunté.");
            return;
        }

        if (counts.authorsCountMap.size() > 1) {
            System.out.println("Plusieurs auteurs ont écrit un livre avec ce titre. Veuillez choisir un auteur :");
            int index = 1;
            List<String> authors = new ArrayList<>(counts.authorsCountMap.keySet());

            for (Map.Entry<String, Integer> entry : counts.authorsCountMap.entrySet()) {
                String author = entry.getKey();
                int count = counts.authorsCountMap.get(author);
                int unavailableCount = counts.unavailableCountMap.getOrDefault(author, 0);
                System.out.println(index + " : " + author + " (Il existe " + count + " exemplaire(s) dont " + unavailableCount + " d'emprunté(s))");
                index++;
            }
            int choice = -1;
            while (choice < 1 || choice > authors.size()) {
                System.out.println("Entrez le numéro correspondant à l'auteur que vous voulez choisir");
                try {
                    choice = s.nextInt();
                    s.nextLine();
                }
                catch (InputMismatchException e) {
                    System.out.println("Veuillez entrer un nombre valide");
                    s.next();
                }
            }String selectedAuthor = authors.get(choice - 1);
            selectedBook = counts.firstUnavailableBookMap.get(selectedAuthor);
        }
        else {
            // Si un seul auteur ou plusieurs exemplaires du même auteur, prendre le premier non emprunté
            String author = counts.authorsCountMap.keySet().iterator().next();
            selectedBook = counts.firstUnavailableBookMap.get(author);
            System.out.println("Il existe " + counts.authorsCountMap.get(author) + " exemplaire(s) de " + author + ".");
        }


        // Vérifier que le livre sélectionné n'est pas déjà emprunté
        System.out.println("Voulez-vous rendre " + selectedBook.getTitle() + " de " + selectedBook.getAuthor() + " ? (oui/non)");
        String confirmation = s.nextLine();
        if (confirmation.equalsIgnoreCase("oui")) {
            selectedBook.setBorrowed(false);  // Marquer le livre comme emprunté
            System.out.println("Vous avez rendu " + selectedBook.getTitle() + " de " + selectedBook.getAuthor() + ".");
        } else {
            System.out.println("Retour annulé.");
        }
    }

    public static void searchBook(Scanner s, Library library) {
        System.out.println("Quel livre cherchez-vous ?");

        String answer = StringUtils.removeAccents(s.nextLine().toLowerCase());
        BookCounts counts = countAvailableBook(answer, library);

        Book selectedBook = null;
        String selectedAuthor ="";
        if (counts.authorsCountMap.size() > 1) {
            System.out.println("Plusieurs auteurs ont écrit un livre avec ce titre. Veuillez choisir un auteur :");
            List<String> authors = new ArrayList<>(counts.authorsCountMap.keySet());

            int choice = -1;
            while (choice < 1 || choice > authors.size()) {
                System.out.println("Entrez le numéro correspondant à l'auteur que vous voulez choisir :");
                for (int i = 0; i < authors.size(); i++) {
                    System.out.println((i + 1) + ". " + authors.get(i));
                }
                try {
                    choice = s.nextInt();
                    s.nextLine(); // Pour nettoyer le buffer
                } catch (InputMismatchException e) {
                    System.out.println("Veuillez entrer un nombre valide.");
                    s.next(); // Pour nettoyer l'entrée incorrecte
                }
            }

            selectedAuthor = authors.get(choice - 1);
        }
        // Récupération du livre correspondant à l'auteur sélectionné
        selectedBook = counts.firstAvailableBookMap.get(selectedAuthor);
        // Si aucun exemplaire n'a été trouvé
        if (selectedBook == null) {
            System.out.println("Aucun exemplaire de " + answer + " n'a été trouvé.");
        }
        else {
            System.out.println(
                    selectedBook.getTitle() + " a été écrit par " + selectedBook.getAuthor() +
                            " et comprend " + selectedBook.getPageNumber() + " pages. " +
                            "Il en existe " + counts.authorsCountMap.get(selectedBook.getAuthor()) + " exemplaire(s), dont " +
                            counts.availableCountMap.get(selectedBook.getAuthor()) + " de disponible(s).");
        }
    }

    public static void removeBook (Scanner s, Library library) {
        System.out.println("Quel livre voulez-vous supprimer de la bibliothèque ?");
        String answer = s.nextLine();
        boolean bookFound = false;

        for (Book book : library.getBooks()) {
            String normalizedTitle = StringUtils.removeAccents(book.getTitle().toLowerCase());
            if (normalizedTitle.equalsIgnoreCase(answer)) {
                System.out.println("Voulez-vous enlever " + book.getTitle() +
                        " de la bibliothèque ? (écrivez 'oui' pour confirmer)");
                String confirmation = s.nextLine();
                if (confirmation.equalsIgnoreCase("oui")) {
                    library.deleteBook(book);  // Supprimer le livre
                    System.out.println("Vous avez enlevé " + book.getTitle() +
                            " de la bibliothèque.");
                } else {
                    System.out.println("Suppression annulé.");
                }
                break;
            }
            if (!bookFound) {
                System.out.println("Le livre " + answer + "n'a pas été trouvé");
            }
        }
    }


    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        Library library = new Library();  // Créer une instance de la bibliothèque
        menu(s, library);


        s.close();

    }
}
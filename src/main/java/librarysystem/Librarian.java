package librarysystem;              //Package library system is initialized here


//Librarian class defined here
public class Librarian {
    private int librarianId;
    private String name;

    //Constructors
    public Librarian(int librarianId, String name) {
        this.librarianId = librarianId;
        this.name = name;
    }

    //Getters
    public int getLibrarianId() { return librarianId; }
    public String getName() { return name; }

    //Usage of toString()
    public String toString() {
        return "Librarian[ID=" + librarianId + ", Name=" + name + "]";
    }
}




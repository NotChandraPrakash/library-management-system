# 🏛️ Digital Library Management System

> A modern JavaFX-based library management application with MySQL database integration, featuring role-based access for librarians and students.

![Java](https://img.shields.io/badge/Java-23-ED8B00?style=for-the-badge&logo=java)
![JavaFX](https://img.shields.io/badge/JavaFX-21.0.1-4DB33D?style=for-the-badge)
![Maven](https://img.shields.io/badge/Maven-4.0.0-C71A36?style=for-the-badge&logo=apache-maven)
![MySQL](https://img.shields.io/badge/MySQL-Latest-00758F?style=for-the-badge&logo=mysql)
![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)

---

## 📋 Table of Contents

- [Features](#features)
- [Demo](#demo)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Configuration](#configuration)
- [Usage](#usage)
- [Project Structure](#project-structure)
- [Technology Stack](#technology-stack)
- [Database Schema](#database-schema)
- [Future Enhancements](#future-enhancements)
- [Contributing](#contributing)
- [License](#license)

---

## ✨ Features

### 🔐 Authentication
- Role-based login system (Librarian & Student)
- Secure password management
- Default credentials for quick testing

### 📚 Librarian Dashboard (6 Tabs)
- ✅ **View Books** - Browse all available books with details
- ✅ **Add Book** - Register new books to the library
- ✅ **Remove Book** - Remove books from inventory
- ✅ **Issued Books** - Track all currently issued books
- ✅ **Add Student** - Register new students in the system
- ✅ **Change Password** - Update account credentials

### 👨‍🎓 Student Dashboard (5 Tabs)
- ✅ **Available Books** - Browse books available to borrow
- ✅ **Issue Book** - Request a book from the library
- ✅ **Return Book** - Return borrowed books with automatic fine calculation
- ✅ **My Issued Books** - View borrowed books with due dates and fine status
- ✅ **Change Password** - Update account credentials

### 💰 Fine Management
- Automatic fine calculation: **₹10 per day** after 7-day due date
- Real-time fine status display
- Transparent late fee information

### 🎨 UI/UX
- Modern gradient-based interface
- Professional color scheme
- Responsive desktop layout
- Intuitive navigation with emoji icons

---

## 📸 Demo

### Login Screen
```
┌─────────────────────────────────────┐
│   BABASAHEB BHIMRAO AMBEDKAR        │
│   UNIVERSITY - LUCKNOW              │
│   CENTRAL LIBRARY MANAGEMENT        │
├─────────────────────────────────────┤
│                                     │
│   Enter your credentials            │
│                                     │
│   ID: [         ]                   │
│   Password: [         ]             │
│   Role: ○ Librarian  ○ Student      │
│   [ LOGIN ]                         │
│                                     │
└─────────────────────────────────────┘
```

### Librarian Dashboard
```
Dashboard with 6 tabs:
📚 View Books | ➕ Add Book | ➖ Remove Book | 
📖 Issued Books | 👨‍🎓 Add Student | 🔑 Change Password
```

### Student Dashboard
```
Dashboard with 5 tabs:
📚 Available Books | 📖 Issue Book | 🔄 Return Book |
📋 My Issued Books | 🔑 Change Password
```

---

## 🚀 Quick Start

### Prerequisites
- **Java Development Kit (JDK)** 23 or higher
- **Apache Maven** 4.0.0 or higher
- **MySQL Server** (latest version)
- **IDE** (IntelliJ IDEA recommended)

### Installation

#### 1. Clone the Repository
```bash
git clone https://github.com/yourusername/digital-library-management-system.git
cd digital-library-management-system
```

#### 2. Set Up MySQL Database
```bash
# Create database
mysql -u root -p
CREATE DATABASE librarydb;
EXIT;
```

#### 3. Update Database Configuration
Edit `src/main/java/librarysystem/DatabaseManager.java`:
```java
private static final String USER = "root";          // Your MySQL username
private static final String PASSWORD = "root";      // Your MySQL password
private static final String URL = "jdbc:mysql://localhost:3306/librarydb?useSSL=false&serverTimezone=UTC";
```

#### 4. Build the Project
```bash
mvn clean install
```

#### 5. Run the Application
```bash
mvn javafx:run
```

Or run directly from IDE:
- Right-click `MainApp.java`
- Select "Run MainApp"

---

## ⚙️ Configuration

### Database Connection
Located in: `src/main/java/librarysystem/DatabaseManager.java`

```java
private static final String URL = "jdbc:mysql://localhost:3306/librarydb?useSSL=false&serverTimezone=UTC";
private static final String USER = "root";
private static final String PASSWORD = "root";
```

### Maven Dependencies
Located in: `pom.xml`

Key dependencies:
- **JavaFX:** UI Framework
- **MySQL Connector:** Database driver
- **Maven JavaFX Plugin:** Runtime configuration

---

## 🎯 Usage

### Default Credentials

#### Librarian Account
```
Role: Librarian
ID: 1
Password: 1
```

#### Student Accounts
```
Role: Student
ID: 101
Password: 101

ID: 102
Password: 102
```

### Sample Data
The application comes pre-loaded with:
- **3 Sample Books** (Java Programming, Database Design, Web Development)
- **2 Sample Students** (IDs: 101, 102)
- **1 Sample Librarian** (ID: 1)

### Workflows

#### 📚 Issuing a Book (Student)
1. Login as Student (ID: 101)
2. Go to "Issue Book" tab
3. Enter Book ID: `1001`
4. Click "Issue Book"
5. View in "My Issued Books" tab

#### 🔄 Returning a Book (Student)
1. Login as Student
2. Go to "Return Book" tab
3. Enter Book ID
4. System calculates fine automatically
5. Confirm return

#### ➕ Adding a Book (Librarian)
1. Login as Librarian (ID: 1)
2. Go to "Add Book" tab
3. Fill in book details:
   - Book ID
   - Title
   - Publisher
   - Edition
   - Number of copies
4. Click "Add Book"

---

## 📁 Project Structure

```
digital-library-management-system/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── MainApp.java                          # JavaFX Application entry point
│   │   │   ├── controllers/
│   │   │   │   ├── LoginController.java              # Authentication logic
│   │   │   │   ├── LibrarianDashboardController.java # Librarian operations
│   │   │   │   └── StudentDashboardController.java   # Student operations
│   │   │   └── librarysystem/
│   │   │       ├── DatabaseManager.java              # Data access layer
│   │   │       ├── Library.java                      # Business logic
│   │   │       ├── Book.java                         # Book model
│   │   │       ├── Student.java                      # Student model
│   │   │       └── Librarian.java                    # Librarian model
│   │   └── resources/
│   │       └── fxml/
│   │           ├── LoginView.fxml                    # Login UI
│   │           ├── LibrarianDashboard.fxml           # Librarian UI
│   │           └── StudentDashboard.fxml             # Student UI
│   │
│   └── test/
│       └── java/
│
├── pom.xml                                           # Maven configuration
├── README.md                                         # This file
└── .gitignore                                        # Git ignore rules
```

---

## 🛠️ Technology Stack

### Frontend
- **JavaFX 21.0.1** - Modern UI framework for Java
- **FXML** - XML-based UI markup language
- **CSS Styling** - UI customization

### Backend
- **Java 23** - Programming language
- **Maven 4.0.0** - Build and dependency management

### Database
- **MySQL 8.0+** - Relational database
- **JDBC** - Database connectivity

### Architecture
- **MVC Pattern** - Model-View-Controller architecture
- **Layered Architecture** - Separation of concerns
  - Presentation Layer (Controllers & FXML)
  - Business Logic Layer (Library.java)
  - Data Access Layer (DatabaseManager.java)
  - Model Layer (POJO classes)

---

## 💾 Database Schema

### Tables

#### `books`
```sql
CREATE TABLE books (
    book_id INT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    publisher VARCHAR(255) NOT NULL,
    edition VARCHAR(100) NOT NULL,
    copies INT NOT NULL DEFAULT 0
);
```

#### `students`
```sql
CREATE TABLE students (
    student_id INT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    department VARCHAR(100) NOT NULL,
    course VARCHAR(100) NOT NULL,
    password VARCHAR(100) DEFAULT NULL
);
```

#### `librarians`
```sql
CREATE TABLE librarians (
    librarian_id INT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    password VARCHAR(100) DEFAULT NULL
);
```

#### `issue_records`
```sql
CREATE TABLE issue_records (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    book_id INT NOT NULL,
    issue_date DATE NOT NULL,
    return_date DATE NULL,
    fine_amount INT DEFAULT 0,
    librarian_id INT NOT NULL,
    FOREIGN KEY (student_id) REFERENCES students(student_id),
    FOREIGN KEY (book_id) REFERENCES books(book_id),
    FOREIGN KEY (librarian_id) REFERENCES librarians(librarian_id)
);
```

---

## 🔮 Future Enhancements

- [ ] Cloud database integration (MongoDB/PostgreSQL)
- [ ] User registration system
- [ ] Book search and filtering
- [ ] Notification system for due dates
- [ ] Reservation system for books
- [ ] Fine payment integration
- [ ] Book recommendations
- [ ] Admin panel for system management
- [ ] Reports and analytics
- [ ] Email notifications
- [ ] Mobile app companion
- [ ] Barcode/QR code support

---

## 🐛 Troubleshooting

### Issue: "Database connection failed"
**Solution:**
- Ensure MySQL server is running
- Verify credentials in `DatabaseManager.java`
- Check database name is `librarydb`

### Issue: "JavaFX runtime components missing"
**Solution:**
```bash
mvn clean install
mvn javafx:run
```

### Issue: "FXML files not found"
**Solution:**
- Ensure resource files are in `src/main/resources/fxml/`
- Rebuild project: `mvn clean install`

---

## 📝 Code Examples

### Adding a Book
```java
Book newBook = new Book(1001, "Java Programming", "Pearson", "10th Edition", 5);
library.addBook(newBook);
// Output: ✅ Book added successfully!
```

### Issuing a Book
```java
library.issueBook(student, bookId, librarian);
// Automatically calculates due date (7 days from today)
// Updates database and reduces available copies
```

### Returning a Book
```java
library.returnBook(student, bookId, librarian);
// Calculates fine if late (₹10 per day after 7 days)
// Updates inventory and records return
```

---

## 📊 Statistics

- **Lines of Code:** ~2000+
- **Java Files:** 8
- **FXML Files:** 3
- **Database Tables:** 4
- **Features:** 15+
- **Test Cases:** Pre-loaded sample data

---

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Development Guidelines
- Follow Java naming conventions
- Write clear, self-documenting code
- Add comments for complex logic
- Test thoroughly before committing

---

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

```
MIT License

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.
```

---

## 👨‍💼 Author

**Chandra Prakash**
- GitHub: [@yourusername](https://github.com/yourusername)
- Email: your.email@example.com

---

## 🙏 Acknowledgments

- Babasaheb Bhimrao Ambedkar University, Lucknow
- JavaFX Community
- Stack Overflow community for solutions

---

## 📞 Support

For support, email your.email@example.com or open an issue in the repository.

---

## 🚀 Getting Help

- 📖 **Documentation:** See the docs folder
- 🐛 **Report Bugs:** Open an issue
- 💡 **Feature Requests:** Create a new discussion
- ❓ **Questions:** Check existing issues or ask in discussions

---

## 📈 Roadmap

### Version 1.0 (Current)
- ✅ Core library management features
- ✅ Role-based access control
- ✅ Fine calculation system
- ✅ Professional UI

### Version 2.0 (Planned)
- 🔜 Cloud database support
- 🔜 Advanced search filters
- 🔜 Notification system
- 🔜 Mobile companion app

### Version 3.0 (Future)
- 🔜 Machine learning recommendations
- 🔜 Advanced analytics
- 🔜 Multi-library support

---

## ⭐ Show Your Support

Give a ⭐️ if this project helped you!

---

<div align="center">

**Made with ❤️ by Chandra Prakash**

[⬆ back to top](#-digital-library-management-system)

</div>

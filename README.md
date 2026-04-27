# Code Smells
## 📚 Overview

This is project demonstrating **Code Smells** and their refactoring solutions in **Java Spring Boot**.

This repository also now includes the **Code Quality Dashboard** backend and frontend for static code analysis, metrics, smell detection, visualization, and analysis history.

## 🧩 Dashboard Project Layout

- `src/main/java/com/codesmells/analysis` - Spring Boot backend for analysis, persistence, and REST APIs
- `frontend/` - React + Vite dashboard UI with charts and history views
- `src/main/resources/application.properties` - backend database and CORS configuration

## ▶️ How to Run

### Backend

1. Start MySQL and create a database named `code_quality_dashboard`.
2. Set `DB_USERNAME` and `DB_PASSWORD` if your local MySQL credentials are different from `root` / `root`.
3. From the project root, run `mvn spring-boot:run`.
4. The API will be available at `http://localhost:8080`.

### Frontend

1. Open the `frontend` folder.
2. Install dependencies with `npm install`.
3. Start the UI with `npm run dev`.
4. Open `http://localhost:5173` in your browser.

### End-to-End Flow

1. Start the backend first.
2. Start the frontend.
3. Paste Java, Python, or C++ code into the editor or upload a source file.
4. Click **Analyze Code** to generate charts, smells, suggestions, and history records.

This repository is designed for to understand code quality issues and learn how to write better, more maintainable code.

## 🎯 Learning Objectives

After studying this repository, students will be able to:
- Identify common code smells in real-world Java code
- Understand why these patterns are problematic
- Apply appropriate refactoring techniques
- Write cleaner, more maintainable code
- Follow SOLID principles and best practices

## 🏗️ Project Structure

The code smells are organized into **five main categories**, each in its own package:

```
com.codesmells/
├── bloaters/              # Code that has grown too large
├── oopabusers/           # Misuse of OOP principles
├── changepreventers/     # Code that makes changes difficult
├── dispensables/         # Unnecessary code elements
└── couplers/             # Excessive coupling between code
```

### Each code smell includes:
- **`bad/` package**: Examples demonstrating the code smell
- **`good/` package**: Refactored examples showing the solution
- **Detailed JavaDoc**: Explaining the problem, why it's bad, and how to fix it
- **Teaching comments**: To help students and instructors

## 📖 Code Smell Categories

### 1. Bloaters 🎈
Code, methods, or classes that have grown so large they're hard to work with.

| Code Smell | Description | Refactoring |
|------------|-------------|-------------|
| **Long Method** | Methods that try to do too much | Extract Method |
| **Large Class** | Classes with too many responsibilities | Extract Class |
| **Primitive Obsession** | Using primitives instead of small objects | Replace Primitive with Object |
| **Long Parameter List** | Methods with too many parameters | Introduce Parameter Object |
| **Data Clumps** | Groups of data that always appear together | Extract Class |

**Location**: `com.codesmells.bloaters.*`

### 2. Object-Orientation Abusers 🔨
Incorrect application of object-oriented programming principles.

| Code Smell | Description | Refactoring |
|------------|-------------|-------------|
| **Switch Statements** | Complex switch/case statements | Replace with Polymorphism |
| **Temporary Field** | Fields used only in certain circumstances | Extract Class |
| **Refused Bequest** | Subclass doesn't use inherited methods | Replace Inheritance with Delegation |
| **Alternative Classes with Different Interfaces** | Classes do similar things with different method names | Rename Method, Extract Superclass |

**Location**: `com.codesmells.oopabusers.*`

### 3. Change Preventers 🚧
Code that makes it difficult to change the program.

| Code Smell | Description | Refactoring |
|------------|-------------|-------------|
| **Divergent Change** | One class changed in many ways for different reasons | Extract Class |
| **Shotgun Surgery** | Making a change requires many small changes across classes | Move Method, Move Field |
| **Parallel Inheritance Hierarchies** | Creating subclass requires creating subclasses elsewhere | Move Method, Move Field |

**Location**: `com.codesmells.changepreventers.*`

### 4. Dispensables ♻️
Code that is unnecessary and should be removed.

| Code Smell | Description | Refactoring |
|------------|-------------|-------------|
| **Duplicate Code** | Same code structure in multiple places | Extract Method, Pull Up Method |
| **Lazy Class** | Class that doesn't do enough to justify existence | Inline Class, Collapse Hierarchy |
| **Data Class** | Class with only fields and getters/setters | Encapsulate Field, Move Method |
| **Dead Code** | Code that is never executed | Delete it |
| **Speculative Generality** | Code for future features that may never be needed | Remove unused abstractions |

**Location**: `com.codesmells.dispensables.*`

### 5. Couplers 🔗
Code with excessive coupling between elements.

| Code Smell | Description | Refactoring |
|------------|-------------|-------------|
| **Feature Envy** | Method uses more features from other class | Move Method |
| **Inappropriate Intimacy** | Classes know too much about each other's internals | Move Method, Extract Class |
| **Message Chains** | Long chains of method calls (a.b().c().d()) | Hide Delegate |
| **Middle Man** | Class delegates most of its work to other classes | Remove Middle Man |

**Location**: `com.codesmells.couplers.*`

## 🚀 Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher
- IDE (IntelliJ IDEA, Eclipse, or VS Code recommended)

### Building the Project

```bash
# Clone the repository
git clone https://github.com/maliknabeel/code-smells-with-ai.git
cd code-smells-with-ai

# Build with Maven
mvn clean install

# Run the application
mvn spring-boot:run
```

## 📝 How to Use This Repository

### For Students:

1. **Start with one category** (e.g., Bloaters)
2. **Read the package-info.java** for category overview
3. **Study the BAD example first**:
   - Read the JavaDoc comments
   - Understand why the code is problematic
   - Try to identify the issues yourself
4. **Then study the GOOD example**:
   - See how the code is refactored
   - Understand the benefits
   - Compare with the bad version
5. **Try to practice**:
   - Write your own bad examples
   - Refactor them using the techniques shown

### For Instructors:

1. **Use as teaching material** in software engineering courses
2. **Demonstrate side-by-side** comparisons in class
3. **Give assignments**:
   - Ask students to identify code smells in the bad examples
   - Have them implement their own refactoring
   - Compare with the good examples
4. **Create exercises**:
   - Mix good and bad code and ask students to identify issues
   - Give intentionally smelly code to refactor
5. **Discussion topics**:
   - When is a code smell acceptable?
   - Trade-offs in refactoring decisions
   - Real-world scenarios

## 🔍 Example: Long Method

### Bad Example (`bloaters.longmethod.bad.OrderProcessorService`)
```java
public double processOrder(String customerId, List<String> items, String shippingAddress) {
    // 60+ lines of code doing:
    // - Validation
    // - Price calculation
    // - Discount calculation
    // - Tax calculation
    // - Shipping calculation
    // - Email notification
    // - Logging
}
```

**Problems**: Too long, hard to understand, hard to test, violates Single Responsibility Principle.

### Good Example (`bloaters.longmethod.good.OrderProcessorService`)
```java
public double processOrder(String customerId, List<String> items, String shippingAddress) {
    validateOrder(customerId, items, shippingAddress);
    double subtotal = calculateSubtotal(items);
    double discount = calculateDiscount(customerId, subtotal);
    double afterDiscount = subtotal - discount;
    double tax = calculateTax(shippingAddress, afterDiscount);
    double shippingCost = calculateShipping(items.size(), afterDiscount);
    double total = afterDiscount + tax + shippingCost;
    sendConfirmationEmail(customerId, subtotal, discount, tax, shippingCost, total);
    logOrder(customerId);
    return total;
}
```

**Benefits**: Clear, readable, each method has single responsibility, easy to test.

## 🎓 Key Principles

### SOLID Principles Applied:
- **S**ingle Responsibility Principle - Each class/method does one thing
- **O**pen/Closed Principle - Open for extension, closed for modification
- **L**iskov Substitution Principle - Subtypes must be substitutable
- **I**nterface Segregation Principle - Many specific interfaces > one general
- **D**ependency Inversion Principle - Depend on abstractions, not concretions

### Clean Code Principles:
- Methods should be small and focused
- Classes should be cohesive
- Code should be self-documenting
- Reduce duplication
- Keep coupling low, cohesion high

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍🎓 Target Audience

- Computer Science students
- Software Engineering students
- Junior developers learning code quality
- Anyone interested in writing better code

## 💡 Tips for Learning

1. **Don't rush** - Take time to understand each example
2. **Compare actively** - Always compare bad vs good side by side
3. **Code along** - Type the examples yourself, don't just read
4. **Question everything** - Ask "why is this better?"
5. **Practice** - Find code smells in your own projects
6. **Discuss** - Talk about examples with peers and instructors

## 🏆 Assessment Ideas for Instructors

### Assignments:
1. **Code Review Assignment**: Give students smelly code to review and identify issues
2. **Refactoring Assignment**: Provide bad code and ask students to refactor it
3. **Comparison Assignment**: Have students write before/after examples with explanations
4. **Real-World Assignment**: Ask students to find and fix code smells in open-source projects

### Exam Questions:
1. Identify the code smell in given code
2. Explain why it's problematic
3. Describe the refactoring technique to fix it
4. Write refactored code

---

**Happy Learning! 🎉**

*Remember: The best code is code that is easy to understand, easy to change, and easy to test.*

# Java Starter Exercises

Welcome! This is a **learning playground**, not a test. Each exercise below is a
small, self-contained program you'll build one step at a time. They're ordered
roughly from easiest to hardest, and each one layers a new idea on top of the
last — so do them in order, take your time, and expect to get stuck. Getting
stuck is where the learning happens.

## How to use these exercises

- Write each solution in its **own small class** with a `main` method that runs
  it, e.g. `FizzBuzz.java`.
- Compile and run from the terminal:

  ```bash
  javac FizzBuzz.java && java FizzBuzz
  ```

- **Run it, don't just write it.** Type a few different inputs and watch what
  happens. Does your program break on `0`, an empty string, or a negative
  number? Good — that's what you're practicing.
- **Use the hints progressively.** Each exercise has hints that get more
  specific. Try the exercise on your own first. If you're stuck for a while,
  read *Hint 1*. Still stuck? *Hint 2* narrows it further. The point is to push
  yourself a little further each time, not to peek immediately.
- The **Stretch** line at the end is optional. Only attempt it once the base
  version works — it's a tougher, more satisfying version of the same idea.
- **Break it on purpose.** Once your solution works, try feeding it weird input
  just to see what it does. Understanding the failure is real learning.

---

## Table of Contents

1. [Hello, Namer](#1-hello-namer)
2. [FizzBuzz](#2-fizzbuzz)
3. [Basic Calculator](#3-basic-calculator)
4. [Number Guessing Game](#4-number-guessing-game)
5. [Palindrome Checker](#5-palindrome-checker)
6. [Word Frequency Counter](#6-word-frequency-counter)
7. [Bank Account Class](#7-bank-account-class)
8. [Student Record Manager](#8-student-record-manager)
9. [Inventory System with Interfaces](#9-inventory-system-with-interfaces)
10. [File I/O: Gradebook](#10-file-io-gradebook)

---

## 1. Hello, Namer

Your very first program. Build a tiny app that asks for a name, reads it from
the keyboard, and greets that person back. It feels trivial — and it is! — but
it teaches you the machinery every program on this list depends on: reading
input, storing it, and printing it back out.

### The task

- Prompt the user to `Enter your name:`.
- Read the name they type.
- Greet them: `Hello, Ada!`

Example run:

```
Enter your name: Ada
Hello, Ada!
```

### Getting started

- Start with the skeleton: a `public class HelloNamer` (or similar) with a
  `public static void main(String[] args)`.
- Create a `Scanner` that reads from `System.in` — this object is how Java
  reads keyboard input.
- Use `nextLine()` to grab the whole line the user typed.
- Print the greeting using string concatenation (`"Hello, " + name + "!"`) or
  `System.out.printf("Hello, %s!", name)`.

### Hints

> [!TIP] **Hint 1** — To read input, you'll need to create a `Scanner`:
> `Scanner scanner = new Scanner(System.in);` (import it with
> `import java.util.Scanner;` at the top of your file).

> [!NOTE] **Hint 2** — `printf` uses format specifiers: `%s` stands in for a
> string. `printf("Hello, %s!", name)` prints the value of `name` where the
> `%s` sits.

### You'll learn here

- Setting up a class with a `main` method.
- `Scanner` and console input/output.
- Building strings with concatenation and `printf` format specifiers.

### Stretch challenge

Greet differently depending on the hour of day — `Good morning` before noon,
`Good evening` after. You'll need `java.time.LocalTime` (or
`LocalDateTime.now().getHour()`) and an `if`/`else` check.

[↑ Back to Table of Contents](#table-of-contents)

---

## 2. FizzBuzz

The classic programming interview warm-up, and for good reason: it squeezes
loops, arithmetic, and `if/else` ordering into one small program. You'll print
the numbers 1–100, but replace every third number with `Fizz`, every fifth with
`Buzz`, and every multiple of **both** with `FizzBuzz`.

### The task

For each number from 1 to 100, print:
- `Fizz` if it's a multiple of 3,
- `Buzz` if it's a multiple of 5,
- `FizzBuzz` if it's a multiple of **both**,
- otherwise, just the number itself.

### Getting started

- Use a `for` loop from 1 to 100.
- Test "is this a multiple of 3" with the modulo operator: `n % 3 == 0`.
- Write it left-to-right, thinking carefully about the **order** of your
  checks (see the note below).

> [!IMPORTANT] **The classic trap is ordering.** The most common bug is
> checking "is it a multiple of 3?" and "is it a multiple of 5?" first, and
> putting the both-case last. But 15 is a multiple of *both* — if you check the
> single cases first, 15 prints `FizzBuzz`'s rivals instead. Check the
> combined case (multiple of 3 **and** 5) **first**, or check `n % 15 == 0`.

### Hints

> [!TIP] **Hint 1** — "Multiple of both 3 and 5" can be written as
> `n % 15 == 0` (since 3 × 5 = 15), or with `&&`:
> `n % 3 == 0 && n % 5 == 0`.

> [!NOTE] **Hint 2** — If `15` prints `Fizz` or `Buzz` instead of `FizzBuzz`,
> your combined check is in the wrong position. Move it to the top of your
> `if/else if` chain.

### You'll learn here

- `for` loops.
- The `%` modulo operator.
- `if / else if / else` chaining, and why **order matters**.

### Stretch challenge

Refactor your logic into a `classify(int n)` method that returns a `String`.
Now you can call `classify(15)`, `classify(3)`, etc. from `main` and check each
result — a big step toward writing testable code.

[↑ Back to Table of Contents](#table-of-contents)

---

## 3. Basic Calculator

Build a simple calculator in the terminal. You prompt for a number, an operator,
and another number, then print the result. This one is all about handling
*unexpected* input gracefully — a normal calculator crashes on `10 / 0`, but
yours shouldn't.

### The task

- Ask for two numbers and an operator (`+`, `-`, `*`, `/`).
- Print the result.
- Handle these cases gracefully instead of crashing:
  - **Division by zero** — print a friendly error message.
  - **An unknown operator** — tell the user what's wrong.

### Getting started

- Read the first number, the operator, and the second number with your
  `Scanner`.
- Use a `switch` on the operator to pick the math, or a chain of `if/else`.
- Store numbers in a `double` (not `int`) so division produces a decimal —
  `5 / 2` should give `2.5`, not `2`.

> [!NOTE] **`int` vs `double`.** Integer division truncates: `5 / 2 == 2` in
> `int` math. Use `double` for numbers when you expect fractional results, and
> you'll get `2.5`. Watch for this gotcha, it trips everyone up.

### Hints

> [!TIP] **Hint 1** — The `switch` cases in Java look like
> `case '+': ... break;`. Match on the *character* rather than the string.

> [!NOTE] **Hint 2** — A `default:` case in your `switch` (or a final `else`)
> is where you handle the "unknown operator" message.

> [!WARNING] **Hint 3** — Before you divide, check whether the second number is
> `0`. Guarding against failure *before* it happens is a core habit.

### You'll learn here

- `switch` statements.
- Primitive types (`int` vs `double`) and integer truncation.
- Defensive checks — catching errors before they happen.

### Stretch challenge

Loop until the user types `quit`, and validate that the input is actually a
number (use `scanner.hasNextDouble()` or `Double.parseDouble()` inside a
`try/catch`).

[↑ Back to Table of Contents](#table-of-contents)

---

## 4. Number Guessing Game

Now you'll play against the computer. It picks a secret number between 1 and
100, and you keep guessing until you get it right — with `higher` / `lower`
hints in between. The goal is a loop that keeps running based on *user input*,
not a fixed number of times.

### The task

- Pick a random number from 1 to 100.
- Loop: ask the user for a guess.
  - Too low → tell them `higher`.
  - Too high → tell them `lower`.
  - Correct → tell them `got it!` and how many guesses it took.
- End when they guess correctly.

### Getting started

- Generate a random number with `Random`:
  `new Random().nextInt(100) + 1` (the `+1` shifts the range to 1–100).
- Use a `while (true)` loop and `break` out when the guess is correct.
- Track guesses with a counter you increment each round.

### Hints

> [!TIP] **Hint 1** — `nextInt(100)` returns 0–99. Add `1` to make it 1–100.

> [!NOTE] **Hint 2** — For a loop that must run at least once until the user is
> correct, `while (true)` with a `break` on success is the simplest shape.

> [!WARNING] **Hint 3** — If you compare the guess but never read a new one
> inside the loop, you'll loop forever. Make sure each pass reads a fresh
> guess.

### You'll learn here

- Random number generation with `Random`.
- `while` loops and **sentinel control** — looping until a condition is met.
- `break` to exit a loop early.

### Stretch challenge

After the game ends, ask `Play again? (y/n)`. Track the best (fewest-guess)
score across rounds and show it.

[↑ Back to Table of Contents](#table-of-contents)

---

## 5. Palindrome Checker

A palindrome is a word or phrase that reads the same forwards and backwards —
like `racecar`, or the famous `A man, a plan, a canal: Panama`. Your job is to
tell the user whether their input is one. The challenge is that punctuation,
spaces, and capitalization all get in the way.

### The task

- Read a string from the user.
- Decide whether it's a palindrome, **ignoring case, spaces, and punctuation**.
- `"taco cat"` is a palindrome. So is `"A man, a plan, a canal: Panama"`.
  `"race a car"` is *not*.

### Getting started

Two main approaches — pick either:

- **Reversal:** build the reversed string, remove spaces/punctuation and
  lowercase both, then check if they're equal.
- **Two pointers:** walk one pointer from the front and one from the back,
  comparing characters inward, and stop as soon as a pair mismatches. If you
  make it to the middle, it's a palindrome.

> [!TIP] **Counterintuitive insight:** you don't need to compare the whole
> string. Two pointers meeting in the middle halve the work, and you can bail
> out early the moment a mismatch appears.

### Hints

> [!NOTE] **Hint 1** — `StringBuilder.reverse().toString()` gives you the
> string backwards. Compare it to the cleaned-up original.

> [!TIP] **Hint 2** — Clean the input before comparing: lowercase everything
> (`.toLowerCase()`), then strip anything that isn't a letter. `Character`
> has methods like `Character.isLetter(ch)` to help filter.

### You'll learn here

- `StringBuilder.reverse()` and `String.toLowerCase()`.
- Checking individual characters with `Character` methods.
- Comparing strings (`.equals()`, not `==`).

### Stretch challenge

Find the **longest palindromic substring** inside an input string — the
longest run of characters within it that forms a palindrome (e.g. inside
`"tracecar"` it's `"racecar"`).

[↑ Back to Table of Contents](#table-of-contents)

---

## 6. Word Frequency Counter

Read a paragraph of text and answer the question: *which words show up most?*
This introduces maps — a data structure for storing *key → value* pairs —
and the two-pass style of programming (first count, then sort).

### The task

- Read a paragraph of text.
- Count how often each unique word appears.
- Print each word with its count, **ordered by frequency (highest first)**.

### Getting started

- Split the input into words with `text.split("\\s+")` (or similar) to break on
  whitespace.
- Keep a `Map<String, Integer>` where the key is a word and the value is its
  count. The classic idiom:

  ```java
  map.put(word, map.getOrDefault(word, 0) + 1);
  ```

- After counting, sort the entries by their *value* (the frequency), not their
  key. You'll need to put them in a list and use a `Comparator`.

### Hints

> [!TIP] **Hint 1** — `Map.getOrDefault(word, 0)` returns the current count, or
> `0` if the word hasn't been seen yet. Add `1` and put it back — that single
> line handles both the "new word" and "seen word" cases.

> [!NOTE] **Hint 2** — `Map` entries live in a `Set<Map.Entry<K, V>>`.
> Convert to an `ArrayList` and sort by `.getValue()` with a `Comparator`.

> [!NOTE] **Hint 3** — `HashMap` gives no reliable order. If you want keys in
> sorted order, `TreeMap` sorts keys automatically — handy if you prefer
> alphabetical words over frequency-sorted results.

### You'll learn here

- `Map<String, Integer>` and the HashMap vs TreeMap trade-off.
- Splitting strings into tokens.
- A **two-pass** approach: collect data first, then process it.

### Stretch challenge

Strip out punctuation (commas, periods, quotes) and ignore case for a cleaner
count — so `Hello,` and `hello` count as the same word.

[↑ Back to Table of Contents](#table-of-contents)

---

## 7. Bank Account Class

Time for a shift: instead of one long `main`, you'll design a **class** — a
blueprint for a `BankAccount` object with its own data and behavior. This is
your first taste of Object-Oriented Programming, and it's where Java really
shines.

### The task

- Build a `BankAccount` class with private fields, a constructor, and
  `deposit` / `withdraw` methods plus a `getBalance()` getter.
- Enforce sanity rules:
  - **No negative deposits** — refuse them.
  - **No overdrawing** — you can't withdraw more than the balance.

### Getting started

- Fields: `private double balance;` (keep it private — see the note below).
- A constructor like `public BankAccount(double initialBalance)`.
- In `deposit`, check the amount is positive *before* adding. In `withdraw`,
  check the amount doesn't exceed the balance *before* subtracting.
- In `main`, create an account, deposit/withdraw, and print the balance.

> [!IMPORTANT] **Encapsulation** is the whole point of this exercise. Marking
> fields `private` means the outside world can only touch the balance through
> your methods — which is exactly how you enforce the "no overdrawing" rule.
> If the field were `public`, anyone could set it to a nonsense value directly.
> The methods are the *gatekeepers*.

### Hints

> [!TIP] **Hint 1** — If a deposit is invalid, do *nothing* rather than
> throwing or printing — methods should silently ignore bad input (or return
> a boolean so the caller knows it failed).

> [!NOTE] **Hint 2** — For "no overdrawing," the check is
> `if (amount <= balance)` before you subtract. Guard first, then mutate.

### You'll learn here

- Classes, constructors, fields, and methods.
- **Encapsulation** — private fields with public accessor methods.
- Class **invariants** — rules the object always maintains.

### Stretch challenge

Add a `SavingsAccount` subclass that refuses to withdraw below a `$100`
minimum balance, overriding the withdraw behavior.

[↑ Back to Table of Contents](#table-of-contents)

---

## 8. Student Record Manager

Now combine objects and collections: a menu-driven program that manages a list
of `Student` objects. You'll be doing full CRUD (Create, Read, Update, Delete)
over a collection — the workhorse pattern of most real programs.

### The task

- Define a `Student` class holding a name and a grade.
- A menu loop that lets the user:
  - **Add** a student,
  - **List** all students,
  - **Find** the student with the highest grade,
  - **Compute** the class average,
  - **Remove** a student.

### Getting started

- Store students in an `ArrayList<Student>`.
- The menu is a `while` loop that prints options, reads a choice, and acts on
  it — compare the choice with a `switch` or `if/else`.
- The "find highest / compute average" options mean looping over the list,
  either tracking a running best or summing grades as you go.
- "Remove" means searching by name and calling `list.remove(...)`.
- Have each option be a distinct method (e.g. `addStudent`, `listStudents`) to
  keep `main` readable.

### Hints

> [!TIP] **Hint 1** — "Highest grade" is the classic *linear search for the
> max*: start with the first student as the best, then walk the list replacing
> the best whenever you find a higher grade.

> [!NOTE] **Hint 2** — To remove by name, loop to find the matching index (or
> check `student.getName().equals(name)` while iterating), then remove it.
> Remember: use `.equals()`, never `==`, to compare strings.

### You'll learn here

- `ArrayList` and basic collection operations.
- Object modeling — pairing a class with a collection of that class.
- CRUD over a collection, and menu-driven loops.

### Stretch challenge

Persist the records: write them to a file so they survive between runs, then
load them back the next time you start (this foreshadows exercise 10).

[↑ Back to Table of Contents](#table-of-contents)

---

## 9. Inventory System with Interfaces

OOP's superpower: designing for *what something can do* rather than *what it
is*. You'll define an `Item` **interface** describing common behavior, implement
it with different concrete classes, and then write one piece of code that works
with *any* item.

### The task

- Define an `Item` interface with methods like `name()` and `price()`.
- Implement it in a couple of concrete classes (e.g. `Book`, `Electronics`).
- Manage a small shopping-cart total that accepts **any** `Item`.

### Getting started

- An interface declares *method signatures* only, no bodies:
  `double price();`
- Each implementing class provides its own `price()` body:
  `return 14.99;`
- Store items as `List<Item>` and sum them in a loop. Because the variable type
  is `Item`, the loop works no matter which concrete class is in the list —
  call `item.price()` and the right version runs.
- Try printing a cart total with a `Book` and an `Electronics` mixed in.

> [!TIP] **Programming to a type.** With the variable declared as `Item`, your
> cart code never needs to know whether it's holding a `Book` or
> `Electronics`. Add a whole new class that implements `Item` and the cart
> keeps working with zero changes. That decoupling is the payoff.

### Hints

> [!NOTE] **Hint 1** — A `Book implements Item` must provide `name()` and
> `price()`. If a class `implements` an interface but leaves a method out, it
> won't compile — that's the compiler enforcing the contract.

> [!TIP] **Hint 2** — `ArrayList<Item>` is the perfect type for the cart:
> polymorphic — it can hold any mix of classes that implement `Item`.

### You'll learn here

- Defining and implementing **interfaces**.
- **Polymorphism** — one method call, many behaviors.
- Programming to a type (interfaces) instead of a concrete class.

### Stretch challenge

Add a `DiscountedItem` **wrapper** that wraps any `Item` and applies a
percentage discount — building new behavior by composing, without changing the
classes you already have.

[↑ Back to Table of Contents](#table-of-contents)

---

## 10. File I/O: Gradebook

The final piece of the puzzle: making your data **outlive a single run**. You'll
read a `.csv` file of names and grades, do some real data processing, and write
a report back out. This is also where you'll learn to fail gracefully instead
of crashing.

### The task

- Read a `.csv` file with names and grades (one per line, e.g. `Ada,92`).
- Load each line into a `Student` object.
- Print a sorted report plus the class average.
- Save that report to a new file.

### Getting started

- Open the file with a `BufferedReader` (or `Files.newBufferedReader`) and read
  line by line with a `while` loop.
- Split each line on the comma to get name and grade.
- After loading everything, compute the average and sort by grade for the
  report.
- Write the report with `FileWriter` (or `Files.newBufferedWriter`).

> [!IMPORTANT] **Try-with-resources.** Use
> `try (BufferedReader r = new BufferedReader(...)) { ... }` so the file is
> closed automatically when you're done. Forgetting to close files leaks
> resources and can leave your data unwritten. The compact form is both safer
> and easier to read.

### Hints

> [!TIP] **Hint 1** — Reading line by line:
> `String line; while ((line = reader.readLine()) != null) { ... }` — the loop
> runs until there's no line left, which is when `readLine()` returns `null`.

> [!NOTE] **Hint 2** — `line.split(",")` returns a `String[]`; element `[0]` is
> the name and `[1]` the grade. Parse the grade with `Double.parseDouble(...)`.

> [!WARNING] **Hint 3** — Reading a file that doesn't exist throws a
> `FileNotFoundException`. Catch it and print a friendly `Couldn't find the
> file: ...` message instead of letting the program crash.

### You'll learn here

- `File` and reading line-by-line with a `BufferedReader`.
- Writing output with `FileWriter`.
- Try-with-resources and graceful failure (`FileNotFoundException`).

### Stretch challenge

Make the whole thing more robust: sort by grade *descending* (highest first),
skip any malformed line without crashing, and handle a file that can't be found
politely instead of dumping a stack trace.

[↑ Back to Table of Contents](#table-of-contents)

---

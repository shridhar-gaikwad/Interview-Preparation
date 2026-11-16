"""
=====================================================================================
                                CLASSES & OBJECTS
=====================================================================================
THEORY
-------------------------------------------------------------------------------------
- A CLASS is a blueprint/template that defines attributes (data) and methods (behaviour). It does not occupy memory for instance data
  until you create an object from it.
- An OBJECT (a.k.a. instance) is a concrete realization of a class, with its own copy of instance attributes,
  created by CALLING the class like a function: `obj = ClassName(args)`.
- Everything in Python is an object — even `int`, `str`, functions, and classes themselves (classes are instances of `type`, the "metaclass").

DEFINING A CLASS
    class Employee:
        company = "Acme"          # CLASS attribute — shared by ALL instances

        def __init__(self, name, salary):
            self.name = name       # INSTANCE attribute — unique per object
            self.salary = salary

        def give_raise(self, amount):   # INSTANCE method
            self.salary += amount

CLASS ATTRIBUTE vs INSTANCE ATTRIBUTE
    - Class attribute: defined directly in the class body, shared across all
      instances (one copy in memory) unless an instance shadows it.
    - Instance attribute: defined via `self.x = ...` (usually in __init__),
      unique to that object.
    - Reading `obj.company` falls back to the class attribute if no instance
      attribute of that name exists. ASSIGNING `obj.company = "X"` creates a
      new INSTANCE attribute that shadows the class attribute for that object
      only — it does not modify the shared class attribute.

TYPES OF METHODS
    Instance method   def method(self, ...)          operates on one object
    Class method       @classmethod
                        def method(cls, ...)           operates on the class itself,
                                                         often used as alternate
                                                         constructors (factory methods)
    Static method       @staticmethod
                        def method(...)                no self/cls — just a
                                                         utility function namespaced
                                                         inside the class

DUNDER (MAGIC) METHODS — control how your object behaves with built-in operators
    __init__   constructor logic (see 02_ConstructorsAndSelf.py)
    __str__    readable string for print()/str() — for end users
    __repr__   unambiguous string for debugging/repr() — ideally eval()-able
    __eq__     defines behaviour of ==
    __len__    defines behaviour of len(obj)

INSTROSPECTION HELPERS
    isinstance(obj, Class)     Is obj an instance of Class (or a subclass)?
    type(obj)                  Exact runtime class of obj
    obj.__dict__               Instance attributes as a dict
    Class.__dict__             Class attributes/methods as a dict

-------------------------------------------------------------------------------------
INTERVIEW Q&A (highly asked)
-------------------------------------------------------------------------------------
Q: What is the difference between a class and an object?
A: A class is the blueprint/definition (no memory for instance data); an
   object is a concrete instance created from that blueprint, with its own
   state in memory.

Q: What's the difference between a class attribute and an instance attribute?
A: Class attributes are defined in the class body and shared by all
   instances (one copy); instance attributes are set via `self.x = ...`,
   typically in `__init__`, and each object gets its own independent copy.

Q: What's the difference between @classmethod and @staticmethod?
A: @classmethod receives the class itself (`cls`) as the first argument and
   can access/modify class state or be used as an alternate constructor
   (e.g. `Employee.from_string(csv_line)`). @staticmethod receives neither
   `self` nor `cls` — it's just a plain function grouped inside the class
   for namespacing/organization, with no access to instance or class state.

Q: __str__ vs __repr__ — what's the difference?
A: __str__ is for a human-readable display (used by print()/str()).
   __repr__ is for an unambiguous, developer-facing representation (used by
   repr(), and as the fallback for print() if __str__ isn't defined) —
   convention says it should ideally look like valid code to recreate the
   object. If you define only one, define __repr__ first.

Q: How do you check if an object belongs to a particular class?
A: `isinstance(obj, ClassName)` — preferred over `type(obj) == ClassName`
   because isinstance() also returns True for subclasses, matching Python's
   duck-typing / polymorphism philosophy.

Q: Why is mutating a class attribute through an instance a common bug?
A: `obj.company = "NewCo"` does NOT change the shared class attribute — it
   creates a new instance attribute that shadows it for that one object.
   Other instances still see the original class attribute. To actually
   change the shared value, assign via the class itself: `Employee.company = "NewCo"`.
"""


class Employee:
    company = "Acme"                 # class attribute, shared by all instances
    employee_count = 0                # another class attribute, used as a counter

    def __init__(self, name, salary):
        self.name = name              # instance attribute
        self.salary = salary          # instance attribute
        Employee.employee_count += 1  # mutate via the CLASS, not an instance

    def give_raise(self, amount):
        self.salary += amount

    @classmethod
    def from_csv_line(cls, line):
        """Alternate constructor (factory method) — common classmethod use case."""
        name, salary = line.split(",")
        return cls(name.strip(), float(salary))

    @staticmethod
    def is_valid_salary(amount):
        """Pure utility function, doesn't need self or cls."""
        return amount > 0

    def __str__(self):
        return f"Employee({self.name}, salary={self.salary})"

    def __repr__(self):
        return f"Employee(name={self.name!r}, salary={self.salary!r})"


# creating objects
e1 = Employee("Alice", 50000)
e2 = Employee("Bob", 60000)

print(e1)                       # uses __str__ -> Employee(Alice, salary=50000)
print(repr(e2))                 # uses __repr__ -> Employee(name='Bob', salary=60000)

e1.give_raise(5000)
print(e1.salary)                # 55000

print("Company (class attribute, shared):", e1.company, e2.company)

# shadowing a class attribute via an instance — common pitfall
e1.company = "Beta"             # creates an INSTANCE attribute on e1 only
print("After shadowing: e1.company =", e1.company, "| e2.company =", e2.company)
print("Employee.company (class itself, untouched):", Employee.company)

print("Total employees created:", Employee.employee_count)

# classmethod as an alternate constructor
e3 = Employee.from_csv_line("Charlie, 45000")
print(e3)

# staticmethod usage
print("Is -100 a valid salary?", Employee.is_valid_salary(-100))

# introspection
print(isinstance(e1, Employee))      # True
print(type(e1) is Employee)          # True
print(e1.__dict__)                   # instance attributes only

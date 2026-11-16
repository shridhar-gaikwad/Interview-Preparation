"""
=====================================================================================
                            CONSTRUCTORS & self
=====================================================================================
THEORY
-------------------------------------------------------------------------------------
__init__ — THE INITIALIZER (commonly called "the constructor")
    - Called automatically right after a new object is created, to set up its
      initial state (instance attributes).
    - Technically, `__init__` is the INITIALIZER, not the true constructor —
      see __new__ below — but in day-to-day Python usage and interviews,
      "constructor" almost always refers to `__init__`.
    - Returns None implicitly; raising/returning a value from __init__ other
      than None is an error.

__new__ — THE TRUE CONSTRUCTOR (rarely overridden, but a favourite interview trap)
    - `__new__(cls, ...)` is a STATIC method (implicitly) that actually
      CREATES and returns the new (empty) object instance.
    - Object creation order: `SomeClass(args)` calls
          obj = SomeClass.__new__(SomeClass, args)   # allocate the instance
          SomeClass.__init__(obj, args)                # initialize its state
          return obj
    - You rarely need to override __new__ — common exceptions: implementing
      immutable types (subclassing tuple/str), singletons, or metaclass work.

self — WHY IT EXISTS AND WHAT IT ACTUALLY IS
    - `self` is simply a reference to the CURRENT INSTANCE the method is
      being called on. It's not a keyword — it's just a strong convention
      (you could name it anything, but never do).
    - Every instance method's first parameter receives the instance
      automatically: `obj.method(x)` is sugar for `Class.method(obj, x)`.
    - Through `self`, a method reads/writes that SPECIFIC object's attributes
      (`self.name = name`) and calls other instance methods on itself
      (`self.other_method()`).
    - Without `self`, instance methods would have no way to know WHICH
      object's data to operate on when the class has many instances.

CONSTRUCTOR OVERLOADING — Python has no built-in overloading
    - Python doesn't support multiple `__init__` signatures like Java/C++.
      Achieve similar flexibility via:
        1. Default argument values: `def __init__(self, name, salary=0)`
        2. *args / **kwargs for flexible signatures
        3. @classmethod alternate constructors (factory methods), e.g.
           `Employee.from_csv_line(...)` (see 01_ClassesAndObjects.py)

CALLING THE PARENT CONSTRUCTOR — super().__init__(...)
    - A subclass that defines its own __init__ does NOT automatically call
      the parent's __init__ — you must call it explicitly, almost always as
      the first line, so the parent's part of the state gets initialized too:
          class Manager(Employee):
              def __init__(self, name, salary, team_size):
                  super().__init__(name, salary)   # initialize Employee part
                  self.team_size = team_size         # then Manager's own part

-------------------------------------------------------------------------------------
INTERVIEW Q&A (highly asked)
-------------------------------------------------------------------------------------
Q: Is __init__ really the constructor in Python?
A: Strictly, no — __new__ is what creates (allocates) the object; __init__
   only initializes the already-created object's attributes. In practice,
   interviews and everyday usage call __init__ "the constructor" because
   that's the one you override 99% of the time.

Q: Why is self explicitly required as the first parameter in every instance method?
A: Python doesn't implicitly inject "this" like Java/C++ do. Since a class's
   method is really just a function living on the class, Python needs an
   explicit way to pass in WHICH instance called it — `self` IS that
   mechanism. `obj.method()` is automatically translated to `Class.method(obj)`.

Q: What happens if you forget `self` as a method's first parameter?
A: Calling it on an instance (`obj.method(x)`) raises a TypeError — Python
   still passes the instance positionally, so it collides with `x`
   ("method() takes 1 positional argument but 2 were given").

Q: Does Python support constructor overloading like Java?
A: No. Use default arguments, *args/**kwargs, or @classmethod factory
   methods instead of multiple __init__ definitions (a later __init__ in the
   same class simply overwrites the earlier one — there's no dispatch by
   signature).

Q: If a subclass defines its own __init__, does the parent's __init__ run automatically?
A: No — you must explicitly call `super().__init__(...)` yourself, otherwise
   the parent class's part of the object's state never gets initialized.

Q: Can you have logic in __init__ that prevents object creation (validation)?
A: Yes — raising an exception inside __init__ (e.g. `if salary < 0: raise
   ValueError(...)`) aborts construction; the object is never returned to
   the caller.
"""


class Employee:
    def __init__(self, name, salary):
        if salary < 0:
            raise ValueError("Salary cannot be negative")
        self.name = name     # `self` binds this attribute to THIS instance
        self.salary = salary

    def describe(self):
        # `self` is how this method knows WHOSE name/salary to read
        return f"{self.name} earns {self.salary}"


e1 = Employee("Alice", 50000)
e2 = Employee("Bob", 60000)
print(e1.describe())
print(e2.describe())

# proving self is "just" the instance reference — this is what Python does internally
print(Employee.describe(e1))          # identical to e1.describe()

try:
    Employee("Eve", -100)             # constructor validation prevents creation
except ValueError as e:
    print("Blocked bad construction:", e)


# __new__ vs __init__ demonstration
class Traced:
    def __new__(cls, *args, **kwargs):
        print("1) __new__ called -> allocates the instance")
        instance = super().__new__(cls)
        return instance

    def __init__(self, value):
        print("2) __init__ called -> initializes the instance")
        self.value = value


t = Traced(42)
print("Final object value:", t.value)


# constructor "overloading" via default args / classmethod, since Python has none natively
class Rectangle:
    def __init__(self, width, height=None):
        self.width = width
        self.height = height if height is not None else width   # height optional -> square

    @classmethod
    def square(cls, side):
        return cls(side)      # alternate constructor, reads clearly at call site

    def area(self):
        return self.width * self.height


r1 = Rectangle(4, 5)
r2 = Rectangle.square(4)
print("Rectangle area:", r1.area())
print("Square area:", r2.area())


# super().__init__() chaining into a subclass
class Manager(Employee):
    def __init__(self, name, salary, team_size):
        super().__init__(name, salary)   # initializes Employee's part of the state
        self.team_size = team_size

    def describe(self):
        return f"{super().describe()}, manages {self.team_size} people"


m = Manager("Charlie", 90000, team_size=5)
print(m.describe())

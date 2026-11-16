"""
=====================================================================================
                                    INHERITANCE
=====================================================================================
THEORY
-------------------------------------------------------------------------------------
Inheritance lets a class (SUBCLASS/CHILD/DERIVED) reuse and extend the
attributes and methods of another class (SUPERCLASS/PARENT/BASE), modelling
an "IS-A" relationship (a Manager IS-A Employee).

    class Parent:
        ...
    class Child(Parent):     # Child inherits everything Parent defines
        ...

WHY USE IT
    - Code reuse: shared behaviour lives once, in the parent.
    - Extensibility: a subclass can ADD new attributes/methods, or OVERRIDE
      a parent's method to specialize behaviour.

TYPES OF INHERITANCE
    Single      class B(A)
    Multi-level class C(B), class B(A)            (a chain: C -> B -> A)
    Multiple    class C(A, B)                      (inherits from 2+ parents
                                                     directly — Python supports
                                                     this; Java does not)
    Hierarchical  class B(A), class C(A)           (several children, one parent)

super() — calling the parent's version of a method
    - `super().method(...)` calls the PARENT's implementation of `method`,
      typically so you can extend it rather than fully replace it (classic
      use: `super().__init__(...)` — see 02_ConstructorsAndSelf.py).
    - super() uses the MRO (see below), so it also works correctly with
      multiple inheritance, not just simple single inheritance.

METHOD RESOLUTION ORDER (MRO) — critical for multiple inheritance
    - Python uses the C3 LINEARIZATION algorithm to decide, for
      `class D(B, C)`, in what order Python searches classes for an
      attribute/method: D, then B, then C, then their common base, etc.
    - Check it any time with `ClassName.__mro__` or `ClassName.mro()`.
    - This is how Python resolves the "DIAMOND PROBLEM" (D inherits from B
      and C, which both inherit from A) deterministically and consistently.

OVERRIDING vs OVERLOADING
    - Overriding: a subclass redefines a method with the SAME name/signature
      as its parent to provide specialized behaviour — fully supported, and
      the main way polymorphism is achieved (see 05_Polymorphism.py).
    - Overloading (same method name, different parameter lists): Python does
      NOT support this natively — only the last-defined version of a method
      name exists on a class. Use default args/*args or singledispatch instead.

isinstance() vs issubclass()
    isinstance(obj, Class)       Is obj an instance of Class or any of its subclasses?
    issubclass(ChildCls, Class)  Is ChildCls a subclass of Class (or Class itself)?

-------------------------------------------------------------------------------------
INTERVIEW Q&A (highly asked)
-------------------------------------------------------------------------------------
Q: What is the difference between single, multiple, and multi-level inheritance?
A: Single: one parent, one child. Multiple: a child inherits directly from
   two or more parent classes simultaneously (`class C(A, B)`), which Python
   supports but many languages (e.g. Java) don't allow directly. Multi-level:
   a chain of inheritance (A -> B -> C), where C gets A's features through B.

Q: What is the MRO and why does it matter?
A: Method Resolution Order — the specific, deterministic order Python
   searches base classes when resolving an attribute/method name, computed
   via the C3 linearization algorithm. It matters most with multiple
   inheritance / diamond-shaped hierarchies, where without a clear, consistent
   order, which parent's method "wins" would be ambiguous. Check it via
   `ClassName.__mro__`.

Q: What is the "diamond problem" and how does Python solve it?
A: It occurs when class D inherits from B and C, which both inherit from a
   common base A — so which path's version of a method should D use? Python
   resolves it deterministically via MRO/C3 linearization rather than
   leaving it ambiguous (unlike classic C++ multiple inheritance issues).

Q: Why do we call super().__init__() instead of calling Parent.__init__(self) directly?
A: super() respects the MRO, so it works correctly even in multiple-
   inheritance hierarchies (each class's super() call chains to the "next"
   class in the MRO, not hardcoded to one specific parent) — hardcoding
   `Parent.__init__(self)` breaks cooperative multiple inheritance.

Q: Does Python support method overloading like Java (same name, different params)?
A: No — defining a method twice with the same name in a class just makes the
   second definition replace the first. Use default argument values,
   *args/**kwargs, or `functools.singledispatch` to emulate it.

Q: isinstance() vs issubclass() — what's the difference?
A: isinstance(obj, Cls) checks whether obj is an instance of Cls (or any
   subclass of Cls). issubclass(ChildCls, Cls) checks the class relationship
   itself, with no object involved — is ChildCls Cls or derived from it?
"""


class Employee:
    def __init__(self, name, salary):
        self.name = name
        self.salary = salary

    def work(self):
        return f"{self.name} is working"

    def describe(self):
        return f"{self.name} earns {self.salary}"


# single inheritance
class Manager(Employee):
    def __init__(self, name, salary, team_size):
        super().__init__(name, salary)        # reuse parent's init
        self.team_size = team_size

    def work(self):                            # OVERRIDING parent's method
        return f"{self.name} is managing a team of {self.team_size}"


m = Manager("Alice", 90000, 5)
print(m.work())          # overridden behaviour
print(m.describe())      # inherited, unmodified behaviour
print(isinstance(m, Employee))   # True -> Manager IS-A Employee
print(issubclass(Manager, Employee))  # True


# multi-level inheritance: Employee -> Manager -> SeniorManager
class SeniorManager(Manager):
    def __init__(self, name, salary, team_size, region):
        super().__init__(name, salary, team_size)
        self.region = region

    def work(self):
        base = super().work()                  # extend, don't fully replace, parent behaviour
        return f"{base}, overseeing region {self.region}"


sm = SeniorManager("Bob", 120000, 10, "APAC")
print(sm.work())


# multiple inheritance + MRO + diamond problem
class Loggable:
    def log(self):
        return f"[LOG] action by {getattr(self, 'name', 'unknown')}"


class Auditable:
    def log(self):
        return f"[AUDIT] action by {getattr(self, 'name', 'unknown')}"


class ComplianceOfficer(Employee, Loggable, Auditable):
    pass


co = ComplianceOfficer("Charlie", 80000)
print(co.log())                       # resolved via MRO -> Loggable.log wins (left to right)
print(ComplianceOfficer.__mro__)      # inspect the resolution order directly

# hierarchical inheritance: multiple children sharing one parent
class Developer(Employee):
    def work(self):
        return f"{self.name} is writing code"


d = Developer("Diana", 70000)
print(d.work())

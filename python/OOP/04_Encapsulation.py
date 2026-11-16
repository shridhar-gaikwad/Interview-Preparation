"""
=====================================================================================
                                    ENCAPSULATION
=====================================================================================
THEORY
-------------------------------------------------------------------------------------
Encapsulation = bundling data (attributes) and the methods that operate on
that data together inside a class, while RESTRICTING direct outside access to
an object's internal state — forcing interaction through a controlled,
well-defined interface (methods/properties) instead of raw attribute access.
Goal: protect internal invariants (e.g. "balance can never go negative") from
being violated by code outside the class.

PYTHON HAS NO TRUE "private" — IT'S CONVENTION + NAME MANGLING
    public      self.name          No leading underscore. Freely accessible.
    protected   self._name          Single leading underscore. CONVENTION ONLY —
                                     signals "internal use / subclasses only,
                                     please don't touch from outside" but
                                     Python does not enforce this at all.
    private     self.__name         Double leading underscore (no trailing
                                     double underscore). Triggers NAME MANGLING:
                                     Python rewrites it to `_ClassName__name`
                                     internally, making accidental external/
                                     subclass access harder (but NOT
                                     impossible — see demo below).

NAME MANGLING IN DETAIL
    class Account:
        def __init__(self):
            self.__balance = 0       # stored internally as self._Account__balance
    acc = Account()
    acc.__balance          # AttributeError — this name doesn't exist as typed
    acc._Account__balance  # Works! Mangling is obfuscation, not real security.
    This exists mainly to avoid accidental NAME COLLISIONS in subclasses, not
    to provide airtight security.

@property — THE PYTHONIC WAY TO ENCAPSULATE (getters/setters without the Java boilerplate)
    class Account:
        def __init__(self, balance):
            self.__balance = balance

        @property
        def balance(self):              # GETTER — accessed like a plain attribute
            return self.__balance

        @balance.setter
        def balance(self, value):       # SETTER — validation runs on every assignment
            if value < 0:
                raise ValueError("Balance cannot be negative")
            self.__balance = value

    acc.balance           # calls the getter -> looks like attribute access
    acc.balance = 500      # calls the setter -> validated automatically
    This is preferred over explicit get_x()/set_x() methods (common in
    Java/C++) because callers use normal attribute syntax while the class
    retains full control, and you can start with a plain public attribute and
    add a @property later WITHOUT breaking the external API.

READ-ONLY PROPERTY
    Define only the getter (no @x.setter) -> assignment raises AttributeError.

-------------------------------------------------------------------------------------
INTERVIEW Q&A (highly asked)
-------------------------------------------------------------------------------------
Q: Does Python have true private attributes like Java's `private` keyword?
A: No. A single underscore (`_name`) is purely a convention meaning "treat as
   internal." A double underscore (`__name`) triggers name mangling
   (`_ClassName__name`), which makes accidental access harder but is still
   accessible if you know the mangled name — Python trusts the developer
   ("we're all consenting adults here") rather than enforcing access control.

Q: What problem does name mangling actually solve?
A: It prevents accidental attribute name collisions between a base class and
   a subclass that both use the same "private" attribute name — each class's
   `__attr` gets its own mangled name, so a subclass can't accidentally
   clobber a parent's internal state just by picking the same name.

Q: Why prefer @property over explicit getter/setter methods (get_x()/set_x())?
A: Callers interact with it using plain attribute syntax (`obj.x`, `obj.x = v`)
   while the class still runs validation/logic behind the scenes. It also
   lets you evolve a plain public attribute into a validated one later
   without changing the class's external interface/breaking caller code.

Q: How do you make an attribute read-only in Python?
A: Define a `@property` getter and do NOT define a corresponding `@x.setter`.
   Any attempt to assign (`obj.x = value`) then raises `AttributeError: can't
   set attribute`.

Q: Is encapsulation about hiding data for security, or something else?
A: Primarily about protecting invariants and managing complexity — controlling
   HOW internal state can change (validation, derived/computed values,
   logging on mutation) and decoupling a class's internal representation
   from its public interface, so the internals can change without breaking
   callers. It's not a security boundary against a malicious actor.
"""


class BankAccount:
    def __init__(self, owner, balance=0):
        self.owner = owner              # public
        self._account_type = "savings"  # protected by convention only
        self.__balance = balance        # private -> name-mangled to _BankAccount__balance

    @property
    def balance(self):
        """Getter — read access looks like a normal attribute."""
        return self.__balance

    @balance.setter
    def balance(self, value):
        """Setter — validation runs automatically on every assignment."""
        if value < 0:
            raise ValueError("Balance cannot be negative")
        self.__balance = value

    def deposit(self, amount):
        if amount <= 0:
            raise ValueError("Deposit amount must be positive")
        self.__balance += amount

    def withdraw(self, amount):
        if amount > self.__balance:
            raise ValueError("Insufficient funds")
        self.__balance -= amount


acc = BankAccount("Alice", 1000)
print(acc.balance)          # calls the getter, reads like plain attribute access

acc.deposit(500)
print(acc.balance)

try:
    acc.withdraw(10_000)    # internal invariant protected -> validation runs
except ValueError as e:
    print("Blocked:", e)

try:
    acc.balance = -50       # setter validation triggers even on direct assignment
except ValueError as e:
    print("Blocked:", e)

# accessing "protected" (single underscore) — technically allowed, convention discourages it
print("Protected, but accessible:", acc._account_type)

# "private" (double underscore) name mangling demo
try:
    print(acc.__balance)             # AttributeError -- this exact name doesn't exist
except AttributeError as e:
    print("Blocked by name mangling:", e)

print("But the mangled name still works:", acc._BankAccount__balance)   # obfuscation, not security


# read-only property example
class Circle:
    def __init__(self, radius):
        self.radius = radius

    @property
    def area(self):              # getter only -> read-only, derived/computed value
        return 3.14159 * self.radius ** 2


c = Circle(5)
print("Area (computed, read-only):", c.area)
try:
    c.area = 100
except AttributeError as e:
    print("Blocked:", e)

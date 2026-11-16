"""
=====================================================================================
                                    POLYMORPHISM
=====================================================================================
THEORY
-------------------------------------------------------------------------------------
Polymorphism ("many forms") = the same interface (method name / operator /
function call) behaves DIFFERENTLY depending on the actual object/type it is
used with. The caller doesn't need to know the concrete type to use it
correctly.

MAIN FORMS IN PYTHON

1. METHOD OVERRIDING (runtime polymorphism via inheritance)
    A subclass redefines a method inherited from its parent with the SAME
    name, and the correct version runs automatically based on the ACTUAL
    object's type, even if accessed through a parent-typed reference/collection.
        class Animal:
            def speak(self): return "..."
        class Dog(Animal):
            def speak(self): return "Woof"      # overrides Animal.speak
        class Cat(Animal):
            def speak(self): return "Meow"
        for a in [Dog(), Cat()]:
            print(a.speak())    # same call, different behaviour per object

2. DUCK TYPING (Python's default, most-used form — "if it walks like a duck...")
    Python doesn't check an object's declared TYPE before calling a method on
    it — it just tries to call the method, and if the object HAS that method,
    it works, regardless of inheritance relationship at all:
        class Duck:
            def sound(self): return "Quack"
        class Car:
            def sound(self): return "Vroom"
        for thing in [Duck(), Car()]:
            print(thing.sound())   # works for BOTH, unrelated classes entirely
    This is why Python favours "ask forgiveness" (try/except AttributeError)
    or simply calling the method, over checking `isinstance()` first.

3. OPERATOR OVERLOADING (dunder methods define what `+`, `==`, `len()`, etc. do)
        class Vector:
            def __add__(self, other):
                return Vector(self.x + other.x, self.y + other.y)
    The SAME operator `+` behaves differently for int, str, list, and your
    own custom class — that's polymorphism applied to operators.
    Common dunders: __add__ (+), __eq__ (==), __lt__ (<), __len__ (len()),
    __str__ (str()/print()), __getitem__ (obj[i]).

4. BUILT-IN FUNCTION POLYMORPHISM
    `len()` works on str, list, dict, tuple, and any custom object defining
    __len__ — one function name, many underlying behaviours per type.

ABSTRACT BASE CLASSES (abc module) — ENFORCING the "same interface" contract
    Polymorphism works best when every subclass is GUARANTEED to implement
    the expected method. `abc.ABC` + `@abstractmethod` enforces this: a
    subclass that doesn't override every abstract method cannot be
    instantiated at all (raises TypeError).

-------------------------------------------------------------------------------------
INTERVIEW Q&A (highly asked)
-------------------------------------------------------------------------------------
Q: What is polymorphism, in one sentence?
A: The ability to use a single, consistent interface (method/operator name)
   to trigger different, type-specific behaviour depending on the actual
   object it's invoked on.

Q: What is "duck typing" and how does it relate to polymorphism in Python?
A: Python doesn't check an object's class before calling a method — it just
   calls it, and if the object supports it, it works ("if it walks like a
   duck and quacks like a duck, it's a duck"). This gives Python
   polymorphism WITHOUT requiring a common base class/interface at all,
   unlike strictly-typed languages that usually require explicit interfaces.

Q: Method overriding vs method overloading — does Python support both?
A: Overriding (same name/signature, redefined in a subclass) — yes, fully
   supported, and is how runtime/inheritance-based polymorphism works.
   Overloading (same name, different parameter lists within ONE class) — no
   native support; the last-defined version of a method wins. Emulate it
   with default args, *args/**kwargs, or functools.singledispatch.

Q: How does operator overloading relate to polymorphism?
A: `+`, `==`, `len()` etc. are themselves polymorphic — their behaviour
   depends on the operand's type via dunder methods (__add__, __eq__,
   __len__). Defining these on your own class lets built-in operators treat
   your objects just like any built-in type, participating in the same
   polymorphic interface.

Q: Why use an Abstract Base Class (ABC) if Python supports duck typing anyway?
A: Duck typing is implicit and unenforced — a bug only surfaces at the
   moment the missing method is actually called. An ABC with
   @abstractmethod makes the contract EXPLICIT and FAILS FAST — Python
   refuses to even instantiate a subclass that forgot to implement a
   required method, catching the mistake immediately rather than at some
   unpredictable later call site.

Q: Give a real interview-friendly example combining overriding + duck typing.
A: A `process_payment(method)` function that calls `method.pay(amount)` on
   any object passed in (CreditCard, PayPal, Crypto) — it never checks
   `isinstance`; it just relies on each class implementing `.pay()`
   (duck typing), and each class's `.pay()` is its own override of a shared
   interface/abstract base (runtime polymorphism).
"""

from abc import ABC, abstractmethod


# 1. method overriding (runtime polymorphism via inheritance)
class Animal:
    def speak(self):
        return "Some generic animal sound"


class Dog(Animal):
    def speak(self):
        return "Woof"


class Cat(Animal):
    def speak(self):
        return "Meow"


for animal in [Dog(), Cat(), Animal()]:
    print(animal.speak())        # same call, behaviour differs per actual type


# 2. duck typing — no shared base class required at all
class Duck:
    def sound(self):
        return "Quack"


class Car:
    def sound(self):
        return "Vroom"


def make_noise(thing):
    # no isinstance() check — just trusts the object has `.sound()`
    print(thing.sound())


for item in [Duck(), Car()]:
    make_noise(item)


# 3. operator overloading via dunder methods
class Vector:
    def __init__(self, x, y):
        self.x = x
        self.y = y

    def __add__(self, other):
        return Vector(self.x + other.x, self.y + other.y)

    def __eq__(self, other):
        return self.x == other.x and self.y == other.y

    def __repr__(self):
        return f"Vector({self.x}, {self.y})"


v1 = Vector(1, 2)
v2 = Vector(3, 4)
print(v1 + v2)                # Vector(4, 6) -> same `+` operator, custom behaviour
print(v1 == Vector(1, 2))     # True -> same `==` operator, custom behaviour


# 4. built-in function polymorphism: len() behaves per-type, incl. custom classes
class Playlist:
    def __init__(self, songs):
        self.songs = songs

    def __len__(self):
        return len(self.songs)


print(len("hello"), len([1, 2, 3]), len(Playlist(["a", "b", "c"])))


# 5. enforcing the polymorphic contract with an Abstract Base Class
class PaymentMethod(ABC):
    @abstractmethod
    def pay(self, amount):
        ...


class CreditCard(PaymentMethod):
    def pay(self, amount):
        return f"Charged {amount} to credit card"


class PayPal(PaymentMethod):
    def pay(self, amount):
        return f"Charged {amount} via PayPal"


def process_payment(method: PaymentMethod, amount):
    # relies purely on the shared `.pay()` interface -- overriding + duck typing combined
    print(method.pay(amount))


for payment_method in [CreditCard(), PayPal()]:
    process_payment(payment_method, 100)

try:
    class IncompleteMethod(PaymentMethod):
        pass   # forgot to implement pay() -> instantiation blocked immediately

    IncompleteMethod()
except TypeError as e:
    print("Blocked by ABC contract:", e)

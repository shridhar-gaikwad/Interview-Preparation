"""
=====================================================================================
                                    TUPLES
=====================================================================================
THEORY
-------------------------------------------------------------------------------------
- A tuple is an ORDERED, IMMUTABLE collection, written with ( ) — the parentheses
  are optional; it's really the COMMA that makes a tuple.
- Ordered    : maintains insertion order, supports indexing/slicing like a list.
- Immutable  : cannot add/remove/reassign elements after creation.
- Allows duplicates and mixed types, same as a list.
- Hashable IF AND ONLY IF every element inside it is hashable -> can be used as
  a dict key or placed inside a set. A list can never be used this way.

WHY USE A TUPLE OVER A LIST?
    - Fixed configuration that must never change:
          database_config = ("localhost", 5432, "production")
    - As a dict key for composite keys:
          cache = {("GET", "/users"): "handler_fn"}
    - Returning multiple values from a function (Python packs them into a tuple).
    - Slightly faster to create/iterate and uses less memory than an
      equivalent list (no over-allocation needed since size is fixed).

SINGLE ELEMENT TUPLE GOTCHA
    x = (5)     # NOT a tuple, this is just int 5 with redundant parens
    x = (5,)    # tuple with one element -> the trailing comma is what matters

PACKING / UNPACKING
    point = 3, 4                 # packing -> (3, 4)
    x, y = point                 # unpacking
    first, *rest = [1, 2, 3, 4]  # starred unpacking -> first=1, rest=[2, 3, 4]
    a, b = b, a                  # classic swap using tuple packing/unpacking

NAMED TUPLES (collections.namedtuple)
    Gives tuple fields names while keeping immutability and tuple performance.
    Interview favourite as a lightweight alternative to a class with no methods.

-------------------------------------------------------------------------------------
INTERVIEW Q&A
-------------------------------------------------------------------------------------
Q: Why is a tuple generally faster than a list?
A: Fixed size known at creation time -> CPython can allocate exact memory once,
   no resizing logic, and tuples are cached/interned for small immutable
   literals, reducing allocation overhead.

Q: Can a tuple contain a mutable object, and is the tuple still "immutable"?
A: Yes — e.g. `t = (1, [2, 3])`. The tuple's own structure (what objects it
   references) can't change, but a mutable element inside it (the list) can
   still be mutated in place: `t[1].append(4)` works fine. This also means
   such a tuple is NOT hashable.

Q: When would you pick namedtuple over a regular class or a dict?
A: When you want readable field access (`point.x`) with the memory footprint
   and immutability of a tuple, and you don't need custom methods or mutability
   — e.g. representing a DB row or a coordinate pair.
"""

config = ("Dev", "Test", "Prod")
print(config[0])      # Dev
print(config)

try:
    config[1] = "Staging"       # TypeError: 'tuple' object does not support item assignment
except TypeError as e:
    print("Cannot modify tuple:", e)

# packing / unpacking
point = 3, 4
x, y = point
print(f"x={x}, y={y}")

first, *rest = (1, 2, 3, 4)
print("first:", first, "rest:", rest)

a, b = 10, 20
a, b = b, a
print("swapped:", a, b)

# tuple as dict key (composite key)
route_handlers = {
    ("GET", "/users"): "list_users",
    ("POST", "/users"): "create_user",
}
print(route_handlers[("GET", "/users")])

# namedtuple
from collections import namedtuple

Point = namedtuple("Point", ["x", "y"])
p = Point(3, 4)
print(p, p.x, p.y)

# tuple with a mutable element inside
t = (1, [2, 3])
t[1].append(4)
print("Tuple with mutated inner list:", t)

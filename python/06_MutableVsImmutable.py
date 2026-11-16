"""
=====================================================================================
                            MUTABLE vs IMMUTABLE
=====================================================================================
THEORY
-------------------------------------------------------------------------------------
    MUTABLE (content can change after creation, same object identity)
        list, dict, set, bytearray, and custom objects by default

    IMMUTABLE (content can never change after creation)
        int, float, complex, bool, str, tuple, frozenset, bytes

- "Changing" an immutable variable (`x = x + 1`) doesn't mutate the original
  object — it creates a brand NEW object and rebinds the name `x` to it.
- `is` checks OBJECT IDENTITY (same memory / same object), `==` checks VALUE
  EQUALITY. Two equal immutables are not necessarily the same object (though
  CPython caches/interns small ints -5..256 and short strings, so `is` can
  appear to work for them — never rely on that, it's an implementation detail).

PASS-BY-OBJECT-REFERENCE (how Python actually passes arguments)
    Python passes a reference to the object, for ALL types. The difference you
    observe is purely because of mutability:
        - Pass a mutable object -> function can mutate it in place, caller sees it.
        - Pass an immutable object -> function can only rebind its LOCAL name;
          the caller's variable is untouched.

THE CLASSIC MUTABLE-DEFAULT-ARGUMENT BUG
    def add_task(task, bucket=[]):   # DANGER: default list created ONCE at
        bucket.append(task)          # function definition time, and REUSED
        return bucket                # across every call that doesn't pass bucket!
    Fix: use `None` as the sentinel default and create the mutable object
    inside the function body each call.

COPYING
    shallow copy -> new outer container, same nested references
    deep copy     -> new outer container AND recursively new nested objects
    (see copy.copy() / copy.deepcopy(), demoed in 01_Lists.py)

MAKING A CUSTOM CLASS IMMUTABLE
    - `@dataclass(frozen=True)` raises on attempted attribute assignment.
    - Override `__setattr__`/`__delattr__` to raise, or use `__slots__` plus
      only setting values in `__init__`.
    - Tuples/namedtuples are the go-to immutable containers for simple data.

-------------------------------------------------------------------------------------
INTERVIEW Q&A
-------------------------------------------------------------------------------------
Q: Why is the mutable-default-argument pattern considered a classic bug?
A: The default value is evaluated exactly once, at function DEFINITION time,
   not on every call — so a mutable default is shared and accumulates state
   across unrelated calls, which is almost never the intended behaviour.

Q: Is a string mutable or immutable, and what does that imply for performance?
A: Immutable. Every "modification" (`s += "x"`, `.replace()`, `.upper()`)
   creates a new string object. Repeated concatenation in a loop is O(n^2) in
   the worst case — use `"".join(parts)` or an f-string instead for building
   strings incrementally.

Q: `a = [1, 2]; b = a; b.append(3)` — what is `a` now, and why?
A: `a` is now `[1, 2, 3]` too. `b = a` does not copy — both names reference
   the exact same list object, so mutating through `b` is visible via `a`.

Q: How do you make a Python class behave like an immutable value object?
A: `@dataclass(frozen=True)` is the idiomatic modern approach; it auto-
   generates `__init__`, `__eq__`, `__hash__`, and blocks attribute writes.
"""

# is vs ==
a = [1, 2, 3]
b = [1, 2, 3]
c = a
print("a == b:", a == b)   # True, same values
print("a is b:", a is b)   # False, different objects
print("a is c:", a is c)   # True, c is literally the same object as a

# mutating through an alias
c.append(4)
print("a after mutating c:", a)   # [1, 2, 3, 4] — same object!

# immutable rebind, not mutate
x = 10
y = x
x = x + 1
print("x:", x, "y:", y)   # x=11, y=10 — independent, because int is immutable

# pass-by-object-reference demo
def mutate_list(lst):
    lst.append("mutated")

def rebind_int(n):
    n = n + 100   # only rebinds the LOCAL name, caller unaffected

my_list = [1, 2]
my_int = 5
mutate_list(my_list)
rebind_int(my_int)
print("my_list after function call:", my_list)   # mutated, visible to caller
print("my_int after function call:", my_int)      # unchanged, still 5

# the mutable default argument trap
def add_task_buggy(task, bucket=[]):
    bucket.append(task)
    return bucket

print(add_task_buggy("task1"))   # ['task1']
print(add_task_buggy("task2"))   # ['task1', 'task2']  <- BUG: leaked from previous call!

def add_task_fixed(task, bucket=None):
    if bucket is None:
        bucket = []
    bucket.append(task)
    return bucket

print(add_task_fixed("task1"))   # ['task1']
print(add_task_fixed("task2"))   # ['task2']  <- correct, independent

# frozen dataclass for an immutable custom type
from dataclasses import dataclass

@dataclass(frozen=True)
class Point:
    x: int
    y: int

p = Point(1, 2)
try:
    p.x = 99
except Exception as e:
    print("Cannot mutate frozen dataclass:", type(e).__name__, e)

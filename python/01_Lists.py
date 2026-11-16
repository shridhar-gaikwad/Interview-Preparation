"""
=====================================================================================
                                    LISTS
=====================================================================================
THEORY
-------------------------------------------------------------------------------------
- A list is an ORDERED, MUTABLE collection of items, written with [ ].
- Ordered     : maintains insertion order.
- Mutable     : contents (add/remove/update) can change after creation.
- Heterogeneous: can hold mixed types -> [1, "two", 3.0, [4, 5]]
- Allows duplicate values.
- Supports indexing (0-based, negative indexing from the end) and slicing.
- Implemented internally as a dynamic array (over-allocates extra capacity so
  append() is amortized O(1); it does NOT reallocate on every single append).

SLICING SYNTAX
    sequence[start:stop:step]
    - start  : inclusive (default 0)
    - stop   : exclusive (default len(seq))
    - step   : default 1, can be negative to reverse

COMMON METHODS
    append(x)        Add single item to the end                  O(1) amortized
    extend(iterable) Add all items of an iterable to the end      O(k)
    insert(i, x)     Insert at index i                            O(n)
    remove(x)        Remove first matching value (ValueError if absent) O(n)
    pop(i=-1)        Remove & return item at index (default last) O(1) at end, O(n) elsewhere
    sort()           Sort in place, returns None                  O(n log n)
    sorted(list)     Returns a NEW sorted list, original untouched
    reverse()        Reverse in place
    index(x)         First index of value (ValueError if absent)
    count(x)         Number of occurrences

TIME COMPLEXITY CHEAT SHEET
    Index access      O(1)
    Append            O(1) amortized
    Insert/Delete (middle) O(n)
    Search (in)       O(n)
    len()             O(1)

COPY SEMANTICS
    - `new_list = old_list`      -> same object, both names point to same list (NOT a copy)
    - `new_list = old_list.copy()` or `old_list[:]` -> SHALLOW copy (new outer list,
       but nested objects inside are still shared references)
    - `copy.deepcopy(old_list)`  -> DEEP copy, nested objects are also duplicated

-------------------------------------------------------------------------------------
INTERVIEW Q&A
-------------------------------------------------------------------------------------
Q: List vs Tuple?
A: List is mutable, slightly slower, more memory (over-allocated); tuple is
   immutable, faster to create/iterate, hashable only if ALL its elements are
   hashable, and usable as a dict key / set member while a list cannot.

Q: Is `list` thread-safe?
A: Individual operations like append/pop are atomic under CPython's GIL for a
   single op, but compound operations (check-then-act) are NOT safe across
   threads without external locking.

Q: How do you remove duplicates while preserving order?
A: `list(dict.fromkeys(my_list))` — dict keeps insertion order (Py3.7+) and
   drops duplicate keys automatically. Using set() would lose order.

Q: Why does list.sort() return None instead of the sorted list?
A: Convention in Python: methods that mutate in place return None (e.g. sort(),
   reverse(), append()) to make it obvious no new object was created. Use the
   built-in `sorted()` when you need a new list back.
"""

task = ["extract data", "transform data", "load data"]
print(task)
print(task[0])          # first element -> extract data
print(task[-1])         # last element  -> load data

task.append("validate data")
task.extend(["clean data", "analyze data"])
print("After append/extend:", task)

task.sort()             # in-place, alphabetical
print("Sorted:", task)

# slicing
print("First two:", task[:2])
print("Reversed copy:", task[::-1])

# list comprehension
numbers_list = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
squares = [n ** 2 for n in numbers_list]
even_numbers = [n for n in numbers_list if n % 2 == 0]
print("Squares:", squares)
print("Evens:", even_numbers)

# shallow copy pitfall
import copy

original = [[1, 2], [3, 4]]
shallow = original.copy()
deep = copy.deepcopy(original)

shallow[0][0] = "CHANGED"   # mutates the nested list shared with `original`
print("Original after shallow mutation:", original)   # [['CHANGED', 2], [3, 4]]
print("Deep copy stays independent:", deep)            # [[1, 2], [3, 4]]

# dedupe preserving order
dupes = [3, 1, 2, 3, 2, 1, 4]
deduped = list(dict.fromkeys(dupes))
print("Deduped (order preserved):", deduped)

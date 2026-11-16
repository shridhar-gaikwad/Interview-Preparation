"""
=====================================================================================
                                    SETS
=====================================================================================
THEORY
-------------------------------------------------------------------------------------
- A set is an UNORDERED collection of UNIQUE, HASHABLE items, written with { }.
  (Note: `{}` alone creates an empty DICT, not a set — use `set()` for an
  empty set.)
- Mutable: you can add()/remove() items, but every item inside must itself be
  immutable/hashable (so you can have a set of tuples, but not a set of lists).
- No duplicates: adding an existing value is a silent no-op, not an error.
- Backed by a hash table like dict -> average O(1) membership test (`in`),
  which is the #1 reason to reach for a set.
- frozenset is the immutable counterpart of set (hashable itself, so it can be
  a dict key or live inside another set).

SET OPERATIONS
    a | b     Union                (all elements from both)
    a & b     Intersection         (common elements)
    a - b     Difference           (in a but not in b)
    a ^ b     Symmetric difference (in a or b, but not both)
    a.issubset(b) / a.issuperset(b) / a.isdisjoint(b)

SET COMPREHENSION
    {expr for item in iterable if condition}

-------------------------------------------------------------------------------------
INTERVIEW Q&A
-------------------------------------------------------------------------------------
Q: Why use a set instead of a list for membership testing?
A: `x in list` is O(n) (must scan), `x in set` is O(1) average, because set
   uses hashing instead of linear scan. Critical for de-duplication and
   "have I seen this before?" checks on large data.

Q: Why can't a set contain a list, but it can contain a tuple?
A: Set membership relies on hash(item). Lists are mutable and therefore
   unhashable (mutating them would change their hash and silently corrupt the
   hash table). Tuples are immutable (and hashable, provided their own
   elements are hashable), so they're allowed.

Q: How do you get a read-only/hashable version of a set?
A: `frozenset(my_set)`. Common use: using a frozenset as a dict key, or as a
   set element when you need "a set of sets".

Q: Fastest way to remove duplicates from a list when order doesn't matter?
A: `list(set(my_list))` — but remember this does NOT preserve order; use
   `dict.fromkeys()` (see 01_Lists.py) when order must be preserved.
"""

task_ids = {1, 2, 3, 4}
task_priority = {3, 4, 5, 6}

task_ids.add(4)       # duplicate, silently ignored
task_ids.add(7)
print("task_ids:", task_ids)

print("Union:", task_ids | task_priority)
print("Intersection:", task_ids & task_priority)
print("Difference (ids - priority):", task_ids - task_priority)
print("Symmetric difference:", task_ids ^ task_priority)

print("Is {3,4} subset of task_ids?", {3, 4}.issubset(task_ids))

numbers = [1, 2, 2, 3, 3, 4]
unique_squares = {n ** 2 for n in numbers}
print("Unique squares:", unique_squares)

# membership test performance intuition (conceptual, not a benchmark)
large_list = list(range(100_000))
large_set = set(large_list)
print(99_999 in large_set)   # O(1) average vs O(n) for `in large_list`

# frozenset as a dict key
frozen_a = frozenset({1, 2, 3})
cache_by_tagset = {frozen_a: "cached_result_for_tags_1_2_3"}
print(cache_by_tagset[frozenset({3, 2, 1})])   # order doesn't matter for lookup

try:
    bad_set = {[1, 2], 3}   # TypeError: unhashable type: 'list'
except TypeError as e:
    print("Cannot put a list in a set:", e)

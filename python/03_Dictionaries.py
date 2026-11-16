"""
=====================================================================================
                                    DICTIONARIES
=====================================================================================
THEORY
-------------------------------------------------------------------------------------
- A dict stores KEY-VALUE pairs, written with { key: value }.
- Keys must be UNIQUE and HASHABLE (so immutable types: str, int, float, tuple
  of hashables). Values can be ANY type, including another dict/list.
- Since Python 3.7, dicts preserve INSERTION ORDER as a language guarantee
  (CPython 3.6 had it as an implementation detail only).
- Backed by a hash table -> average O(1) lookup/insert/delete, worst case O(n)
  on heavy hash collisions (rare, and mitigated by randomized hashing).

COMMON METHODS
    d[key]              Access value, raises KeyError if missing
    d.get(key, default) Access value, returns `default` (None if omitted) if missing
    d[key] = value       Insert or update
    d.pop(key, default)  Remove & return value, or default if missing
    d.setdefault(k, v)   Return d[k] if present, else set d[k] = v and return v
    d.update(other)       Merge another dict/iterable of pairs into d
    d.keys() / .values() / .items()   Views (reflect live changes to the dict)

MERGING DICTS (Python 3.9+)
    merged = dict_a | dict_b          # new dict, dict_b's values win on conflict
    dict_a |= dict_b                  # in-place merge
    Pre-3.9 equivalent: {**dict_a, **dict_b}

DICT COMPREHENSION
    {key_expr: value_expr for item in iterable if condition}

USEFUL collections MODULE HELPERS
    defaultdict(factory)  Auto-creates a default value for missing keys
                           (e.g. defaultdict(list) to group items without
                           checking `if key not in d`)
    Counter(iterable)     Dict subclass specialised for counting occurrences,
                           has .most_common(n)
    OrderedDict           Rarely needed now that plain dict preserves order;
                           still useful for its move_to_end() method and the
                           fact that order participates in equality checks.

-------------------------------------------------------------------------------------
INTERVIEW Q&A
-------------------------------------------------------------------------------------
Q: How does a dict work internally?
A: It's a hash table. The key's hash() value determines a bucket/slot index.
   On collision, CPython uses open addressing (probing) to find the next free
   slot. Lookup re-hashes the key and probes the same sequence of slots.

Q: What makes an object hashable?
A: It must implement __hash__ (returning a value that never changes for the
   object's lifetime) and __eq__, and that hash must be consistent with
   equality (a == b implies hash(a) == hash(b)). Mutable builtins (list, dict,
   set) deliberately have __hash__ = None so they can't be used as keys —
   because mutating them would silently break the hash table invariant.

Q: get() vs []  — when to use which?
A: Use `[]` when the key is expected to exist and its absence is a real bug
   you want to surface loudly (KeyError). Use `.get()` (or `.setdefault()`)
   when a missing key is an expected, normal case.

Q: How do you count occurrences of items efficiently?
A: `collections.Counter(items)` — avoids manual `if key not in d: d[key] = 0`
   boilerplate and gives `.most_common(n)` for free.
"""

task_dependencies = {
    "extract data": [],
    "transform data": ["extract data"],
    "load data": ["transform data"],
}
print(task_dependencies)
print(task_dependencies["extract data"])

task_dependencies["validate data"] = ["load data"]
print(task_dependencies)
print(list(task_dependencies.keys()))

try:
    print(task_dependencies["transform data1"])   # KeyError
except KeyError:
    print("Key not found in the dictionary.")

print(task_dependencies.get("transform data1"))   # None, no exception

# dict comprehension
numbers_dict = [1, 2, 3, 4]
squares_dict = {n: n ** 2 for n in numbers_dict}
print(squares_dict)

# merging (3.9+)
defaults = {"retries": 3, "timeout": 30}
overrides = {"timeout": 60, "verbose": True}
merged = defaults | overrides
print("Merged config:", merged)

# setdefault to build a lookup without checking membership manually
grouped = {}
pairs = [("db", "extract"), ("db", "load"), ("api", "transform")]
for category, task in pairs:
    grouped.setdefault(category, []).append(task)
print("Grouped:", grouped)

# collections helpers
from collections import defaultdict, Counter

dd = defaultdict(list)
for category, task in pairs:
    dd[category].append(task)
print("defaultdict grouped:", dict(dd))

word_counts = Counter("extract transform load extract load load".split())
print("Counter:", word_counts)
print("Most common:", word_counts.most_common(1))

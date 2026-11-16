"""
=====================================================================================
                        COMPREHENSIONS & GENERATOR EXPRESSIONS
=====================================================================================
THEORY
-------------------------------------------------------------------------------------
Comprehensions are concise syntax to build a new collection from an iterable,
in a single readable expression instead of a manual for-loop + append().

    [expression for item in iterable if condition]      # list
    {key: value for item in iterable if condition}      # dict
    {expression for item in iterable if condition}       # set
    (expression for item in iterable if condition)       # generator expression

- List/dict/set comprehensions build the ENTIRE collection in memory immediately.
- A generator expression (parentheses) is LAZY — it yields one item at a time
  and never materializes the full sequence, so it uses O(1) memory regardless
  of input size. Pass it directly to sum(), any(), max(), or a for-loop.

NESTED COMPREHENSION (flattening a matrix)
    flat = [x for row in matrix for x in row]
    # equivalent to:
    # flat = []
    # for row in matrix:
    #     for x in row:
    #         flat.append(x)
    (Loop order in the comprehension matches the nested for-loop order: outer
    loop first, left to right.)

CONDITIONAL EXPRESSION INSIDE A COMPREHENSION (not a filter!)
    [x if x % 2 == 0 else -x for x in numbers]
    This is a ternary applied to EVERY item (transforms each item), whereas
    the trailing `if condition` filters WHICH items are included at all.
    You can combine both:
    [x if x % 2 == 0 else -x for x in numbers if x > 0]

WHEN TO AVOID A COMPREHENSION
    - If the logic needs multiple statements, side effects, or nested
      try/except, a plain for-loop is more readable — don't force it.
    - If you only need to iterate once and don't need the full collection in
      memory, prefer a generator expression over a list comprehension.

-------------------------------------------------------------------------------------
INTERVIEW Q&A
-------------------------------------------------------------------------------------
Q: Comprehension vs map()/filter() — which is more "Pythonic"?
A: Comprehensions are generally preferred in idiomatic Python because they
   read left-to-right like English and don't need a separate lambda. map/
   filter can be marginally faster for simple cases and compose well with
   existing named functions (no lambda needed), but comprehensions handle
   transform+filter in one expression more readably.

Q: Generator expression vs list comprehension — what's the real difference?
A: Memory and laziness. `sum(x*x for x in range(10**8))` never holds 10^8
   numbers in memory at once; `sum([x*x for x in range(10**8)])` builds the
   full list first. Generators also can't be re-iterated or indexed — they're
   single-use.

Q: Does variable scope leak out of a comprehension?
A: No (Python 3) — the loop variable inside a comprehension has its own scope
   and does not leak into the enclosing function/module, unlike a regular
   for-loop's variable.
"""

numbers_list = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]

squares = [n ** 2 for n in numbers_list]
even_numbers = [n for n in numbers_list if n % 2 == 0]
print("Squares:", squares)
print("Evens:", even_numbers)

# dict comprehension
squares_dict = {n: n ** 2 for n in numbers_list}
print("Squares dict:", squares_dict)

# set comprehension
unique_mod_three = {n % 3 for n in numbers_list}
print("Unique remainders mod 3:", unique_mod_three)

# nested comprehension: flatten a matrix
matrix = [[1, 2, 3], [4, 5, 6], [7, 8, 9]]
flat = [value for row in matrix for value in row]
print("Flattened matrix:", flat)

# conditional expression (ternary) vs filter inside comprehension
signed = [n if n % 2 == 0 else -n for n in numbers_list]
print("Even stays positive, odd becomes negative:", signed)

# generator expression - lazy, O(1) memory
sum_of_squares = sum(n ** 2 for n in numbers_list)
print("Sum of squares (via generator):", sum_of_squares)

gen = (n for n in numbers_list if n > 5)
print("Generator object:", gen)      # not materialized yet
print("Consumed once:", list(gen))   # now exhausted
print("Second attempt (empty, already consumed):", list(gen))

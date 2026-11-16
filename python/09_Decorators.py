"""
=====================================================================================
                                    DECORATORS
=====================================================================================
THEORY
-------------------------------------------------------------------------------------
A decorator is a function that wraps another function (or class) to add extra
behaviour BEFORE/AFTER it runs, WITHOUT modifying its original source code.
This works because in Python, functions are FIRST-CLASS OBJECTS — they can be
passed as arguments, returned from other functions, and assigned to variables.

THE MECHANICS, STEP BY STEP
    1. A decorator is just a function that takes a function and returns a
       (usually new) function:
           def my_decorator(func):
               def wrapper(*args, **kwargs):
                   # ... do something before ...
                   result = func(*args, **kwargs)
                   # ... do something after ...
                   return result
               return wrapper
    2. `@my_decorator` above a function def is pure SYNTACTIC SUGAR for:
           extract_data = my_decorator(extract_data)
    3. The `wrapper` uses *args/**kwargs so it can decorate ANY function
       signature, not just no-argument functions.

CLOSURES
    `wrapper` is a closure — it "remembers" `func` from its enclosing scope
    even after `my_decorator` has already returned. This is what lets the
    returned `wrapper` still call the original function later.

functools.wraps — ALWAYS USE THIS (interview favourite)
    Without it, the decorated function's `__name__`, `__doc__`, and other
    metadata get replaced by `wrapper`'s — breaking introspection, debuggers,
    and tools like Sphinx. `@functools.wraps(func)` on the wrapper copies
    that metadata over so the decorated function still looks like itself.

DECORATORS THAT TAKE THEIR OWN ARGUMENTS (decorator factories)
    A decorator factory is a function that returns a decorator:
        def repeat(times):            # outer: accepts the decorator's own args
            def decorator(func):      # middle: the actual decorator
                @functools.wraps(func)
                def wrapper(*args, **kwargs):   # inner: wraps the call
                    for _ in range(times):
                        result = func(*args, **kwargs)
                    return result
                return decorator
            return decorator
        @repeat(times=3)
        def greet(): ...
    Three levels of nesting: outer(factory args) -> decorator(func) -> wrapper(call args).

STACKING MULTIPLE DECORATORS
    @decorator_a
    @decorator_b
    def f(): ...
    # equivalent to: f = decorator_a(decorator_b(f))
    Applied BOTTOM-UP (closest to the function runs its wrapping innermost),
    but executes outer-to-inner when called.

CLASS-BASED DECORATORS
    Any object with a `__call__` method can act as a decorator.
    class CountCalls:
        def __init__(self, func):
            self.func = func
            self.calls = 0
        def __call__(self, *args, **kwargs):
            self.calls += 1
            return self.func(*args, **kwargs)

REAL-WORLD USES
    - Logging / timing every call (shown below)
    - `functools.lru_cache` — memoization decorator, built into the stdlib
    - Access control / auth checks before a handler runs
    - Framework routing: `@app.get("/tasks")` in FastAPI, `@app.route(...)`
      in Flask — the decorator registers the function against a URL rather
      than changing what the function does when called directly.

-------------------------------------------------------------------------------------
INTERVIEW Q&A
-------------------------------------------------------------------------------------
Q: What is a decorator, in one sentence?
A: A higher-order function that takes a function and returns a (typically
   wrapped) function, adding behaviour without changing the original's code.

Q: Why must the wrapper use *args, **kwargs?
A: So the decorator works for ANY decorated function's signature, regardless
   of how many positional/keyword arguments it takes.

Q: Why use functools.wraps?
A: It preserves the original function's __name__, __doc__, and __module__ on
   the wrapper, so introspection, logging, and documentation tools still see
   the decorated function's real identity instead of generic "wrapper" info.

Q: What's the difference between a decorator and a decorator FACTORY?
A: A decorator takes only the function being decorated: `decorator(func)`.
   A decorator factory takes its OWN arguments and returns a decorator:
   `factory(config)` returns `decorator`, which then takes `func`. This is
   why `@repeat(times=3)` needs parentheses but `@logger` doesn't.

Q: In `@a @b def f()`, which decorator's effect is "outermost" when f() is called?
A: `a`. Application order is `f = a(b(f))`, so calling f() enters a's wrapper
   first, which calls into b's wrapper, which calls the original f.

Q: How does FastAPI/Flask use decorators differently from a typical logging decorator?
A: Those decorators don't wrap the function's call behaviour at all — they
   register the function in a routing table as a side effect and then return
   the ORIGINAL function unchanged, so it can still be tested/called directly.
"""

from functools import wraps
import time


# A. functions as first-class objects
def greet():
    return "Hello"


def execute(func):
    print(func())


execute(greet)


# B. your first decorator
def my_decorator(func):
    def wrapper():
        print("Before function execution")
        func()
        print("After function execution")
    return wrapper


@my_decorator
def extract_data():
    print("Extracting data")


extract_data()


# C. decorator with *args/**kwargs — works for ANY function signature
def logger(func):
    @wraps(func)
    def wrapper(*args, **kwargs):
        print("Task started")
        result = func(*args, **kwargs)
        print("Task completed")
        return result
    return wrapper


@logger
def extract(table_name):
    print("Extracting:", table_name)
    return "Success"


result = extract("customers")
print(result)
print("Metadata preserved by functools.wraps ->", extract.__name__, "|", extract.__doc__)


# D. decorator factory — a decorator that itself takes arguments
def repeat(times):
    def decorator(func):
        @wraps(func)
        def wrapper(*args, **kwargs):
            last_result = None
            for _ in range(times):
                last_result = func(*args, **kwargs)
            return last_result
        return wrapper
    return decorator


@repeat(times=3)
def transform(table_name):
    print("Transforming:", table_name)


transform("orders")


# E. stacking multiple decorators
def upper_result(func):
    @wraps(func)
    def wrapper(*args, **kwargs):
        return func(*args, **kwargs).upper()
    return wrapper


@logger
@upper_result
def load(table_name):
    return f"loaded {table_name}"


print(load("orders"))


# F. a practical timing decorator
def timed(func):
    @wraps(func)
    def wrapper(*args, **kwargs):
        start = time.perf_counter()
        result = func(*args, **kwargs)
        elapsed = time.perf_counter() - start
        print(f"{func.__name__} took {elapsed:.6f}s")
        return result
    return wrapper


@timed
def slow_task():
    total = sum(range(1_000_000))
    return total


slow_task()


# G. class-based decorator using __call__
class CountCalls:
    def __init__(self, func):
        self.func = func
        self.calls = 0

    def __call__(self, *args, **kwargs):
        self.calls += 1
        print(f"{self.func.__name__} has been called {self.calls} time(s)")
        return self.func(*args, **kwargs)


@CountCalls
def validate(table_name):
    print("Validating:", table_name)


validate("orders")
validate("orders")


# H. built-in memoizing decorator from functools
from functools import lru_cache


@lru_cache(maxsize=None)
def fibonacci(n):
    if n < 2:
        return n
    return fibonacci(n - 1) + fibonacci(n - 2)


print("fib(30) =", fibonacci(30))
print("Cache info:", fibonacci.cache_info())


# I. how a web framework (FastAPI) uses a decorator — registers, doesn't wrap
#    (illustrative only — requires `pip install fastapi` to actually run)
"""
from fastapi import FastAPI

app = FastAPI()

@app.get("/tasks")
def get_tasks():
    return {"tasks": ["extract", "transform", "load"]}

# @app.get(...) registers `get_tasks` against the route table and returns the
# SAME function object unchanged — unlike `logger`/`timed` above, it does not
# wrap or alter what happens when you call get_tasks() directly in a test.
"""

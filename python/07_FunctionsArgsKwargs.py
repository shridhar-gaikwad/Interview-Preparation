"""
=====================================================================================
                    FUNCTION ARGUMENTS: *args and **kwargs
=====================================================================================
THEORY
-------------------------------------------------------------------------------------
PARAMETER KINDS (in the order Python requires them in a def signature)
    def f(pos_only, /, normal, *args, kw_only, **kwargs):
        ...
    - positional-only   : before `/`           (3.8+, rare, used by some builtins)
    - normal            : can be passed positionally OR by keyword
    - *args             : collects any EXTRA positional arguments into a TUPLE
    - keyword-only       : after `*` or `*args`, MUST be passed by name
    - **kwargs          : collects any EXTRA keyword arguments into a DICT

*args  — variable positional arguments, gathered into a tuple inside the function.
**kwargs — variable keyword arguments, gathered into a dict inside the function.

UNPACKING WHEN *CALLING* A FUNCTION (the mirror-image usage)
    numbers = [1, 2, 3]
    options = {"priority": 1, "status": "Pending"}
    create_task(*numbers, **options)
    # *numbers unpacks the list into separate positional args
    # **options unpacks the dict into separate keyword args

DEFAULT ARGUMENTS
    - Evaluated ONCE at function definition time (see 06_MutableVsImmutable.py
      for why a mutable default is a classic bug).
    - Parameters with defaults must come after parameters without defaults.

ORDER RULE WHEN CALLING: positional args, then *args-style unpacking, then
keyword args, then **kwargs-style unpacking — Python enforces this order.

-------------------------------------------------------------------------------------
INTERVIEW Q&A
-------------------------------------------------------------------------------------
Q: What is the difference between *args and **kwargs?
A: *args collects extra POSITIONAL arguments into a tuple; **kwargs collects
   extra KEYWORD arguments into a dict. Both let a function accept an
   arbitrary number of arguments without declaring each one by name.

Q: Can a function have both *args and **kwargs, and in what order?
A: Yes — `def f(a, b, *args, **kwargs)`. *args must come before **kwargs in
   the signature; Python collects remaining positionals into args and
   remaining keywords into kwargs.

Q: Why would you use **kwargs instead of just listing all keyword parameters?
A: When the full set of options isn't known ahead of time (e.g. forwarding
   arbitrary config to another function/API), or when you want to keep a
   function's signature stable while allowing optional extension.

Q: What's the risk of overusing *args/**kwargs?
A: It hides the function's real contract — IDEs/type checkers can't validate
   calls, and readers must dig into the body to know what's actually
   accepted. Prefer explicit named parameters when the set of arguments is
   known and fixed.
"""

class TaskRunner:
    def run_task(self, *args):
        """Runs an arbitrary number of tasks, collected into the `args` tuple."""
        print("Tasks received:", args)
        for task in args:
            print("Running:", task)

    def create_task(self, **kwargs):
        """Creates a task from arbitrary keyword options, collected into `kwargs` dict."""
        print("Options received:", kwargs)
        print("Name:", kwargs.get("name"))
        print("Priority:", kwargs.get("priority"))

    def create_and_run_task(self, *args, **kwargs):
        """Combines both — extra positionals go to args, extra keywords go to kwargs."""
        self.run_task(*args)
        self.create_task(**kwargs)


runner = TaskRunner()
runner.run_task("task1", "task2", "task3")
runner.create_task(name="Task1", description="This is task 1", priority=1, status="Pending")
runner.create_and_run_task(
    "task1", "task2",
    name="Task2", description="This is task 2", priority=2, status="In Progress",
)

# unpacking when CALLING a function
def describe_point(x, y, label="point"):
    print(f"{label}: ({x}, {y})")

coords = (3, 4)
describe_point(*coords, label="origin-offset")

options = {"x": 10, "y": 20, "label": "target"}
describe_point(**options)

# positional-only / keyword-only parameters (Python 3.8+)
def charge(amount, /, *, currency="USD"):
    # `amount` can ONLY be passed positionally (before `/`)
    # `currency` can ONLY be passed by keyword (after bare `*`)
    print(f"Charging {amount} {currency}")

charge(100, currency="EUR")
try:
    charge(amount=100, currency="EUR")   # TypeError: amount is positional-only
except TypeError as e:
    print("Error:", e)

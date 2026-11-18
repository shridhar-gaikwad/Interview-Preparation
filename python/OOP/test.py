import keyword
a, b, c = "Hello", 20, 30.30

x = True
y = False

# print("keyword : ", keyword.kwlist)

abc = ("Hello", 20, 30.30, True, False)
# print(abc)
# abc = ("Bye", 22)
# print(abc)
#
# if 1:
#     print("True")
# if -3:
#     print("False")



def print_pattern():
    s = ""
    for i in range(1, 10):
        for j in range(i):
            s += str(i) + " "
        s += "\n"
    # return s
    print(s)

print((1, 2, 3, 4, 5, 6, 7, 8, 9, 10)[0::2])
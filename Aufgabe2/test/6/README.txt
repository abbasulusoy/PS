main accepts an OPEN parameter and passes that on to another function (r1) which accepts an OPEN+ parameter.
This function return the whole parameter except the first element to the main function.
In the main function a third function (r2) is called to print the elements returned by r1.

INPUT:
HelloWorld John Alice

EXPECTED OUTPUT:
John Alice

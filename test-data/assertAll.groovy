// Example for the GlobalMethodsTest
a = 2

assertAll(
	{ assert a==1 },
	{ a = 1/0 },
	{ assert a==2 },
	{ assert a==3 }
)
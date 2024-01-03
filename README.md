Assumptions:
* Long id non negative not null not empty
* String firstName length between 1 and 30 not null not empty
* String lastName length between 1 and 30 not null not empty
* String city length between 1 and 30 not null not empty
* double cash not null not empty
* int numberOfAssets non negative not null not empty

I also throwed an error in case something was wrong and in case more fields were sent.

Notes:
I had some issues with testing the DB using a test suite, as the mocked database did not actually write anything.
I have tested the DB using postman and checked that the outputs match  
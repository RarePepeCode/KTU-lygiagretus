#ifndef LAB1_2_PAYMENT_H
#define LAB1_2_PAYMENT_H

#include <string>

using namespace std;

class Payment {

public:
    int number;
    string person;
    double amount;

    Payment(int number, string person, double amount);

    Payment();
};

#endif // !LAB1_#_PAYMENT_H
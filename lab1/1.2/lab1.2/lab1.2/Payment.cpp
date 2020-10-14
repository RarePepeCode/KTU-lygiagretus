#include "Payment.h"

Payment::Payment(int number, string person, double amount) {
    this->number = number;
    this->person = person;
    this->amount = amount;
}

Payment::Payment() {}
#include "Monitor.h"
#include <iostream>

Monitor::Monitor(int numberOfPayment, int arraySize) {
    payments = new Payment[arraySize];
    this -> numberOfPayment = numberOfPayment;
    this -> arraySize = arraySize;
    counterIndex = 0;
    readIndex = 0;
}

void Monitor::addPayment(Payment& payment) {
    int index = counterIndex % arraySize;
    while ((payments[index].person != "")) { }
#pragma omp critical
    {
        counterIndex++;
        payments[index] = payment;
    }
}
 
Payment Monitor::getPayment() {
    int index = readIndex % arraySize;
    Payment foundPayment;
    while ((payments[index].person == "")) { 
    }
#pragma omp critical
    {
        readIndex++;
        foundPayment = payments[index];
        payments[index] = Payment();
    }
    return foundPayment;
}


bool Monitor::finisheWork() {
    return isArrayEmpty() && numberOfPayment == counterIndex;
}

bool Monitor::isArrayEmpty() {
    for (int i = 0; i < arraySize; i++) {
        if (payments[i].person != "") {
            return false;
        }
    }
    return true;
}
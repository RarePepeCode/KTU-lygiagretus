#ifndef LAB1_2_MONITOR_H
#define LAB1_2_MONITOR_H

#include "Payment.h"
#include <omp.h>
#include <iostream>

using namespace std;

class Monitor {
public:
	Payment* payments;
    int counterIndex;
    int readIndex;
    int numberOfPayment;
    int arraySize;

    Monitor(int numberOfPayment, int arraySize);

    void addPayment(Payment& payment);

    Payment getPayment();

    bool finisheWork();

    bool isArrayEmpty();

};
#endif // !LAB1_2_MONITOR_H

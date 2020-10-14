#include <omp.h>
#include <iostream>
#include <string>
#include <iomanip>
#include <fstream>
#include "Payment.h"
#include "Monitor.h"
#include <vector>
#include <sstream> 
#include <utility>
#include <stdexcept>

using namespace std;

#define NUMBER_OF_PAYMENTS 26
#define NUMBER_OF_THREADS 10
#define DATA_FILE_NAME "IFF-7_5_KucinasI_dat_1.csv"
#define RESULT_FILE_NAME "IFF-7_5_KucinasI_rez_1.txt"

vector<Payment> readData();
void writeData(Monitor monitor);

int main()
{
    auto payments = readData();

    Monitor dataMonitor(NUMBER_OF_PAYMENTS, NUMBER_OF_PAYMENTS / 2);
    Monitor resultMonitor(NUMBER_OF_PAYMENTS, NUMBER_OF_PAYMENTS);

#pragma omp parallel num_threads(NUMBER_OF_THREADS)
    {
        string startJob = "Thread " + to_string(omp_get_thread_num()) + " started \n";
        cout << startJob;
        if (omp_get_thread_num() == 0) {
            for (Payment& payment : payments) {
                dataMonitor.addPayment(payment);
            }
        }
        else {
            while (!dataMonitor.finisheWork())
            {
                Payment payment = dataMonitor.getPayment();
                if (payment.number % 2 == 0) {
                    resultMonitor.addPayment(payment);
                }
            }
        }
    }
    writeData(resultMonitor);
    cout << "Hello World!\n";
}

vector<Payment> readData() {
    vector<Payment> payments;
    ifstream fin(DATA_FILE_NAME);
    string number;
    string name;
    string amount;
    while (fin.good()) {
        getline(fin, name, ',');
        getline(fin, number, ',');
        getline(fin, amount, '\n');
        int a = atoi(number.c_str());
        double b = atof(amount.c_str());
        Payment payment = Payment(a, name, b);
        payments.push_back(payment);
    }
    return payments;
}

void writeData(Monitor monitor) {
    remove(RESULT_FILE_NAME);
    ofstream fout;
    fout.open(RESULT_FILE_NAME, ios_base::app);
    for (int i = 0; i < NUMBER_OF_PAYMENTS; ++i) {
        if (monitor.payments[i].person != "")
        fout << to_string(monitor.payments[i].number) + " |" << setw(25) << monitor.payments[i].person + " |" << to_string(monitor.payments[i].amount) +  " |" << endl;
    }
}


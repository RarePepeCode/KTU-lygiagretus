package main

import (
	"encoding/csv"
	"fmt"
	"io"
	"log"
	"os"
	"strconv"
	"strings"
	"sync"
)

const NUMBER_OF_PAYMENTS int = 26
const NUMBER_OF_WORKERS int = 7
const DATA_FILE_NAME string = "IFF-7_5_KucinasI_dat_1.csv"
const RESULT_FILE_NAME string = "IFF-7_5_KucinasI_rez_1.txt"

func main() {
	fmt.Println("Hello")
	payments := readData()
	var workerChanels sync.WaitGroup
	var mainChanels sync.WaitGroup
	var results []Payment

	dataChanel := make(chan Payment)
	resultChanel := make(chan Payment)
	workerResultRoutine := make(chan Payment)
	workerDataRoutine := make(chan Payment)

	mainChanels.Add(2)
	go dataRoutine(dataChanel, workerDataRoutine, &mainChanels)
	go resultRoutine(workerResultRoutine, resultChanel, &mainChanels)

	workerChanels.Add(NUMBER_OF_WORKERS)
	for i := 0; i < NUMBER_OF_WORKERS; i++ {
		go workerRoutine(workerDataRoutine, workerResultRoutine, &workerChanels)
	}

	for _, payment := range payments {
		dataChanel <- payment
	}
	close(dataChanel)

	workerChanels.Wait()
	close(workerResultRoutine)

	for payment := range resultChanel {
		results = append(results, payment)
	}
	fmt.Println("Done")
	mainChanels.Wait()
	writeData(results)
}

func dataRoutine(dataChanel <-chan Payment, workerDataRoutine chan<- Payment, chanel *sync.WaitGroup) {
	var payments []Payment
	closed := false
	go func() {
		for {
			if len(payments) < 6 {
				payment, open := <-dataChanel
				if !open {
					closed = true
					break
				}
				payments = append(payments, payment)

			}
		}
	}()

	for {
		if len(payments) > 0 {
			fmt.Println(payments)
			workerDataRoutine <- payments[0]
			payments = payments[1:]
		} else if closed {
			break
		}
	}
	close(workerDataRoutine)
	chanel.Done()
}

func resultRoutine(workerResultRoutine chan Payment, resultChanel chan Payment, chanel *sync.WaitGroup) {
	var payments []Payment
	for payment := range workerResultRoutine {
		payments = append(payments, payment)
	}
	for _, payment := range payments {
		resultChanel <- payment
	}
	close(resultChanel)
	chanel.Done()
}

func workerRoutine(workerDataRoutine <-chan Payment, workerResultRoutine chan<- Payment, worker *sync.WaitGroup) {
	for payment := range workerDataRoutine {
		for i := 0; i < payment.number; i++ {
			payment.amount = payment.amount + float64(i)
		}
		if payment.number%2 == 0 {
			workerResultRoutine <- payment
		}
	}
	worker.Done()
}

func readData() []Payment {
	var payments []Payment
	csvfile, err := os.Open(DATA_FILE_NAME)
	if err != nil {
		log.Fatalln("Couldn't open the csv file", err)
	}
	r := csv.NewReader(csvfile)
	for {
		record, err := r.Read()
		if err == io.EOF {
			break
		}
		if err != nil {
			log.Fatal(err)
		}
		payments = append(payments, Payment{record[0], parseInt(record[1]), parseFloat(record[2])})
	}
	return payments
}

func writeData(payments []Payment) {
	f, err := os.Create(RESULT_FILE_NAME)
	if err != nil {
		log.Fatal(err)
	}
	for _, value := range payments {
		line := "|" + parseString(value.number) + "|" + value.person + strings.Repeat(" ", 20-len(value.person)) + "|" + parseFloatToString(value.amount) + "|" + "\n"
		_, err2 := f.WriteString(line)
		if err2 != nil {
			log.Fatal(err)
		}
	}
}

type Payment struct {
	person string
	number int
	amount float64
}

func parseString(parm int) string {
	result := strconv.Itoa(parm)
	return result
}

func parseFloatToString(parm float64) string {
	result := fmt.Sprintf("%f", parm)
	return result
}

func parseInt(parm string) int {
	result, _ := strconv.Atoi(parm)
	return result
}

func parseFloat(parm string) float64 {
	result, _ := strconv.ParseFloat(parm, 64)
	return result
}

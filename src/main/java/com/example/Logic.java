package com.example;

/**
 * Created with IntelliJ IDEA.
 * User: ryan
 * Date: 7/31/13
 * Time: 12:51 PM
 */
public class Logic {

    public boolean isPrime(long number) {
    	if (number <= 1) {
        	return false;
        }
    	if (number == 2) {
        	return true;
        }
        if (number % 2 == 0) {
        	return false;
        }
        for(int i = 3; i * i <= number; i += 2) {
            if(number % i == 0)
                return false;
        }
        return true;
    }

    public long nextPrimeFrom(long number) {
        int result = (int) number + 1;
    	if (result <= 1) {
    		result = 2;
        }
    	if (result % 2 == 0 && result != 2) {
    		result ++;
    	}
        while (!isPrime(result)){
        	result += 2;
        }
        return result;
    }
}

package com.example

import spock.lang.Shared
import spock.lang.Specification

/**
 * Created with IntelliJ IDEA.
 * User: ryan
 * Date: 8/5/13
 * Time: 11:06 AM
 */
class IntegrationTest extends Specification {
    @Shared def application = new Application()
    def jms = new JmsAccessor()
    def jdbc = new JdbcAccessor()
    def logic = new Logic()

	void readResultFromDatabaseAndTestWhetherItIsPrime(long number) {
		jms.receiveFromNumberOutput()
		long result = jdbc.getInsertedNumbers().last()
		assert logic.isPrime(result)
		if (logic.isPrime(number)) {
			assert result == number
		} else {
			assert result == logic.nextPrimeFrom(number)
		}
	}
	
	void jmsReceiveAndTestTheNumber(long number) {
		long result = jms.receiveFromNumberOutput()
		if (logic.isPrime(number)) {
			assert result == number + 1
		} else {
			assert result == logic.nextPrimeFrom(number)
		}
	}

    def setup() {
        application.start()
    }

    def cleanup() {
        application.stop()
    }

    def 'test overall system for first prime'() {
        when:
        jms.sendToNumberInput(2)

        then:
        jms.receiveFromNumberOutput() == 3
        jdbc.getInsertedNumbers().first() == 2
    }

    def 'test overall system for first non-prime'() {
        when:
        jms.sendToNumberInput(4)

        then:
        jms.receiveFromNumberOutput() == 5
        jdbc.getInsertedNumbers().first() == 5
    }

    def 'test database storage'() {
        when:
        jms.sendToNumberInput(2)

        then:
        jms.receiveFromNumberOutput() // wait for processing
        jdbc.insertedNumbers.first() == 2
    }
    
    def 'test integers less than 1'() {
            
        expect:           	
        jms.sendToNumberInput(number)
        jmsResult == jms.receiveFromNumberOutput()
        dbResult == jdbc.getInsertedNumbers().first()
        
        where:
        number | jmsResult | dbResult
        -1     | 2		   | 2
        0      | 2		   | 2
        1      | 2		   | 2    	
    }      
    
    def 'test number 2'() {
    	when:
        jms.sendToNumberInput(2)

        then:
        jms.receiveFromNumberOutput() == 3
        jdbc.getInsertedNumbers().first() == 2
    }
        
    // To test this case, I had to use above helper methods
    // which in turn rely on methods from Logic class.
    // I understand that this is the wrong thing to do,
    // as Logic class is part of the system under test.
    // I could not find any alternative within short time    
    
    def 'test a random integer'() {
    	when:
    	def num = Math.random() * 999 as int
    	
    	then:
		jms.sendToNumberInput(num)
    	jmsReceiveAndTestTheNumber(num) 
    	
		jms.sendToNumberInput(num)   	
    	readResultFromDatabaseAndTestWhetherItIsPrime(num)
    }
    
    def 'test a prime'() {
    	when:
        jms.sendToNumberInput(11)

        then:
        jms.receiveFromNumberOutput() == 12
        jdbc.getInsertedNumbers().first() == 11
    }
    
    def 'test a non-prime'() {
    	when:
        jms.sendToNumberInput(15)

        then:
        jms.receiveFromNumberOutput() == 17
        jdbc.getInsertedNumbers().first() == 17
    }
    
    def 'performance test'() {
    
        when:
        long start = System.currentTimeMillis()
        
        for ( i in 1..100 ) {   
        	def num = Math.random() * 999999 as int 		
        	jms.sendToNumberInput(num)
		}
		for ( i in 1..100 ) {    		
        	println "result:" + jms.receiveFromNumberOutput()
		}
 
        long elapsed = System.currentTimeMillis() - start
		println "Time Elapsed:" + elapsed
		
        then:
        elapsed <= 2000
    }
}

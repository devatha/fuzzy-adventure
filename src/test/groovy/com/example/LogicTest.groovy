package com.example

import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: ryan
 * Date: 8/5/13
 * Time: 12:45 PM
 */
class LogicTest extends Specification {
    def logic = new Logic()

    @Unroll
    def 'test for primeness of #number'() {
        expect:
        logic.isPrime(number)

        where:
        number << [2, 3, 5]
    }
    
    @Unroll
    def 'test for non-primeness of #number'() {
        expect:
        !logic.isPrime(number)

        where:
        number << [-23, 0, 1, 4, 9, 15]
    }

    @Unroll
    def 'test for next prime after #number'() {
        expect:
        logic.nextPrimeFrom(number) == next

        where:
        number | next
        -23    | 2
        0	   | 2
        1      | 2
        2      | 3
        3      | 5
        4      | 5
        5      | 7
        9	   | 11
        15	   | 17
    }
}

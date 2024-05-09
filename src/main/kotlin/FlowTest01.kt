import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.runBlocking
import java.math.BigInteger

object FlowTest01 {

    fun mainTest() {

        //test01()
        //test02()

    }

    private fun test03() {
        
    }

    private fun test02() = runBlocking {
        val startTime = System.currentTimeMillis()
        calculateFactorialOfV2(5).collect {
            printWithTimePassed(it, startTime)
        }
        println("Ready for work!")
    }

    private fun test01() {
        val startTime = System.currentTimeMillis()
        calculateFactorialOf(5).forEach {
            printWithTimePassed(it, startTime)
        }
    }

    private fun calculateFactorialOf(number: Int): Sequence<BigInteger> = sequence {
        var factorial: BigInteger = BigInteger.ONE
        println("factorial => $factorial")
        for (i in 1..number) {
            Thread.sleep(10)
            factorial = factorial.multiply(BigInteger.valueOf(i.toLong()))
            yield(factorial)
        }
    }


    private fun calculateFactorialOfV2(number: Int): Flow<BigInteger> = flow {
        var factorial: BigInteger = BigInteger.ONE
        println("factorial => $factorial")
        for (i in 1..number) {
            delay(10)
            factorial = factorial.multiply(BigInteger.valueOf(i.toLong()))
            emit(factorial)
        }
    }.flowOn(Dispatchers.Default)
}


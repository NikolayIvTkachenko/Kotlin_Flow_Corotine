import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.math.BigInteger
import kotlin.coroutines.EmptyCoroutineContext

object FlowTest01 {

    fun mainTest() {

        //test01()
        //test02()

        runBlocking {
            //test03()
        }
        //test04()
        test05()

    }
    private fun test05() {
        val flow01 = flow {
            delay(100)

            println("Emiiting first value")
            emit(1)

            delay(100)

            println("Emitting second value")
            emit(2)
        }

        val scope = CoroutineScope(EmptyCoroutineContext)
        flow01
            .onEach { println("Received $it with launchIn()") }
            .launchIn(scope)


        scope.launch {
            flow01.collect {
                println("Received $it in collect")
            }
        }

        Thread.sleep(2000)

    }

    private fun test04() {
        val flow01 = flow {
            delay(100)

            println("Emiiting first value")
            emit(1)

            delay(100)

            println("Emitting second value")
            emit(2)
        }

        val flow02 = flow {
            delay(100)

            println("Emiiting first value")
            emit(1)

        }

        runBlocking {
            flow01.collect {receivedValue ->
                println("Received value $receivedValue")

            }
        }

        runBlocking {
            val item = flow01.first()
            println("Received $item")
        }

        runBlocking {
            val item = flow01.first{ it > 1}
            println("Received $item")
        }

        runBlocking {
            val item = flow01.last()
            println("Received $item")
        }

        runBlocking {
            val item = flow02.single() //должно быть тольуо одно
            println("Received $item")
        }

        runBlocking {
            val item = flow01.toSet()
            println("Received set $item")
        }

        runBlocking {
            val item = flow01.toList()
            println("Received list $item")
        }

        runBlocking {
            val item = flow01.fold(6) { accumulator, emittedItem ->
                accumulator + emittedItem
            }
            println("Received value =  $item")
        }

    }

    private suspend fun test03() {
        val firstFlow = flowOf(1).collect{ emittedValue ->
            println("firstFlow: $emittedValue")
        }
        val secondFlow = flowOf(1, 2, 3)

        secondFlow .collect{ emittedValue ->
            println("secondFlow: $emittedValue")
        }
        val thirdFlow = flowOf<Any>(1, "two", 3)
        thirdFlow.collect{ emittedValue ->
            println("thirdFlow: $emittedValue")
        }

        listOf("A", "B", "C").asFlow().collect{ emittedValue ->
            println("asFlow: $emittedValue")
        }

        flow {
            delay(200)
            emit("item emitted after 2000ms")

            secondFlow.collect { emittedValue ->
                emit(emittedValue)
            }
            emitAll(thirdFlow)
        }.collect{ emittedValue ->
            println("flow{}: $emittedValue")
        }
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


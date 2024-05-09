import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import java.util.*

import kotlin.collections.ArrayList
import kotlin.coroutines.CoroutineContext
import kotlin.system.measureTimeMillis



val words = listOf("level", "pope", "needle", "Anna", "Pete", "noon", "stats")

data class UserInfo(val name: String, val lastName: String, val id: Int)
lateinit var user: UserInfo

var counter = 0






fun main(args: Array<String>) = runBlocking {

    //testApp02(args)
    //testApp03(args)
    //testApp05(args)
    //testApp06(args)
    //testApp07(args)
    //testApp08(args)
    //testApp09(args)
    //testApp10(args)
    //testApp11(args)
    //testApp13(args)
    //testApp15(args)
    testApp16(args)


}

//Защита от многопоточности
val contextV2 = newSingleThreadContext("counter")

//val actorCounter = actor<Void?>(contextV2) {
//    for(msg in channel) {
//        counter++
//    }
//}
suspend fun testApp17(args: Array<String>){

    val workerA = asyncIncrementV3(2000)
    val workerB = asyncIncrementV3(100)
    workerA.await()
    workerB.await()

    print("counter [$counter]")
}
fun asyncIncrementV3(by: Int) = CoroutineScope(Dispatchers.Default).async {
    for(i in 0 until by) {
        counter++
    }
}




suspend fun testApp16(args: Array<String>){

    val workerA = asyncIncrementV2(2000)
    val workerB = asyncIncrementV2(100)
    workerA.await()
    workerB.await()

    print("counter [$counter]")
}

fun asyncIncrementV2(by: Int) = CoroutineScope(Dispatchers.Default).async(contextV2) {
    for(i in 0 until by) {
        counter++
    }
}

suspend fun testApp15(args: Array<String>) {
    val time = measureTimeMillis {
        val channel = Channel<Int>(Channel.CONFLATED) //возвращает только последнее
       CoroutineScope(Dispatchers.Unconfined).launch {
            repeat(10) {
                channel.send(it)
                println("Sent $it")
            }
        }

        delay(500)
        println("Taking two")
        println(" Recieve: ${channel.receive()}")
        println(" Recieve: ${channel.receive()}")
        delay(500)
    }

    println("Took ${time} ms")
}

suspend fun testApp14(args: Array<String>) {
    val time = measureTimeMillis {
        val channel = Channel<Int>(4)
        val sender = CoroutineScope(Dispatchers.Unconfined).launch {
            repeat(10) {
                channel.send(it)
                println("Sent $it")
            }
        }

        delay(500)
        println("Taking two")
        println(" Recieve: ${channel.receive()}")
        println(" Recieve: ${channel.receive()}")
        delay(500)
    }

    println("Took ${time} ms")
}


suspend fun testApp13(args: Array<String>) {
    val time = measureTimeMillis {
        val channel = Channel<Int>(Channel.UNLIMITED)
        val sender = CoroutineScope(Dispatchers.Unconfined).launch {
            repeat(10) {
                channel.send(it)
                println("Sent $it")
            }
        }

        delay(500)
    }

    println("Took ${time} ms")
}

suspend fun testApp12(args: Array<String>) {

    val time = measureTimeMillis {
        val channel = Channel<Int>()
        val sender = CoroutineScope(Dispatchers.Unconfined).launch {
            repeat(10) {
                channel.send(it)
                println("Sent $it")
            }
        }

        channel.receive()
        channel.receive()
    }

    println("Took ${time} ms")
}


suspend fun testApp11(args: Array<String>) {

//    val s1 = iterator{
//        yield("First")
//        yield("Second")
//        yield("Third")
//    }
//    println(s1.asSequence().elementAt(1))
//    s1.forEach {
//        print("$it ")
//    }

    val fibonacci = sequence {
        yield(1L)
        var current = 1L
        var next = 1L
        while(true) {
            yield(next)
            val tmpNext = current + next
            current = next
            next = tmpNext
        }
    }

    val indexed = fibonacci.take(50).withIndex()
    for ((index, value) in indexed) {
        println("$index: $value")
    }

    val fibonacci2 = iterator {
        yield(1L)
        var current = 1L
        var next = 1L
        while(true) {
            yield(next)
            val tmpNext = current + next
            current = next
            next = tmpNext
        }
    }

    for (i in 0..91) {
        println("$i is ${fibonacci2.next()}")
    }

}

suspend fun testApp10(args: Array<String>) {

    val dispatcher = newSingleThreadContext("OneThread")
    val name =  withContext(dispatcher){
        "Nikolas Petrov"
    }
    println("User: $name")
}


suspend fun testApp09(args: Array<String>) {

    val dispatcher = newSingleThreadContext("OneThread")
    val name =  CoroutineScope(Dispatchers.Unconfined).async(dispatcher){
        "Nikolas Petrov"
    }.await()
    println("User: $name")
}

suspend fun testApp08(args: Array<String>) {
    val dispatcher = newSingleThreadContext("OneDispatcher")
    val handler = CoroutineExceptionHandler({_, throwable ->
        println("Error captured")
        println("Message: ${throwable.message}")
    })
    val context = dispatcher + handler

    val tmpCtx = context.minusKey(dispatcher.key)

    CoroutineScope(Dispatchers.Unconfined).launch(tmpCtx) {
        println("Running in ${Thread.currentThread().name}")
        TODO("Not implemeted")
    }.join()
}

suspend fun testApp07(args: Array<String>) {
    val dispatcher = newSingleThreadContext("OneDispatcher")
    val handler = CoroutineExceptionHandler({_, throwable ->
        println("Error captured")
        println("Message: ${throwable.message}")
    })

    CoroutineScope(Dispatchers.Unconfined).launch(dispatcher + handler) {
        println("Running in ${Thread.currentThread().name}")
        TODO("Not implemeted")
    }.join()
}

suspend fun testApp06(args: Array<String>) {
    val dispatcher = newSingleThreadContext("OneThread")
    val dispatcher02 = newFixedThreadPoolContext(4, "NewPool")

    CoroutineScope(Dispatchers.Unconfined).launch(dispatcher02) {
        println("Starting in ${Thread.currentThread().name} ")
        delay(500)
        println("Resuming in ${Thread.currentThread().name} ")
    }.join()
}

@OptIn(InternalCoroutinesApi::class)
suspend fun testApp05(args: Array<String>){
    val netDispatcher = newSingleThreadContext(name = "ServiceCall")

    val task = CoroutineScope(netDispatcher).async {
        doSomething()
    }
    task.join()
    if(task.isCancelled) {
        val exception = task.getCancellationException()
        println("Error with message: ${exception.message}")
    } else {
        println("Success")
    }


    println("Completed")
}

fun doSomething() {
    throw UnsupportedOperationException("Can't do")
}

suspend fun testApp04(args: Array<String>){
    val workerA = asyncIncrement(2000)
    val workerB = asyncIncrement(100)
    workerA.await()
    workerB.await()

    print("counter [$counter]")
}

suspend fun testApp03(args: Array<String>){
    asyncGetUserInfo(1)
    delay(2000)
    println("User ${user.id} is ${user.name}")
}

fun testApp02(args: Array<String>){
    println("Coroutine test app")
    println("Program arguments: ${args.joinToString()}")

    filterPalindromes(words).forEach {
        println(it)
    }
}

suspend fun testApp01(args: Array<String>){
    println("Coroutine test app")
    println("Program arguments: ${args.joinToString()}")

    println("${Thread.activeCount()} threads active at the start")

    val time = measureTimeMillis {
        createCoroutines(10_000)
    }
    println("${Thread.activeCount()} threads active at the end")
    println("Took $time ms")
}

fun asyncGetUserInfo(id: Int) = CoroutineScope(Dispatchers.Default).async {
    delay(1100)
    user = UserInfo(id = id, name = "Anna", lastName = "Tramp")

}

suspend fun createCoroutines(amount: Int) {
    val jobs = ArrayList<Job>()
    for (i in 1..amount) {
        jobs +=  CoroutineScope(Dispatchers.Default).launch {
            println("Started $i in ${Thread.currentThread().name} ")
            delay(1000)
            println("Finished $i in ${Thread.currentThread().name} ")
        }
    }

    jobs.forEach {
        it.join()
    }

}

fun isPalindrome(word: String) : Boolean {
    val lcWord = word.lowercase(Locale.getDefault())
    return lcWord == lcWord.reversed()
}

fun filterPalindromes(words: List<String>) : List<String> {
    return words.filter { isPalindrome(it)}
}


fun asyncIncrement(by: Int) = CoroutineScope(Dispatchers.Default).async {
    for(i in 0 until by) {
        counter++
    }
}

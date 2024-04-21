import kotlinx.coroutines.*
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
    testApp10(args)


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

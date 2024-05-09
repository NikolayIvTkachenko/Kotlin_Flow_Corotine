import java.math.BigInteger

fun printWithTimePassed(value: BigInteger, startTime: Long) {
    val currentTime = System.currentTimeMillis()
    val delta = currentTime - startTime
    println("$delta ms:  $value")
}
import com.twilio.Twilio
import com.twilio.rest.api.v2010.account.Call
import com.twilio.type.PhoneNumber
import com.twilio.twiml.VoiceResponse
import com.twilio.twiml.voice.Say
import java.net.URI
import kotlinx.coroutines.*
import spark.Spark.*


fun main(args: Array<String>) {
    port(3000)
    runBlocking{
        val cc = launch {
            phoneCall("<Phone number along with country code>")
        }
        cc.join()
    }
    post("/handle-choice") { req, res ->
        val userResponse = req.queryParams("Digits")
        val twimlResponse = handleChoice(userResponse)
        res.type("application/xml")
        twimlResponse.toXml()
    }
}

fun phoneCall(phone: String){
    val accountSid = "<account SID>"
    val authToken = "<Auth Token>"

    Twilio.init(accountSid, authToken)

    val from = PhoneNumber("<Phone number added on Twilio>")
    val to = PhoneNumber(phone)

    val call = Call.creator(
        to,
        from,
        URI.create("https://handler.twilio.com/twiml/EH4cc053265abd5fbe1aa9fa46f32c084b")
    ).create()

    println("Phone call sent with SID: ${call.sid}")
}

fun handleChoice(choice: String): VoiceResponse {
    val respOne = "Thank you for your response! We are sending you one whatsApp/Email message, please check that to raise your query further."
    val respTwo = "Have a great trip! We are sending you one whatsApp/Email message, please follow that to give your valuable feedback."
    return when (choice) {
        "1" -> {
            VoiceResponse.Builder().say(Say.Builder(respOne).build()).build()
        }
        "2" -> {
            VoiceResponse.Builder().say(Say.Builder(respTwo).build()).build()
        }
        else -> {
            VoiceResponse.Builder().say(Say.Builder("Invalid choice. Goodbye!").build()).build()
        }
    }
}

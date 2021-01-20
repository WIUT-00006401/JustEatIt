package com.example.justeatituser.Remote

import com.example.justeatituser.Model.FCMResponse
import com.example.justeatituser.Model.FCMSendData
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface IFCMService {
    @Headers(
        "Content-Type:application/json",
        "Authorization:key=AAAAKk5JUjQ:APA91bHW_krMvBnAaD6sPb51Vaco7UwM-grpSlXDnENYNzk4j0mOZGGWJcwZrnoZMJyvsyYbUOVTSiZxhFr4s7C8C6rq6kUXOgwqpObddmZwU0lmDf1QChCZXYcq7Y8vexnhMVm_PS8M"
    )
    @POST("fcm/send")
    fun sendNotification(@Body body: FCMSendData): Observable<FCMResponse>
}
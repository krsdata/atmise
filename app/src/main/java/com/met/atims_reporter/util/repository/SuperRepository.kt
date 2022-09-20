@file:Suppress("MemberVisibilityCanBePrivate")

package com.met.atims_reporter.util.repository

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.met.atims_reporter.util.model.Result
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

open class SuperRepository {

    inline fun <reified T> fromJson(json: String): T {
        return Gson().fromJson(json, object : TypeToken<T>() {}.type)
    }

    fun toJson(argument: Any) = Gson().toJson(argument)!!

    //util function
    fun getErrorMessage(throwable: Throwable): String {
        return if (throwable is HttpException) {
            val responseBody = throwable.response()!!.errorBody()
            try {
                val jsonObject = JSONObject(responseBody!!.string())
                jsonObject.getString("error")
            } catch (e: Exception) {
                e.message!!
            }
        } else (when (throwable) {
            is SocketTimeoutException -> "Timeout occurred"
            is IOException -> "network error"
            else -> throwable.message
        })!!
    }

    @Suppress("unused")
    private fun getErrorMessage(responseBody: ResponseBody): String {
        return try {
            val jsonObject = JSONObject(responseBody.string())
            jsonObject.getString("error")
        } catch (e: Exception) {
            "Something went wrong."
        }
    }

    inline fun <reified T> makeApiCall(
        observable: Observable<Response<ResponseBody>>,
        responseJsonKeyword: String = "",
        isResponseAString: Boolean = false,
        doNotLookForResponseBody: Boolean = false,
        lookForOnlySuccessCode: Boolean = false,
        callback: SuperRepositoryCallback<T>? = null,
        successMutableLiveData: MutableLiveData<Event<T>>? = null,
        errorMutableLiveData: MutableLiveData<Event<Result>>? = null,
        responseNotEnclosedInInnerResponseKeyword: Boolean = false,
        canReadOnlyOnce : Boolean = true
    ) {
        observable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                object : Observer<Response<ResponseBody>> {
                    override fun onComplete() {

                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onNext(t: Response<ResponseBody>) {
                        when (t.code()) {
                            200 -> {
                                if (lookForOnlySuccessCode) {
                                    successMutableLiveData?.postValue(
                                        Event(
                                            T::class.java.newInstance()
                                        )
                                    )

                                    callback?.success(
                                        T::class.java.newInstance()
                                    )

                                    return
                                }
                                try {
                                    val jsonObject = JSONObject(t.body()!!.string())
                                    val statusReply =
                                        fromJson<Result>(
                                            jsonObject.getJSONObject("status").toString()
                                        )
                                    if (statusReply.isResultOk()) {
                                        if (doNotLookForResponseBody) {
                                            successMutableLiveData?.postValue(
                                                Event(
                                                    T::class.java.newInstance()
                                                )
                                            )

                                            callback?.success(
                                                T::class.java.newInstance()
                                            )

                                            return
                                        }

                                        val resultToSendString =
                                            if (responseNotEnclosedInInnerResponseKeyword) {
                                                jsonObject.getString("result")
                                            } else {
                                                if (responseJsonKeyword == "") toJson(statusReply) else (
                                                        jsonObject.getJSONObject("result")
                                                                as JSONObject
                                                        )
                                                    .getString(responseJsonKeyword)
                                            }

                                        val resultToSend =
                                            if (isResponseAString)
                                                resultToSendString as T
                                            else fromJson(
                                                resultToSendString
                                            )

                                        successMutableLiveData?.postValue(
                                            Event(
                                                resultToSend,
                                                canReadOnlyOnce
                                            )
                                        )

                                        callback?.success(
                                            resultToSend
                                        )
                                    } else {
                                        errorMutableLiveData?.postValue(
                                            Event(
                                                statusReply
                                            )
                                        )

                                        callback?.error(statusReply)
                                    }
                                } catch (ex: Exception) {
                                    val errorReply = Result(
                                        -1,
                                        "Failed to parse data",
                                        ResultType.FAIL
                                    )
                                    errorMutableLiveData?.postValue(
                                        Event(
                                            errorReply
                                        )
                                    )

                                    callback?.error(errorReply)
                                }
                            }
                            else -> {
                                val errorReply = Result(
                                    -1,
                                    "Failed to parse data",
                                    ResultType.FAIL
                                )
                                errorMutableLiveData?.postValue(
                                    Event(
                                        errorReply
                                    )
                                )

                                callback?.error(errorReply)
                            }
                        }
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        val errorReply = Result(
                            -1,
                            getErrorMessage(e),
                            ResultType.FAIL
                        )
                        errorMutableLiveData?.postValue(
                            Event(
                                errorReply
                            )
                        )

                        callback?.error(errorReply)
                    }
                }
            )
    }

    lateinit var superRepositoryUnAuthorisedCallbackGlobal: SuperRepositoryCallback<Result>

    fun registerForUnAuthorisedGlobalCallback(callback: SuperRepositoryCallback<Result>) {
        this.superRepositoryUnAuthorisedCallbackGlobal = callback
    }
}
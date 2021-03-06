package com.trip.taxi.utils

object H5Page {
    var ENV = ""
//    var MESSAGE_DETAIL = String.format(HOST + "/message/detail", ENV)
//    var HELP = String.format(HOST + "/help", ENV)
//    var TRIP_CANCEL = String.format(HOST + "/cancel", ENV)


    private var host = "https://%staxi.com"
    private var m2host = "https://%sm2.com"
    fun initEnv(env: String) {
        ENV = env + "-"
    }

    public fun login(): String {
        return WebpageQueryHelper.append(String.format(host + "/taxi/login", ENV), null)
    }

    public fun register(): String {
        return WebpageQueryHelper.append(String.format(host + "/taxi/register", ENV), null)
    }

    public fun mine(): String {
        return WebpageQueryHelper.append(String.format(host + "/taxi/mine", ENV), null)
    }

    public fun withdrawHelp(): String {
        return WebpageQueryHelper.append(String.format(host + "/taxi/help", ENV), null)
    }

    public fun cannotWithDrawReason(): String {
        var map: MutableMap<String, String> = HashMap()
        map.put("key", "4")
        return WebpageQueryHelper.append(String.format(host + "/taxi/help/detail", ENV), map)
    }

    public fun messageDetail(id: String, messageType: String): String {
        var map: MutableMap<String, String> = HashMap()
        map.put("id", id)

        map.put("msgType", messageType)
        return WebpageQueryHelper.append(String.format(host + "/taxi/message/detail", ENV), map)
    }

    public fun protocol(): String {
        return WebpageQueryHelper.append(String.format(m2host + "/app/help/zh/taxiDriverProtocol.html", ENV), null)
    }

    fun lawAndPrivatePolicy(): String {
        return WebpageQueryHelper.append(String.format(host + "/taxi/help/protocol", ENV), null)
    }

    public fun orderCancel(orderId: String): String {
        var map: MutableMap<String, String> = HashMap()
        map.put("orderId", orderId)
        map.put("bizeType", "3")
        return WebpageQueryHelper.append(String.format(host + "/taxi/cancel", ENV), map)
    }

    public fun bindCard(): String {
        return WebpageQueryHelper.append(String.format(host + "/taxi/register/bankcard", ENV), null)
    }

    public fun certification(): String {
        return WebpageQueryHelper.append(String.format(host + "/taxi/register/bankcard/2", ENV), null)
    }
}
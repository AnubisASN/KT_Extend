package com.anubis.module_tcp

import android.os.Handler
import com.anubis.kt_extends.eLog
import com.anubis.kt_extends.eLogI
import kotlinx.coroutines.*
import java.net.Socket
import java.util.HashMap

/**
 * Author  ： AnubisASN   on 20-10-9 上午11:55.
 * E-mail  ： anubisasn@gmail.com ( anubisasn@qq.com )
 * Q Q： 773506352
 * 命名规则定义：
 * Module :  module_'ModuleName'
 * Library :  lib_'LibraryName'
 * Package :  'PackageName'_'Module'
 * Class :  'Mark'_'Function'_'Tier'
 * Layout :  'Module'_'Function'
 * Resource :  'Module'_'ResourceName'_'Mark'
 * Layout Id :  'LoayoutName'_'Widget'_'FunctionName'
 * Class Id :  'LoayoutName'_'Widget'+'FunctionName'
 * Router :  /'Module'/'Function'
 * 说明：
 */
private  class Test{
    val mTCP=eTCP.eInit(Handler())
    private val evTCPIP=""
    private val evTCPPort=""
    private var tcpJob:Job?=null
    init {
        if (mTCP.eIsExecuteConnect(
                        "$evTCPIP:$evTCPPort",
                        mTCP.eClientHashMap
                )
        )
            eLog("已有任务")
        else
            tcpConnect()
    }


    private fun tcpConnect() {
        tcpJob?.cancel()
        tcpJob = GlobalScope.launch {
            mTCP.eSocketConnect(
                    evTCPIP,
                    evTCPPort.toInt(),
                    true,
                    null,
                    1,
                    { tcpConnect() }
            ) { address: String, code: Int, msg: String, hashMap: HashMap<String, Socket?> ->
                eLogI("TCP接收-address:$address  code:$code  msg:$msg")
                when (code) {
                    //连接成功
                    mTCP.HANDLER_CONNECT_CODE -> {

                    }
                    //接收到消息
                    mTCP.HANDLER_MSG_CODE -> eLog("$msg-$address-$hashMap" )
                    else ->{} // heartbeatJob?.cancel()  关闭心跳
                }
            }
        }
    }
}

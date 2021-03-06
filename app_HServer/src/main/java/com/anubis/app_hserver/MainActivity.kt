package com.anubis.app_hserver

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.anubis.kt_extends.*
import com.anubis.module_dialog.eForegroundService
import com.anubis.module_dialog.eNotification
import com.anubis.module_extends.eRvAdapter
import com.anubis.module_httpserver.eHttpServer
import com.anubis.module_httpserver.eManage
import com.anubis.module_httpserver.eResolver
import com.anubis.module_httpserver.eResolverType
import com.anubis.module_httpserver.protocols.http.IHTTPSession
import com.anubis.module_httpserver.protocols.http.eHTTPD
import com.anubis.module_httpserver.protocols.http.response.Response
import com.anubis.module_picker.ePicker
import com.anubis.module_tts.eTTS
import com.anubis.module_ttse.eTTSE
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import java.io.File


@SuppressLint("SetTextI18n")
class MainActivity : AppCompatActivity() {
    private var mHttpServer: eHTTPD? = null
    private var mTTS: eTTS? = null
    private var mTTSE: eTTSE? = null
    private var mNotify: eNotification? = null
    private var httpHandler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                eResolverType.FILE_PARSE -> eLog("文件路径：${msg.obj}")
                eResolverType.FILE_PUSH -> eLog("文件推送：${msg.obj}")
                eResolverType.NULL_PARSE -> eLog("自定义返回：${msg.obj}")
                eResolverType.RAW_PARSE -> eLog("RAW解析：${msg.obj}")
                eResolverType.SESSION_PARSE -> {
                    eLog("常用解析")
                    (msg.obj as HashMap<*, *>).forEach {
                        eLog("Key:${it.key}--Value:${it.value}")
                    }
                }
                else -> {
                }
            }
        }
    }

    private var testjob: Job? = null

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        File("/sdcard/Web").apply { if (!this.exists()) this.mkdirs() }
        eAssets.eInit.eAssetsToFile(this, "Web/index.html", "/sdcard/Web/index.html")
        eAssets.eInit.eAssetsToFile(this, "Web/base64.js", "/sdcard/Web/base64.js")
        eAssets.eInit.eAssetsToFile(this, "Web/index.js", "/sdcard/Web/index.js")
        eAssets.eInit.eAssetsToFile(this, "Web/jquery.js", "/sdcard/Web/jquery.js")
        eAssets.eInit.eAssetsToFile(this, "Web/Vysor5.5.5.crx", "/sdcard/Web/Vysor5.5.5.crx")
        eAssets.eInit.eAssetsToFile(this, "Web/Vysor.zip", "/sdcard/Web/Vysor.zip")
        eResolver.eResultBlock = { uri: String, session: IHTTPSession ->
            Response.newFixedLengthResponse(when (uri) {
                "File" ->
                    eManage.eInit.eFileParse(session, "file")!!.eLog("111" + uri)
//        RAW发送    upRequestBody(RequestBody.create(MediaType.parse("application/json; charset=utf-8"),"{\"type\":\"Response\"}"))
                "Raw" -> eManage.eInit.eRawParse(session)!!.eLog("111" + uri)
                "Data" -> eManage.eInit.eSessionParse(session)!!.size.eLog("111" + uri).toString()
                else -> "a"
            })
        }
        mHttpServer = eHttpServer.eInit.eStart(eResolver::class.java, handler = httpHandler)



        mNotify = eNotification(this, MainActivity::class.java)

        eRvAdapter(this, rv, android.R.layout.activity_list_item, arrayListOf("1", "2", "3", "4", "5"), { view: View, s: String, i: Int ->
            view.findViewById<TextView>(android.R.id.text1).text = s
        }, layoutManagerBlock = {
            GridLayoutManager(it, 3)
        }, orientation = LinearLayoutManager.HORIZONTAL)
//        eVerification.eInit.eSetSwipeCaptcha(this, R.drawable.logo, findViewById(R.id.sample_card_scv), findViewById(R.id.sample_card_bar))
    }

    override fun onResume() {
        super.onResume()
        tvHint.eSpannableTextView(String.format(resources.getString(R.string.hint), "${eDevice.eInit.eGetHostIP()}:${mHttpServer?.myPort}"))
        testjob = GlobalScope.launch(start = CoroutineStart.LAZY) {
        }
    }

    var i: Int = 1

    @RequiresApi(Build.VERSION_CODES.O)
    @TargetApi(Build.VERSION_CODES.N)
    fun onClick(v: View) {
        when (v.id) {
            btTest1.id -> {
                val intent = Intent(this, eForegroundService::class.java)
                eForegroundService.initParam(R.drawable.logo, "系统提示", "正在后台运行", "")
                eForegroundService.initStart(this, intent, MainActivity::class.java, testjob, true, notiLayoutId = R.layout.layout_notification) {
                    when (it) {
                        R.id.notif_ivClose.toString() -> {
                            val intent = Intent(this@MainActivity, eForegroundService::class.java)
                            stopService(intent)
                        }
                    }
                }
            }
            btTest2.id -> {
                    ePicker.eInit.eColorStart(this) {
                        eLog("color:$it")
                    }
            }
            btEnglish.id -> {
                mNotify?.eSendNotify(R.drawable.dia_background, "非服务普通通知", "又没钱啦", null, false)
            }
            btSendMsg.id -> {
                eForegroundService.mNotification?.eSendNotify(R.drawable.dia_btbackground0, "开始打工啦", "又没钱啦", "", false, builderBlock = {
                    it.setContentTitle("开始打工啦啦啦啦")
                }).eLog("NotifyId")
            }
            btTest.id -> {
                val intent = Intent(this@MainActivity, eForegroundService::class.java)
                stopService(intent)
            }
            button8.id -> {
                mNotify?.eCleanNotify()
                eForegroundService.mNotification?.eCleanNotify()
            }
            imageView.id -> {
//                eShowTip(eVerification.eInit.eGetCaptchaCode(imageView))
            }
//            sample_swipe_code_view.id-> {
//                dragBar.isEnabled = false
//                swipeCaptchaView.eResetCaptcha()
//
//            }
        }
    }


}

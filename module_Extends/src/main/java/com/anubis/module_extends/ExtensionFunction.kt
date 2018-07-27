package com.anubis.kt_extends

import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.hardware.Camera
import android.os.Bundle
import android.preference.*
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.text.TextUtils.indexOf
import android.util.Base64
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.regex.Matcher
import java.util.regex.Pattern
import java.util.ArrayList
import kotlin.experimental.and


/**
 * 作者 ： AnubisASN   on 2017/7/3 16:23.
 * 邮箱 ： anubisasn@gmail.com ( anubisasn@qq.com )
 *  QQ :  773506352   ( 441666482 )
 */

/**
 * 扩展函数库
 */

/**
 * Toamst扩展函数-----------------------------------------------------------------------
 */

fun Context.eShowTip(str: Any, i: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, str.toString(), i).show()
}

/**
 * Log i e扩展函数------------------------------------------------------------------------
 */

fun Activity?.eLog(str: Any, TAG: String = "TAG") {
    Log.i(TAG, "${this?.localClassName ?: "eLog"}-：${str.toString()}\n ")
}

fun eLog(str: Any, TAG: String = "TAG") {
    Log.i(TAG, "eLog:${str.toString()}\n ")
}

fun Activity?.eLogE(str: Any, TAG: String = "TAG") {
    Log.e(TAG, "${this?.localClassName ?: "eLogE"}-：${str.toString()}\n ")
}

fun eLogE(str: Any, TAG: String = "TAG") {
    Log.e(TAG, "eLogE:${str.toString()}\n ")
}

/**
 * KeyDownExit事件监听------------------------------------------------------------------------------------
 */
private var clickTime: Long = 0

fun Activity.eSetKeyDownExit(keyCode: Int, activityList: ArrayList<Activity>? = null, systemExit: Boolean = true, hint: String = "再按一次退出", exitHint: String = "APP已退出", time: Long = 2000): Boolean {
    return if (keyCode == KeyEvent.KEYCODE_BACK) {
        if (System.currentTimeMillis() - com.anubis.kt_extends.clickTime > time) {
            eShowTip(hint)
            com.anubis.kt_extends.clickTime = System.currentTimeMillis()
            false
        } else {
            if (activityList != null) {
                for (activity in activityList) {
                    activity.finish()
                }
            }
            this.eShowTip(exitHint)
            this.finish()
            if (systemExit) {
                System.exit(0)
            }
            true
        }
    } else
        false
}

/**
 * SharedPreferences数据文件存储扩展----------------------------------------------------------------------
 */
//系统数据文件存储扩展
fun Context.eSetSystemSharedPreferences(key: Any, value: Any): Boolean {
    val sharedPreferences = getSharedPreferences(packageName, Context.MODE_PRIVATE)
    val key = key.toString()
    val editor = sharedPreferences.edit()
    when (value) {
        is String -> editor.putString(key, value)
        is Boolean -> editor.putBoolean(key, value)
        is Float -> editor.putFloat(key, value)
        is Int -> editor.putInt(key, value)
        is Long -> editor.putLong(key, value)
    }
    editor.putString(key, value.toString())
    return editor.commit()
}

//系统数据文件存储读取扩展
fun Context.eGetSystemSharedPreferences(key: String, value: Any = ""): Any {
    val sharedPreferences = getSharedPreferences(packageName, Context.MODE_PRIVATE)
    return when (value) {
        is String -> sharedPreferences.getString(key, value).toString()
        is Boolean -> sharedPreferences.getBoolean(key, value) as Boolean
        is Float -> sharedPreferences.getFloat(key, value)
        is Int -> sharedPreferences.getInt(key, value)
        is Long -> sharedPreferences.getLong(key, value)
        else -> sharedPreferences.getString(key, value as String?)
    }

}

//用户文件数据存储扩展
fun Context.eSetUserPutSharedPreferences(userID: String, key: String, value: Any): Boolean {
    val sharedPreferences = getSharedPreferences(userID, Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    when (value) {
        is String -> editor.putString(key, value)
        is Boolean -> editor.putBoolean(key, value)
        is Float -> editor.putFloat(key, value)
        is Int -> editor.putInt(key, value)
        is Long -> editor.putLong(key, value)
    }
    editor.putString(key, value.toString())
    return editor.commit()
}

//用户文件数据读取扩展
fun Context.eGetUserSharedPreferences(userID: Int, key: String, value: Any = ""): Any {
    val sharedPreferences = getSharedPreferences(userID.toString(), Context.MODE_PRIVATE)
    return when (value) {
        is String -> sharedPreferences.getString(key, value)
        is Boolean -> sharedPreferences.getBoolean(key, value)
        is Float -> sharedPreferences.getFloat(key, value)
        is Int -> sharedPreferences.getInt(key, value)
        is Long -> sharedPreferences.getLong(key, value)
        else -> sharedPreferences.getString(key, value as String?)
    }

}

//首选项数据文件存储扩展
fun Context.eSetDefaultSharedPreferences(key: String, value: Any = ""): Any {
    val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
    val edit = sharedPref.edit()
    return when (value) {
        is String -> sharedPref.getString(key, value)
        is Boolean -> sharedPref.getBoolean(key, value)
        is Float -> sharedPref.getFloat(key, value)
        is Int -> sharedPref.getInt(key, value)
        is Long -> sharedPref.getLong(key, value)
        else -> sharedPref.getString(key, value as String?)
    }
}

//首选项数据文件读取扩展
fun Context.eGetDefaultSharedPreferences(key: String, value: Any = ""): Any {
    var sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
    return when (value) {
        is String -> sharedPref.getString(key, value)
        is Boolean -> sharedPref.getBoolean(key, value)
        is Float -> sharedPref.getFloat(key, value)
        is Int -> sharedPref.getInt(key, value)
        is Long -> sharedPref.getLong(key, value)
        else -> sharedPref.getString(key, value as String?)
    }
}


/**
 * String Json解析扩展--------------------------------------------------------------------
 */
fun eGetJsonObject(json: String, resultKey: String) = JSONObject(json).getString(resultKey)

//Array Json解析扩展
fun eGetJsonArray(json: String, resultKey: String, i: Int) = JSONObject(json).getJSONArray(resultKey).getJSONObject(i).toString()

fun eGetJsonArray(json: String, resultKey: String) = JSONObject(json).getJSONArray(resultKey)


/**
 * Intent Set传递扩展-------------------------------------------------------------------------
 */
//Intent Get传递扩展
fun Intent.eGetMessage(Sign: String): String = getStringExtra(Sign)


/**
 * Intent Set 捆绑传递扩展-------------------------------------------------------------------------
 */
fun Bundle.eSetMessage(Sign: String, Message: Any = "") = when (Message) {
    is String -> putString(Sign, Message)
    is Int -> putInt(Sign, Message)
    is Float -> putFloat(Sign, Message)
    is Boolean -> putBoolean(Sign, Message)
    is Char -> putChar(Sign, Message)
    is Byte -> putByte(Sign, Message)
    is ArrayList<*> -> putStringArrayList(Sign, Message as ArrayList<String>)
    is IntArray -> putIntArray(Sign, Message)
    else -> {
    }
}

/**
 * 高斯模糊轮子扩展---------------------------------------------------------------------------------
 */
//fun Context.eGetGaussianBlur(resource: Int, radius: Int = 5, scale: Int = 3): Bitmap {
//    return EasyBlur.with(this)
//            .bitmap(BitmapFactory.decodeResource(getResources(), resource)) //要模糊的图片
//            .radius(radius)//模糊半径
//            .scale(scale)//指定模糊前缩小的倍数
//            .policy(EasyBlur.BlurPolicy.FAST_BLUR)//使用fastBlur
//            .blur()
//}


/**
 * 本地应用遍历
 */
fun Context.eGetLocalApps(appName: String): String? {
    var PackageName: String? = null
    val intent = Intent(Intent.ACTION_MAIN, null)
    intent.addCategory(Intent.CATEGORY_LAUNCHER)
    val apps = getPackageManager().queryIntentActivities(intent, 0)
    //for循环遍历ResolveInfo对象获取包名和类名
    for (i in 0..apps.size - 1) {
        val info = apps.get(i)
        val packageName = info.activityInfo.packageName
        val className = info.activityInfo.name
        val appsName = info.activityInfo.loadLabel(getPackageManager())
        if (appName == appsName) {
            PackageName = packageName
            return PackageName
        }
        PackageName = null
    }
    return PackageName
}

/**
 * 数值段获取----------------------------------------------------------
 */
fun eGetNumberPeriod(str: String, start: Any, end: Any): String {
    val Str = str.trim()
    val Start: Int = when (start) {
        is Int -> start
        is String -> str.indexOf(start) + 1
        else -> {
            0
        }
    }
    val End: Int = when (end) {
        is Int -> end
        is String -> if (end == "MAX") str.length else str.indexOf(end)
        else -> {
            0
        }
    }
    return if (Str.isEmpty()) "" else Str.substring(Start, End).trim()
}

/**
 * 状态判断-----------------------------------------------------------------------------------
 */
fun Activity.eAppRestart() {
    val LaunchIntent = packageManager.getLaunchIntentForPackage(application.packageName)
    LaunchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    startActivity(LaunchIntent)
}


//AppLication运行判断
fun Context.isAppRunningForeground(packageName: String): Boolean {

    val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val runningAppProcessInfoList = activityManager.runningAppProcesses ?: return false
    for (processInfo in runningAppProcessInfoList) {
        if (processInfo.processName == packageName && processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
            return true
        }
    }
    return false
}

//Activity运行判断
fun Context.eActivityWhetherWorked(className: String): Boolean {
    val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val list = am.getRunningTasks(1)
    if (list != null && list.size > 0) {
        val cpn = list[0].topActivity
        if (className == cpn.className) {
            return true
        }
    }
    return false
}


//服务运行判断 com.anubis.iva.Service.IVAService
fun Context.eServiceWhetherWorked(className: String): Boolean {
    val myManager = this.applicationContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val runningService = myManager.getRunningServices(30) as ArrayList<ActivityManager.RunningServiceInfo>
    for (i in runningService.indices) {
        if (runningService[i].service.className.toString() == className) {
            return true
        }
    }
    return false
}

/**
 * 正则表达式------------------------------------------------------------------------------------
 */
fun eGetNumber(str: String): Int {
    val regEx = "[^0-9]"
    val p = Pattern.compile(regEx)
    val m = p.matcher(str)
    return (m.replaceAll("").trim { it <= ' ' }).toInt()
}

//IP格式判断
fun eIsIP(ip: String): Boolean {
    val regIP: String = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$"
    return Pattern.compile(regIP).matcher(ip).matches()
}

//电话格式判断
fun eIsPhoneNumber(number: Any): Boolean {
    val regExp: String = "^((13[0-9])|(15[^[4,5],\\D])|(16[^6,\\D])|(18[0,1,5-9])|(17[6,7,8]))\\d{8}$"
    val p: Pattern = Pattern.compile(regExp)
    val m: Matcher = p?.matcher(number.toString())
    return m.find()
}

//邮编格式判断
fun eIsZipNO(zipString: String): Boolean {
    val str = "^[1-9][0-9]{5}$"
    return Pattern.compile(str).matcher(zipString).matches()
}

//邮箱格式判断
fun eIsEmail(email: String?): Boolean {
    if (null == email || "" == email) return false
    val p = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*")//复杂匹配
    val m = p.matcher(email)
    return m.matches()
}

/**
 * 首选Summary项动态改变--------------------------------------------------
 */
//sSummaryDynamicSetting(findPreference("root_screen") as PreferenceScreen)
//Summary修改
fun Context.eSummaryModify(group: PreferenceGroup) {
    val sp = PreferenceManager.getDefaultSharedPreferences(this)
    for (i in 0..group.preferenceCount - 1) {
        val p = group.getPreference(i)
        if (p is PreferenceGroup) {
            eSummaryModify(p)
        } else {
            if ((p is ListPreference) or (p is EditTextPreference)) {
                val value = sp.all[p.key]
                p.summary = if (value == null) "" else "$value"
                p.onPreferenceChangeListener = this as Preference.OnPreferenceChangeListener?
            }
        }
    }
}


/**
 * 图片文件工具类------------------------------------------------------------
 */

//Bitmap释放
fun eGcBitmap(bitmap: Bitmap?) {
    var bitmap = bitmap
    if (bitmap != null && !bitmap.isRecycled) {
        bitmap.recycle() // 回收图片所占的内存
        bitmap = null
        System.gc() // 提醒系统及时回收
    }
}

//Bitmap转Base64工具
fun eBitmapToBase64(bitmap: Bitmap?): String? {
    var baos: ByteArrayOutputStream? = null
    var reslut: String? = null
    try {
        if (bitmap != null) {
            baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos)
            baos.flush()
            baos.close()
            // 转换为字节数组
            val byteArray = baos.toByteArray()
            // 转换为字符串
            reslut = Base64.encodeToString(byteArray, Base64.DEFAULT)
        } else {
            return null
        }
    } catch (e: IOException) {
        e.printStackTrace()
    } finally {
        try {
            baos?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }
    return reslut
}

//Base64转Bitmap工具
fun eBase64ToBitmap(base64String: String): Bitmap {
    val decode = Base64.decode(base64String, Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(decode, 0, decode.size)
}

 fun eGetPhoneBitmap(mImageNV21: ByteArray, width: Int, height: Int, mCameraID: Int = 1): Bitmap? {
     var mBitmap:Bitmap?=null
     if (mImageNV21!=null){


         val image = YuvImage(mImageNV21, ImageFormat.NV21, width, height, null)
         val stream = ByteArrayOutputStream()
         image.compressToJpeg(Rect(0, 0, width, height), 80, stream)
         val bmp = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size())
          mBitmap = rotateMyBitmap(bmp)
         stream.close()
         eGcBitmap(bmp)
     }
    return mBitmap
}

fun rotateMyBitmap(bmp: Bitmap, mCameraID: Int = 1): Bitmap {
    //*****旋转一下
    var matrix = Matrix()
    if (mCameraID == Camera.CameraInfo.CAMERA_FACING_FRONT) {
        matrix.postRotate(270f)
    } else {
        matrix.postRotate(90f)
    }
    var nbmp2 = Bitmap.createBitmap(bmp, 0, 0, bmp.width, bmp.height, matrix, true)
    return nbmp2

}

//十六进制字符串转字节
fun eGetHexStringToBytes(hexString: String?): ByteArray? {
    var hexString = hexString
    if (hexString == null || hexString == "") {
        return null
    }
    hexString = hexString.toUpperCase()
    val length = hexString.length / 2
    val hexChars = hexString.toCharArray()
    val d = ByteArray(length)
    for (i in 0 until length) {
        val pos = i * 2
        d[i] = (eGetCharToByte(hexChars[pos]).toInt() shl 4 or eGetCharToByte(hexChars[pos + 1]).toInt()).toByte()
    }
    return d
}

//字符转字节
fun eGetCharToByte(c: Char) = indexOf("0123456789ABCDEF", c).toByte()

fun bytesToHexString(src: ByteArray?, lenth: Int): String? {
    val stringBuilder = StringBuilder("")
    if (src == null || src.size <= 0) {
        return null
    }
    for (i in 0 until lenth) {
        val v = src[i] and 0xFF.toByte()
        val hv = Integer.toHexString(v.toInt())
        if (hv.length < 2) {
            stringBuilder.append(0)
        }
        stringBuilder.append(hv)
    }
    return stringBuilder.toString()
}


/**
 * 运行权限扩展---------------------------------------------------------------
 */

fun Activity.eSetPermissions(permissionsArray: Array<out String>, requestCode: Int = 1) {
    val permissionsList = ArrayList<String>()

    for (permission in permissionsArray) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission)
        }

    }
    if (!permissionsList.isEmpty()) {//未授予的权限为空，表示都授予了
        ActivityCompat.requestPermissions(this, permissionsList.toArray(arrayOfNulls<String>(permissionsList.size)), 1)
    }

}


//显示授权设置
fun Activity.eSetOnRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray, isPermissionsOKHint: String = "", isPermissionsNoHint: String = "") {
    when (requestCode) {
        1 -> for (i in permissions.indices) {
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                if (isPermissionsOKHint.isEmpty()) {
                    eShowTip(isPermissionsOKHint)
                }
            } else {
                if (isPermissionsNoHint.isEmpty()) {
                    eShowTip(isPermissionsNoHint)
                }
            }
        }
    }
}

/**
 * 服务意图扩展------------------------------------------------
 */

//开机自启
var isSetAutoBoot = true

fun eSetAutoBoot(myApplication: Application, context: Context, intent: Intent, className: Any? = null) {
    if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
        eLog("开机启动", "SAK")
        val pm = myApplication.packageManager
        if (className != null && isSetAutoBoot) {
            isSetAutoBoot = false
            val packName = intent.resolveActivityInfo(pm, 0).toString()
            val cls = when (className) {
                is String -> Class.forName(className)
                is Class<*> -> className
                else -> Class.forName(packName)
            }
            val startServiceIntent = Intent(context, cls::class.java)
            startServiceIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(startServiceIntent)
        }
    }
}


///**
// * 反射机制动态加载扩展----------------------------------------------------------
// */
////获取 加载类
//fun eGetClass(packageName: String) = Class.forName(packageName)
////获取 实例化类
//fun eGetClassInstance(cls:Class<Any>)=cls.newInstance()
////调用方法
//fun  eInvokeMethod(cls:Class<Any>,methodName:String)={
//    cls.getDeclaredMethod(methodName,String::class.java)
//}


package com.example.august

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter
import com.nhaarman.listviewanimations.appearance.simple.ScaleInAnimationAdapter
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter
import com.nhaarman.listviewanimations.appearance.simple.SwingLeftInAnimationAdapter
import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView
import com.nhaarman.listviewanimations.itemmanipulation.dragdrop.TouchViewDraggableManager
import android.view.ViewGroup
import com.example.august.adapter.MailAdapter
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.undo.SimpleSwipeUndoAdapter
import android.view.View
import android.text.InputType
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.*
import android.widget.AdapterView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import br.tiagohm.markdownview.MarkdownView
import br.tiagohm.markdownview.css.styles.Github
import co.nedim.maildroidx.MaildroidXType


import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.input.getInputField
import com.afollestad.materialdialogs.input.input
import com.afollestad.materialdialogs.list.isItemChecked
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.example.august.lib.MaildroidX
import com.example.august.lib.TinyDB
import com.gordonwong.materialsheetfab.MaterialSheetFab
import com.nhaarman.listviewanimations.itemmanipulation.dragdrop.OnItemMovedListener

import com.nhaarman.listviewanimations.ArrayAdapter
import com.stfalcon.smsverifycatcher.OnSmsCatchListener
import com.stfalcon.smsverifycatcher.SmsVerifyCatcher
import com.suke.widget.SwitchButton
import com.tuenti.smsradar.Sms
import com.tuenti.smsradar.SmsListener
import com.tuenti.smsradar.SmsRadar
import kotlinx.android.synthetic.main.custom_view.*
import okhttp3.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.Logger
import javax.mail.PasswordAuthentication
import javax.mail.Session
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    //lateinit var smsVerifyCatcher:SmsVerifyCatcher
    lateinit var mySmsObserver: MySmsObserver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mContext=this@MainActivity

        /**************第一次启动判断*************/
        var pref=getSharedPreferences("firstBoot", Context.MODE_PRIVATE)
        var firstBoot=pref.getBoolean("booted",true)
        if (firstBoot){ // 如果是第一次启动
            //var editor=pref.edit()
            //editor.putBoolean("booted",false)//标记为不是第一次启动
        }

        /********初始化存储***********/
        tinydb= TinyDB(getSharedPreferences("mailList", Context.MODE_PRIVATE))
        loadMailList() // 载入邮件
        loadMailConfig()
        /***********fab**************/
        var fab = findViewById<Fab>(R.id.fab)
        var sheetView = findViewById<View>(R.id.fab_sheet)
        var overlay = findViewById<View>(R.id.overlay)
        var sheetColor = getResources().getColor(R.color.fab_sheet_color)
        var fabColor = getResources().getColor(R.color.fab_color)
        val materialSheetFab = MaterialSheetFab(fab, sheetView, overlay, sheetColor, fabColor)

        var configButton=findViewById<View>(R.id.fab_sheet_item_config)
        var creatButton=findViewById<View>(R.id.fab_sheet_item_add)
        var helpButton=findViewById<View>(R.id.fab_sheet_item_help)
        var serverChanKey=findViewById<View>(R.id.fab_sheet_item_server_chan)
        configButton.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                selectMailType()
            }})
        creatButton.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                getMailAddress()
            }})
        helpButton.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                showHelpPage()
            }})
        serverChanKey.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                configServerChanKey()
            }})

        /*********************************************************************************listview*****************/
        mDynamicListView=findViewById<DynamicListView>(R.id.dynamiclistview) // 找到listview
        if(mailList.isNotEmpty()) // 不为空才初始化，防止空报错
            initListView()

        /****************************************************************************serverchan****************************/
        var serverChan=findViewById<ConstraintLayout>(R.id.serverchan_layout)
        var serverChanName=serverChan.findViewById<TextView>(R.id.textView_receive_mail)
        var serverChanButton=serverChan.findViewById<SwitchButton>(R.id.switch_button)
        serverChanName.setText(this.getResources().getString(R.string.serverchan))
        serverChanButton.setOnCheckedChangeListener { buttonView, isChecked ->
            serverChanSwitch=isChecked
        }
        if(serverChanSwitch)
            serverChanButton.setChecked(true)
        else
            serverChanButton.setChecked(false)

        /***********************************************************************************启动服务**********************/
        mySmsObserver= MySmsObserver(this, Handler())
        mySmsObserver.registerSMSObserver()
//        smsVerifyCatcher= SmsVerifyCatcher(this@MainActivity,object : OnSmsCatchListener<String>{
//            override fun onSmsCatch(message: String?) {
//                mailObject="手机短信"
//                mailBody=message
//                sendMails()
//            }
//        })
//
//        smsVerifyCatcher.onStart()

    }//onCreat

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        mySmsObserver.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onPause() {
        saveMailList() // 保存数据,当活动的内存被清理的时候，也就是list清空之前
        saveMailConfig()
        super.onPause()
    }

    /*******************************************数据的存储操作************************************************/
    /**保存发送邮箱的设置*/
    fun saveMailConfig(){
        var editor=getSharedPreferences("mailConfig", Context.MODE_PRIVATE).edit() // 创建一个可以用来存放键值的editor,保存的文件名为mailConfig
        editor.putString("serverName",serverName)
        editor.putString("sendMialAddress",sendMialAddress)
        editor.putString("mailPassword",mailPassword)
        editor.putString("serverPort", serverPort)
        editor.putBoolean("serverChan", serverChanSwitch)
        editor.putString("serverChanKey", serverChanKey)
        editor.apply() // 提交保存
    }

    /**读取发送邮箱的设置*/
    fun loadMailConfig(){
        var pref=getSharedPreferences("mailConfig", Context.MODE_PRIVATE) // 把文件读取到pref
        serverName=pref.getString("serverName",null) // 如果该值不存在就返回null
        sendMialAddress=pref.getString("sendMialAddress",null)
        mailPassword=pref.getString("mailPassword",null)
        serverPort=pref.getString("serverPort", null)
        serverChanSwitch=pref.getBoolean("serverChan", true)
        serverChanKey=pref.getString("serverChanKey", "nothing") as String
    }

    /**保存邮件列表*/
    fun saveMailList(){
        var list=mailList//防止mailList被其他线程修改
        tinydb.putListObject("mailList", list as ArrayList<Any>)
    }

    /**加载邮件列表*/
    fun loadMailList(){
        mailList= tinydb.getListObject("mailList", MailItem::class.java) as List<MailItem>
    }


/**************************************************************************dialog操作函数******************************************/
    fun configServerChanKey(){
    MaterialDialog(this).show {
        title(R.string.serverchantitle) // 标题
        input(allowEmpty = false,hintRes = R.string.serverchanhint, prefill= serverChanKey) { dialog, text -> // 加载自定义布局
            serverChanKey=getInputField().text.toString() // 获取输入的邮件地址
            tryHttp("http://sc.ftqq.com/"+serverChanKey+".send?text=Connection test&desp=August has connect to serverchan "+getDate())
            Toast.makeText(this@MainActivity,R.string.checkserverchan, Toast.LENGTH_LONG).show()
        }
        positiveButton(R.string.submit)
        negativeButton(android.R.string.cancel)
        cornerRadius(10f) // 角半径
    }
    }

    fun showHelpPage(){
        MaterialDialog(this@MainActivity, BottomSheet()).show {
            customView(R.layout.help_view, scrollable = true, horizontalPadding = true) // 布局文件必须放在最前面，不然后面findViewById会返回空
            cornerRadius(20f) // 角半径
            title(R.string.helptitle)
            var mMarkdownView = findViewById<MarkdownView>(R.id.markdown_view)
            var css=Github()
            css.addRule("body", "padding: 0in")
            mMarkdownView.addStyleSheet(css)
            //mMarkdownView.loadMarkdown("**MarkdownView**")
            mMarkdownView.loadMarkdownFromAsset("help.md")
//             mMarkdownView.loadMarkdownFromFile(new File())
//             mMarkdownView.loadMarkdownFromUrl("url")
        }
    }

    fun getMailAddress(){ // 添加邮件
        MaterialDialog(this).show {
            title(R.string.putinmail) // 标题
            input(allowEmpty = false,hintRes = R.string.hint,inputType= InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS) { dialog, text -> // 加载自定义布局
                var mailAddress=getInputField().text.toString() // 获取输入的邮件地址
                if(MainActivity.mailList.isNotEmpty()) {// 如果此时列表不为空就直接用listview的方法添加
                    var haveSameMail=false
                    for (i in 0 until MainActivity.mailList.size){
                        if(MainActivity.mailList[i].mail==mailAddress){
                            haveSameMail=true
                            break
                        }
                    }
                    if(!haveSameMail)
                        MainActivity.mDynamicListView.insert(0, MailItem(mailAddress,true)) // 更新列表
                }
                else{ // 当列表为空表示vistview还没有被创建,所以手动添加列表并创建listview
                    MainActivity.mailList +=MailItem(mailAddress,true) // 初始化列表
                    initListView() // 初始化listview
                }
            }
            positiveButton(R.string.submit)
            negativeButton(android.R.string.cancel)
            cornerRadius(10f) // 角半径
        }
    }

    fun configMail(id:Int){ // 修改邮件
        MaterialDialog(this@MainActivity, BottomSheet()).show {
            customView(R.layout.custom_view, scrollable = true, horizontalPadding = true) // 布局文件必须放在最前面，不然后面findViewById会返回空
            cornerRadius(20f) // 角半径
            loadMailConfig()
            var checkBox=findViewById<CheckBox>(R.id.showPassword)
            var passwordEdit=findViewById<EditText>(R.id.password)
            var serverEdit=findViewById<EditText>(R.id.server_name)
            var portEdit=findViewById<EditText>(R.id.server_port)
            var mailEdit=findViewById<EditText>(R.id.mail_address)
            if(id!=0){
                serverName= mailTypeServer[id]
                serverPort= mailTypePort[id]
            }else{
                serverName=null
                serverPort=null
            }
            if (serverName!=null)
                serverEdit.setText(serverName)
            if(serverPort!=null)
                portEdit.setText(serverPort)
            if (sendMialAddress!=null)
                mailEdit.setText(sendMialAddress)
            if (mailPassword!=null)
                passwordEdit.setText(mailPassword)
            checkBox.setOnClickListener(object : View.OnClickListener{
                override fun onClick(v: View?) {
                    if(checkBox.isChecked)
                        passwordEdit.setTransformationMethod(HideReturnsTransformationMethod.getInstance())
                    else
                        passwordEdit.setTransformationMethod(PasswordTransformationMethod.getInstance())
                }})
            var testConnectButton=findViewById<Button>(R.id.test_connect)
            testConnectButton.setOnClickListener(object : View.OnClickListener{
                override fun onClick(v: View?) {
                    serverName=serverEdit.text.toString()
                    serverPort=portEdit.text.toString()
                    sendMialAddress=mailEdit.text.toString()
                    mailPassword=passwordEdit.text.toString()
                    sendMail(sendMialAddress as String,true)
                }})
            title(R.string.configmail)
            positiveButton(R.string.save) { dialog ->
                /**按下保存键的操作*/
                serverName=serverEdit.text.toString()
                serverPort=portEdit.text.toString()
                sendMialAddress=mailEdit.text.toString()
                mailPassword=passwordEdit.text.toString()
                saveMailConfig() // 确认之后存放数据
            }
            negativeButton(android.R.string.cancel)

        }
    }

    fun selectMailType(){
        val dialog=MaterialDialog(this).show {
            listItemsSingleChoice(items = mailType, initialSelection = 1){ dialog, index, text ->
                // Invoked when the user selects an item
                var i=0
                while (!dialog.isItemChecked(i)){
                    i++
                }
                configMail(i)
            }
            positiveButton(R.string.submit)
        }

    }

/***********************************************ListView操作函数*****************************************************/
    /**控制删除操作的类，OnDismissCallback是简单的删除处理，见官方教程*/
    private inner class MyOnDismissCallback internal constructor(private val mAdapter: com.nhaarman.listviewanimations.ArrayAdapter<MailItem>) :
        OnDismissCallback {
        private var mToast: Toast? = null
        /**这个方法在删除操作进行时会被调用*/
        override fun onDismiss(listView: ViewGroup, reverseSortedPositions: IntArray) {
            for (position in reverseSortedPositions) {
                mAdapter.remove(position) // 删除数据
            }
            if (mToast != null) {
                mToast!!.cancel()
            }
            mToast = Toast.makeText(
                this@MainActivity,
                this@MainActivity.getResources().getString(R.string.hintdeleteaddress), // 被删除的时候显示这句提示
                Toast.LENGTH_LONG
            )
            mToast!!.show()
        }
    }

    /**控制添加的类*/
    private inner class MyOnItemClickListener internal constructor(private val mListView: DynamicListView) :
        AdapterView.OnItemClickListener {

        override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
            /**item点击之后的操作在这里进行，下面这个例子是在点击的位置添加一个item
             *mListView.insert(position, MailItem("new",true))*/
        }
    }

    /**控制移动的类*/
    private inner class MyOnItemMovedListener internal constructor(private val mAdapter: ArrayAdapter<MailItem>) :
        OnItemMovedListener {

        private var mToast: Toast? = null

        override fun onItemMoved(originalPosition: Int, newPosition: Int) {
            if (mToast != null) {
                mToast!!.cancel()
            }
            /**当item被移动的时候执行的代码*/
        }
    }

    /**操作长按*/
    private inner class MyOnItemLongClickListener internal constructor(val listview:DynamicListView?): AdapterView.OnItemLongClickListener{
        val mListView:DynamicListView?
        init {
            mListView=listview
        }
        override fun onItemLongClick(
            parent: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long
        ): Boolean {
            if (mListView != null) {
                //长按的操作，下面是长按拖动的例子
                 mListView.startDragging(position - mListView.getHeaderViewsCount())
            }
            return true
        }
    }

    fun initListView(){
        var myAdapter=MailAdapter(this,mailList as List<MailItem>,R.layout.itemrow_gripview) // 创建自己的适配器
        var simpleSwipeUndoAdapter=SimpleSwipeUndoAdapter(myAdapter, this@MainActivity, MyOnDismissCallback(myAdapter)) // 为适配器添加删除操作
        val animAdapter = AlphaInAnimationAdapter(simpleSwipeUndoAdapter) // 添加显示动画
        animAdapter.setAbsListView(mDynamicListView) // 设置适配器
        assert(animAdapter.viewAnimator != null)
        animAdapter.viewAnimator!!.setInitialDelayMillis(1) // 设置取消操作延迟时间
        mDynamicListView.setAdapter(animAdapter) // 设置适配器

        mDynamicListView.enableDragAndDrop() // 使能拖拽
        //mDynamicListView.setDraggableManager(TouchViewDraggableManager(R.id.textView_receive_mail)) // 可以拖拽的位置,这里我用的长按拖拽这里就注释掉了
        mDynamicListView.setOnItemMovedListener(MyOnItemMovedListener(myAdapter as ArrayAdapter<MailItem>)) // 移动监听
        mDynamicListView.setOnItemLongClickListener(MyOnItemLongClickListener(mDynamicListView)) // 长按操作

        mDynamicListView.enableSimpleSwipeUndo() // 使能撤销
        mDynamicListView.setOnItemClickListener(MyOnItemClickListener(mDynamicListView))
    }

    /********************************************************data**********************************************/
    fun getDate():String{
        var date = Date(System.currentTimeMillis())
        return date.toString()
    }

    /**************************************************************************************************************/
    companion object{
        /**保存邮箱配置信息的变量*/
        lateinit var mContext:Context
        var mHttpClient = OkHttpClient()
        var serverChanSwitch=true
        var serverChanKey:String="nothing"
        val mailType=listOf("others", "Gmail", "QQ", "Outlook", "163", "Sina")
        val mailTypeServer= listOf("0", "smtp.gmail.com", "smtp.qq.com", "smtp.office365.com", "smtp.163.com", "smtp.sina.com")
        val mailTypePort= listOf("0", "465", "465", "587", "465", "465")
        var serverName:String?=null
        var serverPort:String?=null
        var sendMialAddress:String?=null
        var mailPassword:String?=null
        lateinit var mailList:List<MailItem> // 保存发送邮箱列表
        lateinit var  mDynamicListView:DynamicListView // listview
        lateinit var tinydb:TinyDB // 不能放在外面初始化
        var mailObject:String?=null
        var mailBody:String?=null
        fun toggleSwitch(name:String,isChecked:Boolean){
            for (i in 0 until mailList.size){
                if (mailList[i].mail==name){
                    mailList[i].choosed=isChecked
                    break
                }
            }
            //mailList[position].choosed=isChecked // swich button取反后对应的存储值取反
        }
        /**********************mail******************/
        fun sendMail(aimAddress:String, hint:Boolean) {
            MaildroidX.Builder()
                .smtp(if(serverName!=null) serverName as String else " ")
                .smtpUsername(if(sendMialAddress!=null) sendMialAddress as String else " ")
                .smtpPassword(if(mailPassword!=null) mailPassword as String else " ")
                .smtpAuthentication(true)
                .port(if(serverPort!=null) serverPort as String else " ")
                .type(MaildroidXType.HTML)
                .to(aimAddress)
                .from(if(sendMialAddress!=null) sendMialAddress as String else " ")
                .subject(if(mailObject!=null) mailObject as String else "Test")
                .body(if(mailBody!=null) mailBody as String else "August connect successful")
//            .attachment("")
//            //or
//            .attachments() //List<String>
            .onCompleteCallback(object : MaildroidX.onCompleteCallback{
                override val timeout: Long = 3000
                override fun onSuccess() {
                    if(hint)
                        Toast.makeText(MainActivity.mContext, mContext.getString(R.string.connectsuccessful), Toast.LENGTH_SHORT).show()
                }
                override fun onFail(errorMessage: String) {
                    if(hint)
                        Toast.makeText(MainActivity.mContext, mContext.getString(R.string.connectfail), Toast.LENGTH_SHORT).show()
                }
            })
                .mail()
        }
        fun sendMails(){
            if(serverName!=null && sendMialAddress!=null && mailPassword!=null)
            {
                if (mailList!=null){
                    for(address in mailList)
                        if (address.choosed)
                            sendMail(address.mail,false)
                }
            }
            if(serverChanSwitch){
                tryHttp("http://sc.ftqq.com/"+serverChanKey+".send?text="+mailObject+"&desp="+ mailBody)
            }
        }

        /*********************************okhttp*******************************/
        fun tryHttp(url:String):String{
            var successful=false
            var date=" "
            object : Thread() {
                override fun run() {
                    var request=Request.Builder()
                        .url(url)
                        .build()
                    var response = mHttpClient.newCall(request).execute()
                    //successful=response.body.toString().contains("e")
                    date= response.body.toString()
                }
            }.start()
            return date
        }
    }
}


/**每个item中存储的数据结构*/
class MailItem(mails:String="nothing",chooseds:Boolean=true) {
    var mail:String=mails
    var choosed:Boolean=chooseds
}

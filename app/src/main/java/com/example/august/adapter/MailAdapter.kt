package com.example.august.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.august.MailItem
import com.suke.widget.SwitchButton

import com.nhaarman.listviewanimations.ArrayAdapter
import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView
import com.nhaarman.listviewanimations.itemmanipulation.dragdrop.OnItemMovedListener
import com.nhaarman.listviewanimations.itemmanipulation.dragdrop.TouchViewDraggableManager
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.undo.SimpleSwipeUndoAdapter
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.undo.UndoAdapter
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter
import android.widget.AdapterView
import androidx.annotation.NonNull
import android.widget.TextView
import android.widget.Toast
import com.example.august.MainActivity
import android.widget.CompoundButton



/**
 * 1.邮件地址显示的listView适配器
 * 2.传入的3个参数分别是context 要显示的数据 子项布局文件的ID
 * 3.更改结构体时（结构体名还是MailItem）只需要更改getView函数*/
class MailAdapter internal  constructor(private val mContext:Context, private var datas:List<MailItem>,private val layoutId:Int):
        ArrayAdapter<MailItem>(datas), UndoAdapter {

    /**用来存放view中控件id的类*/
    class ViewHolder{
        var mailName:TextView?=null // 用来存放text的id
        var switchButton:SwitchButton?=null // 存放button的id
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).hashCode().toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    /**得到取消删除的那个对话框*/
    override fun getUndoView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView // 如果已经在缓存中就直接返回
        if (view == null) { // 如果不再缓存中就加载
            view = LayoutInflater.from(mContext).inflate(com.example.august.R.layout.undo_row, parent, false)
        }
        return view as View
    }

    /**得到取消按键的id*/
    override fun getUndoClickView(view: View): View {
        return view.findViewById(com.example.august.R.id.undo_row_undobutton)
    }

    /**得到要加载的view*/
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var viewBeLoad=convertView // 获得即将被加载的view
        var viewHolder=ViewHolder() // 用来存放view的一些信息
        var nowData=getItem(position) // 得到要存入view的数据

        /**这里如果用回收的convertView来创建新的view的话选择按钮会出错，现象为
         * 1. 在添加新项的时候本来照代码里的逻辑初始化是选中状态，但是实际初始化状态不确定
         * 2. 在拖动的时候有些按钮的选中状态也会被改变
         * 如果谁能看到这里的代码，真诚希望你能帮忙解决我的疑惑，万分感激*/

//        if (viewBeLoad==null){ // 如果这个view里是空的，就是从未被加载过
            viewBeLoad=LayoutInflater.from(mContext).inflate(layoutId,parent,false) // 得到这个view
            viewHolder.mailName=viewBeLoad.findViewById(com.example.august.R.id.textView_receive_mail) // 获得这个view的text的id
            viewHolder.switchButton=viewBeLoad.findViewById(com.example.august.R.id.switch_button) // 获得button的id
//            viewBeLoad.tag=viewHolder
//        }else{ // 如果已经被加载过
//            viewHolder=viewBeLoad!!.tag as ViewHolder // 将从view中取出的数据存入viewHolder
//        }
        viewHolder.switchButton!!.setOnCheckedChangeListener { buttonView, isChecked ->

            MainActivity.toggleSwitch(nowData.mail,isChecked) // 点击反转数据
        }

        viewHolder.mailName!!.text=nowData!!.mail // 将当前的mail信息放入text
        if(nowData.choosed){
            viewHolder.switchButton!!.setChecked(true) // 如果选中就显示选中
        }else{
            viewHolder.switchButton!!.setChecked(false)
        }

        return viewBeLoad as View // 将非空的viewBeLoad返回，下次这个item进入屏幕作为convertView传入
    } // getView
} // class


/************官方例程************/
//    class MyListAdapter internal constructor(private val mContext: Context) :
//        ArrayAdapter<String>(), UndoAdapter {
//
//        init {
//            for (i in 0..19) {
//                add(mContext.getString(R.string.row_number, i))
//            }
//        }
//
//        override fun getItemId(position: Int): Long {
//            return getItem(position).hashCode().toLong()
//        }
//
//        override fun hasStableIds(): Boolean {
//            return true
//        }
//
//        override fun getView(position: Int, convertView: View, parent: ViewGroup): View {
//            var view: View? = convertView
//            if (view == null) {
//                view = LayoutInflater.from(mContext)
//                    .inflate(R.layout.list_row_dynamiclistview, parent, false)
//            }
//
//            (view!!.findViewById(R.id.list_row_draganddrop_textview) as TextView).text =
//                getItem(position)
//
//            return view
//        }
//
//        override fun getUndoView(position: Int, convertView: View?, parent: ViewGroup): View {
//            var view = convertView
//            if (view == null) {
//                view = LayoutInflater.from(mContext).inflate(R.layout.undo_row, parent, false)
//            }
//            return view
//        }
//
//        override fun getUndoClickView(view: View): View {
//            return view.findViewById(R.id.undo_row_undobutton)
//        }
//    }
//
//    class MyOnItemLongClickListener internal constructor(private val mListView: DynamicListView?) :
//        AdapterView.OnItemLongClickListener {
//
//        override fun onItemLongClick(
//            parent: AdapterView<*>,
//            view: View,
//            position: Int,
//            id: Long
//        ): Boolean {
//            mListView?.startDragging(position - mListView.headerViewsCount)
//            return true
//        }
//    }





///**
// * 重写ArrayAdapter实现功能
// * 1.将数据放入view
// * 2.利用缓存数据
// * 3.利用tag*/
////class MailAdapter(items: ArrayList<MailItem>, ctx: Context) :
////    ArrayAdapter<MailItem>(ctx, R.layout.itemrow_gripview, items) {
//class MailAdapter(items: ArrayList<MailItem>, ctx: Context) :
//    ArrayAdapter<MailItem>(ctx, com.example.august.R.layout.itemrow_gripview, items) {
//
//    //用来存放view中控件id的类
//    class ViewHolder{
//        var mailName:TextView?=null //用来存放text的id
//        var switchButton:SwitchButton?=null//存放button的id
//    }
//
//    //当有新的item要进入屏幕的时候被调用
//    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
//        var viewBeLoad=convertView//获得即将被加载的view
//        var viewHolder=ViewHolder()//用来存放view的一些信息
//        var nowData=getItem(position)//得到要存入view的数据
//
//        if (viewBeLoad==null){//如果这个view里是空的，就是从未被加载过
//            viewBeLoad=LayoutInflater.from(context).inflate(com.example.august.R.layout.itemrow_gripview,parent,false)//得到这个view
//            viewHolder.mailName=viewBeLoad.findViewById(com.example.august.R.id.textView_receive_mail)//获得这个view的text的id
//            viewHolder.switchButton=viewBeLoad.findViewById(com.example.august.R.id.switch_button)//获得button的id
//            viewBeLoad.tag=viewHolder
//            viewHolder.mailName!!.text=nowData!!.mail//将当前的mail信息放入text
//            if(nowData.choosed){
//                viewHolder.switchButton!!.setChecked(true)//如果选中就显示选中
//            }else{
//                viewHolder.switchButton!!.setChecked(false)
//            }
//        }else{//如果已经被加载过
//            viewHolder=viewBeLoad!!.tag as ViewHolder//将从view中取出的数据存入viewHolder
//            viewHolder.mailName!!.text=nowData!!.mail//将当前的mail信息放入text
//            if(nowData.choosed){
//                viewHolder.switchButton!!.setChecked(true)//如果选中就显示选中
//            }else{
//                viewHolder.switchButton!!.setChecked(false)
//            }
//        }
//        return viewBeLoad as View//将非空的viewBeLoad返回，下次这个item进入屏幕作为convertView传入
//    }//getView
//
//    override fun hasStableIds(): Boolean {
//        return true
//    }
//
//}//class
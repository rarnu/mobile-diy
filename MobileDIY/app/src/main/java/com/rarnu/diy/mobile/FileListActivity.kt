package com.rarnu.diy.mobile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.SimpleAdapter
import android.widget.SimpleCursorAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.rarnu.diy.mobile.databinding.ActivityFilelistBinding
import com.rarnu.diy.mobile.util.globalFileUtil
import java.io.File

class FileListActivity: AppCompatActivity() {

    private lateinit var binding: ActivityFilelistBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilelistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fileList = globalFileUtil.cardFilePath.list()?.toList() ?: listOf()
        binding.lvFiles.adapter = CardFileAdapter(fileList)
        binding.lvFiles.setOnItemClickListener { _, _, position, _ ->
            setResult(RESULT_OK, Intent().apply {
                putExtra("path", File(globalFileUtil.cardFilePath, fileList[position]).absolutePath)
            })
            finish()
        }

        if (fileList.isNotEmpty()) {
            binding.tvNoCard.visibility = View.GONE
        }
    }

    inner class CardFileAdapter(private val list: List<String>): BaseAdapter() {

        override fun getCount(): Int = list.size

        override fun getItem(position: Int): Any = list[position]

        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var v = convertView
            if (v == null) {
                v = layoutInflater.inflate(R.layout.item_cardname, parent, false)
            }
            var holder = v?.tag as? CardFileHolder
            if (holder == null) {
                holder = CardFileHolder()
                holder.text = v?.findViewById(R.id.tvItem)
                v?.tag = holder
            }
            holder.text?.text = list[position]
            return v!!
        }

        inner class CardFileHolder {
            var text: TextView? = null
        }

    }

}
package com.example.smarthome.ActivityStanze

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.example.smarthome.R
import com.example.smarthome.TAG
import com.example.smarthome.viewModel.HomeStatModel
import kotlinx.android.synthetic.main.frag_home.*


class fragHomeStanze : Fragment() {

    //dichiarazione ViewModel
    val model: HomeStatModel by activityViewModels()


    internal var callback: onAddRoomPressedListener? = null

    internal var callback2: onRoomSelectedListener? = null



    fun setOnAddRoomPressedListener(callback: onAddRoomPressedListener) {
        this.callback = callback
    }


    fun setOnRoomSelectedListener(callback: onRoomSelectedListener) {
        this.callback2 = callback
    }

    //interfaccia per callback su click della stanza
    interface onRoomSelectedListener{
        fun onRoomSelected()
    }

    // This interface can be implemented by the Activity, parent Fragment,
    // or a separate test implementation.
    interface onAddRoomPressedListener {
        fun onAddRoomPressed()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.frag_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //lista di ADDROOMTEXT
        val AddRoomStanze = mutableListOf(addroomStanza2,addroomStanza1,addroomStanza3)

        //lista colori
        val listaColori = mutableListOf("#F2B6CC","#BAD0FF","#FFC4B1")

        //lista di Framelayout
        val FrameLayoutList = mutableListOf(stanza2,stanza1,stanza3)


        //lista di TextViewStanza
        val TextViewStanze = mutableListOf(stanza2Textview,stanza1Textview,stanza3Textview)

        //lista container stanze
        val StanzeContainer = mutableListOf(stanza2,stanza1,stanza3)

        var indice = model.Stanze?.indices

        if (indice!!.endInclusive != 0) {
            Log.d(TAG,"FRAGHOME---> ${model.Stanze.toString()}")

            for ( i in indice!!){
                //imposta il colore del riquadro stanza
                FrameLayoutList.get(i)
                //nasconde la scritta addRoom se esistente
                AddRoomStanze.get(i).isVisible = false
                //imposta nome stanza nel TextView
                TextViewStanze.get(i).setText(model.Stanze?.get(i)?.nome.toString())
                //imposta il listener
                TextViewStanze.get(i).setOnClickListener{
                    callback2?.onRoomSelected()
                }
                StanzeContainer.get(i).setOnClickListener{
                    callback2?.onRoomSelected()
                }
            }
        }

        addroomStanza1.setOnClickListener {
            callback?.onAddRoomPressed()
        }
        addroomStanza2.setOnClickListener {
            callback?.onAddRoomPressed()
        }
        addroomStanza3.setOnClickListener {
            callback?.onAddRoomPressed()
        }

    }

}
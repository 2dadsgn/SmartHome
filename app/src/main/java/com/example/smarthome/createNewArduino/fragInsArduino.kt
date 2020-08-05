package com.example.smarthome.createNewArduino

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.smarthome.R
import com.example.smarthome.viewModel.viewmodel
import kotlinx.android.synthetic.main.fragment_frag_ins_arduino.*


/**
 * A simple [Fragment] subclass.
 * Use the [fragInsArduino.newInstance] factory method to
 * create an instance of this fragment.
 */
class fragInsArduino : Fragment() {



    internal var callback: OnNextPressedListener? = null

    val model: viewmodel by activityViewModels()


    fun setOnNextPressedListener(callback: OnNextPressedListener) {
        this.callback = callback
    }

    // This interface can be implemented by the Activity, parent Fragment,
    // or a separate test implementation.
    interface OnNextPressedListener {
        fun onNextPressed()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_frag_ins_arduino, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        nextButton.setOnClickListener {
            model.setArduinocode(textViewArduinoCode.text.toString())
            Log.d("MainAct",model.ArduinoCode)
            callback?.onNextPressed()
        }
    }
}
package com.example.weatherui.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import com.example.weatherui.R
import com.example.weatherui.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tomorrow.setOnClickListener { changeWeatherToTomorrow() }
    }

    private fun changeWeatherToTomorrow() {
        binding.tomorrow.setTextColor(requireActivity().getColor(R.color.textColor))
        binding.today.setTextColor(Color.parseColor("#D6996B"))

        changeIndicatorDotPosition(R.id.tomorrow)
    }

    private fun changeIndicatorDotPosition(viewId: Int) {
        val constraintSet = ConstraintSet()
        constraintSet.clone(binding.parentLayout)
        constraintSet.connect(R.id.indicator, ConstraintSet.START, viewId, ConstraintSet.START, 0)
        constraintSet.connect(R.id.indicator, ConstraintSet.END, viewId, ConstraintSet.END, 0)
        constraintSet.applyTo(binding.parentLayout)
    }
}
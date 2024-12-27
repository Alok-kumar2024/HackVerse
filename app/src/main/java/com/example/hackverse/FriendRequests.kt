package com.example.hackverse

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.hackverse.databinding.FragmentFriendRequestsBinding

class FriendRequests : Fragment() {

    private var _binding : FragmentFriendRequestsBinding? =null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment



        _binding = FragmentFriendRequestsBinding.inflate(inflater,container,false)



        return binding.root
    }


}
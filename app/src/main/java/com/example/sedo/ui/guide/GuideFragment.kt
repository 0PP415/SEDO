package com.example.sedo.ui.guide

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sedo.R
import com.example.sedo.databinding.FragmentGuideBinding
import com.example.sedo.databinding.ItemGuideBinding

data class SymbolItem(
    val iconResId: Int,
    val name: String,
    val desc: String
)

class GuideAdapter(private val items: List<SymbolItem>) : RecyclerView.Adapter<GuideAdapter.GuideViewHolder>() {
    inner class GuideViewHolder(private val binding: ItemGuideBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SymbolItem) {
            binding.ivSymbolIcon.setImageResource(item.iconResId)
            binding.tvSymbolName.text = item.name
            binding.tvSymbolDesc.text = item.desc
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuideViewHolder {
        val binding = ItemGuideBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GuideViewHolder(binding)
    }
    override fun onBindViewHolder(holder: GuideViewHolder, position: Int) {
        holder.bind(items[position])
    }
    override fun getItemCount() = items.size
}

class GuideFragment : Fragment(R.layout.fragment_guide) {

    private var _binding: FragmentGuideBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentGuideBinding.bind(view)

        setupToolbar()
        setupRecyclerView()
    }

    private fun setupToolbar() {
        val navController = findNavController()
        val drawerLayout = requireActivity().findViewById<DrawerLayout>(R.id.drawer_layout)
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.homeFragment, R.id.closetFragment, R.id.guideFragment), drawerLayout)
        binding.toolbarGuide.setupWithNavController(navController, appBarConfiguration)
    }

    private fun setupRecyclerView() {
        val realGuideData = listOf(
            SymbolItem(
                android.R.drawable.ic_menu_info_details,
                "물세탁 가능",
                "세탁기 사용이 가능하며, 세탁 시 적정 물의 온도를 준수해 주세요."
            ),
            SymbolItem(
                android.R.drawable.ic_menu_info_details,
                "물세탁 금지",
                "물세탁을 할 수 없으며, 수축이나 변형이 일어날 수 있으니 주의하세요."
            ),
            SymbolItem(
                android.R.drawable.ic_menu_info_details,
                "표백제 사용 금지",
                "염소계 및 산소계 표백제를 사용하여 옷을 탈색하거나 하얗게 만들 수 없습니다."
            ),
            SymbolItem(
                android.R.drawable.ic_menu_info_details,
                "건조기 사용 금지",
                "고온의 기계 건조 시 옷감이 줄어들 수 있으므로 건조기 사용을 피해주세요."
            ),
            SymbolItem(
                android.R.drawable.ic_menu_info_details,
                "그늘에 뉘어서 건조",
                "옷의 변형과 색바램을 막기 위해 햇빛이 없는 그늘에서 평평하게 뉘어서 말리세요."
            ),
            SymbolItem(
                android.R.drawable.ic_menu_info_details,
                "다림질 가능",
                "원단에 맞는 적정 온도(보통 140~160도)를 유지하여 다림질할 수 있습니다."
            ),
            SymbolItem(
                android.R.drawable.ic_menu_info_details,
                "드라이클리닝",
                "석유계나 전문 용제를 사용한 세탁소 드라이클리닝이 필요한 의류입니다."
            )
        )

        binding.rvGuide.layoutManager = LinearLayoutManager(requireContext())
        binding.rvGuide.adapter = GuideAdapter(realGuideData)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
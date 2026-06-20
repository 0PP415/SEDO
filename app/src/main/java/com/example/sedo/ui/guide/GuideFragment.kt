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

// 1. 가이드 사전용 데이터 클래스
data class SymbolItem(
    val iconResId: Int,
    val name: String,
    val desc: String
)

// 2. 가이드 리스트 어댑터
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

// 3. 가이드 화면 프래그먼트
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
        // 실제 표준 규격에 맞춘 5대 세탁 가이드 데이터
        val realGuideData = listOf(
            SymbolItem(
                android.R.drawable.ic_menu_info_details, // ➡️ 나중에 R.drawable.ic_wash_standard 로 교체
                "물세탁 가능",
                "세탁기 사용이 가능하며, 세탁 시 적정 물의 온도를 준수해 주세요."
            ),
            SymbolItem(
                android.R.drawable.ic_menu_info_details, // ➡️ 나중에 R.drawable.ic_wash_prohibit 로 교체
                "물세탁 금지",
                "물세탁을 할 수 없으며, 수축이나 변형이 일어날 수 있으니 주의하세요."
            ),
            SymbolItem(
                android.R.drawable.ic_menu_info_details, // ➡️ 나중에 R.drawable.ic_bleach_prohibit 로 교체
                "표백제 사용 금지",
                "염소계 및 산소계 표백제를 사용하여 옷을 탈색하거나 하얗게 만들 수 없습니다."
            ),
            SymbolItem(
                android.R.drawable.ic_menu_info_details, // ➡️ 나중에 R.drawable.ic_dry_machine_prohibit 로 교체
                "건조기 사용 금지",
                "고온의 기계 건조 시 옷감이 줄어들 수 있으므로 건조기 사용을 피해주세요."
            ),
            SymbolItem(
                android.R.drawable.ic_menu_info_details, // ➡️ 나중에 R.drawable.ic_dry_shade_flat 로 교체
                "그늘에 뉘어서 건조",
                "옷의 변형과 색바램을 막기 위해 햇빛이 없는 그늘에서 평평하게 뉘어서 말리세요."
            ),
            SymbolItem(
                android.R.drawable.ic_menu_info_details, // ➡️ 나중에 R.drawable.ic_iron_high 로 교체
                "다림질 가능",
                "원단에 맞는 적정 온도(보통 140~160도)를 유지하여 다림질할 수 있습니다."
            ),
            SymbolItem(
                android.R.drawable.ic_menu_info_details, // ➡️ 나중에 R.drawable.ic_dry_clean 로 교체
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
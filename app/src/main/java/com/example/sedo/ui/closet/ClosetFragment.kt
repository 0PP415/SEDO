package com.example.sedo.ui.closet

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.fragment.app.setFragmentResult
import androidx.core.os.bundleOf
import com.bumptech.glide.Glide
import com.example.sedo.R
import com.example.sedo.data.ClothEntity
import com.example.sedo.databinding.FragmentClosetBinding
import com.example.sedo.databinding.ItemClosetGridBinding
import com.example.sedo.ui.ClosetViewModel
import kotlinx.coroutines.launch

class DatabaseClosetAdapter(
    private var items: List<ClothEntity>,
    private val onItemClick: (ClothEntity) -> Unit
) : RecyclerView.Adapter<DatabaseClosetAdapter.ClosetViewHolder>() {

    inner class ClosetViewHolder(private val binding: ItemClosetGridBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ClothEntity) {
            binding.tvClothName.text = item.name
            binding.tvClothCategory.text = "${item.category} • ${item.season}"
            binding.tvWashGuide.text = item.washGuide
            Glide.with(binding.root.context)
                .load(item.imageUri)
                .centerCrop()
                .into(binding.ivClothImage)

            binding.root.setOnClickListener { onItemClick(item) }
        }
    }

    fun updateItems(newItems: List<ClothEntity>) {
        this.items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClosetViewHolder {
        val binding = ItemClosetGridBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ClosetViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ClosetViewHolder, position: Int) = holder.bind(items[position])
    override fun getItemCount() = items.size
}

class ClosetFragment : Fragment(R.layout.fragment_closet) {

    private var _binding: FragmentClosetBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ClosetViewModel
    private lateinit var closetAdapter: DatabaseClosetAdapter
    private var allClothesList: List<ClothEntity> = emptyList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentClosetBinding.bind(view)

        viewModel = ViewModelProvider(this)[ClosetViewModel::class.java]

        // ClosetFragment.kt 의 onViewCreated 내부 어댑터 세팅 영역
        closetAdapter = DatabaseClosetAdapter(emptyList()) { cloth ->

            // ⭐️ 기초 디버깅: DB에서 방금 꺼내온 Entity 객체의 뱃속을 직접 열어봅니다!
            android.util.Log.d("SEDO_TRACKING", "[1. 출발지] 클릭한 옷 이름: ${cloth.name}")
            android.util.Log.d("SEDO_TRACKING", "[1. 출발지] Entity가 가진 V1: ${cloth.video1Id}")
            android.util.Log.d("SEDO_TRACKING", "[1. 출발지] Entity가 가진 T1: ${cloth.video1Title}")

            val bundle = Bundle().apply {
                putLong("id", cloth.id)
                putString("name", cloth.name)
                putString("imageUri", cloth.imageUri)
                putString("category", cloth.category)
                putString("season", cloth.season)
                putString("washGuide", cloth.washGuide)
                putStringArrayList("tagSymbols", ArrayList(cloth.tagSymbols))

                // ⭐️ 핵심: DB에 저장된 진짜 유튜브 ID와 제목 4개를 번들에 실어 보냅니다.
                putString("video1Id", cloth.video1Id)
                putString("video2Id", cloth.video2Id)
                putString("video1Title", cloth.video1Title)
                putString("video2Title", cloth.video2Title)
            }
            findNavController().navigate(R.id.detailFragment, bundle)
        }

        setupToolbar()
        setupRecyclerView()
        setupFilterChips()
        setupFab()
        observeDatabase()
    }

    private fun setupToolbar() {
        val navController = findNavController()
        val drawerLayout = requireActivity().findViewById<DrawerLayout>(R.id.drawer_layout)
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.homeFragment, R.id.closetFragment), drawerLayout)
        binding.toolbarCloset.setupWithNavController(navController, appBarConfiguration)
    }

    private fun setupRecyclerView() {
        binding.rvCloset.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvCloset.adapter = closetAdapter
    }

    private fun setupFilterChips() {
        binding.chipGroupCategory.setOnCheckedStateChangeListener { group, checkedIds ->
            val checkedId = checkedIds.firstOrNull()
            applyFilter(checkedId)
        }
    }

    private fun applyFilter(checkedId: Int?) {
        val filteredList = when (checkedId) {
            R.id.chip_top -> allClothesList.filter { it.category == "상의" }
            R.id.chip_bottom -> allClothesList.filter { it.category == "하의" }
            R.id.chip_outer -> allClothesList.filter { it.category == "아우터" }
            else -> allClothesList
        }
        closetAdapter.updateItems(filteredList)

        if (filteredList.isEmpty()) {
            binding.rvCloset.visibility = View.GONE
            binding.layoutEmptyCloset.visibility = View.VISIBLE
        } else {
            binding.rvCloset.visibility = View.VISIBLE
            binding.layoutEmptyCloset.visibility = View.GONE
        }
    }

    private fun observeDatabase() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.allClothes.collect { clothesList ->
                allClothesList = clothesList
                val currentCheckedId = binding.chipGroupCategory.checkedChipId
                applyFilter(currentCheckedId)
            }
        }
    }

    private fun setupFab() {
        binding.fabAddCloth.setOnClickListener {
            setFragmentResult("auto_open_gallery", bundleOf("autoOpen" to true))
            findNavController().popBackStack(R.id.homeFragment, false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}